import os
import tempfile

import aiofiles.os
from fastapi import APIRouter, Depends, UploadFile, File, Form, Path
from fastapi_utils.cbv import cbv
from sqlalchemy import select, delete
from sqlalchemy.ext.asyncio import AsyncSession
from starlette.requests import Request
from starlette.responses import Response

from src.app.models import Order
from src.app.routes.images.images import UPLOAD_DIRECTORY
from src.app.routes.orders.base import BaseRouter

from src.app.ml_modules.trees_search.base import TreesSearcher
from src.app.routes.general_models import GeneralHeadersModel, GeneralParams
from src.app.routes.orders.models import OrderResponse
from src.app.routes.orders.response_models import order_responses

order_router = APIRouter()
order_tags = ["Заявки"]


@cbv(order_router)
class OrderRouter(BaseRouter):

    def __init__(self):
        super(OrderRouter, self).__init__()
        self.trees_searcher = TreesSearcher()

    @order_router.get(
        "/orders/",
        name="orders",
        summary="Список заявок",
        response_model=OrderResponse,
        responses=order_responses,
        description="GET-операция для получения всех заявок",
        tags=order_tags
    )
    async def get(
        self,
        request: Request,
        response: Response,
        params: GeneralParams = Depends(),
        headers: GeneralHeadersModel = Depends()
    ):
        if not await self.auth_service_client(headers.authorization_token):
            return self.make_response_by_auth_error()

        async with AsyncSession(self.engine, autoflush=False, expire_on_commit=False) as session:
            select_rel = self.set_order_by(params.order_by, select(Order))
            return await self.get_response_by_select_rel(session, params, select_rel)

    @order_router.post(
        "/orders/",
        name="orders",
        summary="Определить растение по изображению",
        response_model=OrderResponse,
        responses=order_responses,
        description="POST-операция на создание заявки",
        tags=order_tags
    )
    async def post(
        self,
        request: Request,
        response: Response,
        file: UploadFile = File(...),
        title: str = Form(None),
        headers: GeneralHeadersModel = Depends()
    ):
        if not await self.auth_service_client(headers.authorization_token):
            return self.make_response_by_auth_error()

        content = await file.read()
        suffix = os.path.splitext(file.filename)[1]

        tmp = tempfile.NamedTemporaryFile(delete=False, suffix=suffix)
        tmp_path = tmp.name
        tmp.close()

        try:
            result: dict = self.trees_searcher.run(content)
        finally:
            await aiofiles.os.remove(tmp_path)

        async with AsyncSession(self.engine, autoflush=False, expire_on_commit=False) as session:
            save_path = await self.save_annotated_image(result['image'], tmp_path)

            order = self.create_order(title, save_path)
            session.add(order)
            await session.flush()
            await self.create_detection_results(session, order, result['preds'])
            await session.commit()

            data: dict = await self.get_data_by_response(session, order)
            return self.get_data(data)


@cbv(order_router)
class PersonalOrderRouter(BaseRouter):

    @order_router.get(
        "/orders/{order_id}/",
        name="get_order_by_id",
        summary="Получить заявку по ID",
        response_model=OrderResponse,
        responses=order_responses,
        description="GET-операция для получения с заявок по ID",
        tags=order_tags
    )
    async def get(
        self,
        request: Request,
        response: Response,
        order_id: int = Path(
            ...,
            title="ID заявки",
            description="ID заявки",
            example="1"
        ),
        headers: GeneralHeadersModel = Depends()
    ):
        if not await self.auth_service_client(headers.authorization_token):
            return self.make_response_by_auth_error()

        async with AsyncSession(self.engine, autoflush=False, expire_on_commit=False) as session:
            order = await self.get_instance_by_id(session, Order, order_id)
            if not order:
                return self.make_response_by_error_not_exists()

            data: dict = await self.get_data_by_response_created(session, order)
            return self.get_data(data)

    @order_router.delete(
        "/orders/{order_id}/",
        name="delete_order",
        summary="Удалить заявку по ID",
        description="DELETE-операция для удаления заявки по ID",
        tags=order_tags
    )
    async def delete(
        self,
        request: Request,
        response: Response,
        order_id: int = Path(..., title="ID заказа", description="ID заказа", example=1),
        headers: GeneralHeadersModel = Depends()
    ):
        if not await self.auth_service_client(headers.authorization_token):
            return self.make_response_by_auth_error()

        async with AsyncSession(self.engine, autoflush=False, expire_on_commit=False) as session:
            order = await self.get_instance_by_id(session, Order, order_id)
            if not order:
                return self.make_response_by_error_not_exists()

            file_path = os.path.join(UPLOAD_DIRECTORY, order.image_path)
            if os.path.exists(file_path):
                os.remove(file_path)

            await session.execute(delete(order))
            await session.commit()

            return self.get_data({
                "success": True,
                "message": "Заявка успешно удалён",
            })
