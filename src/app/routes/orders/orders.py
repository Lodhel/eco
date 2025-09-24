import os
import tempfile

import aiofiles.os
from fastapi import APIRouter, Depends, UploadFile, File, Form
from fastapi_utils.cbv import cbv
from sqlalchemy.ext.asyncio import AsyncSession
from starlette.requests import Request
from starlette.responses import Response

from src.app.routes.orders.base import BaseRouter

from src.app.ml_modules.trees_search.base import TreesSearcher
from src.app.routes.general_models import GeneralHeadersModel
from src.app.routes.orders.models import OrderResponse
from src.app.routes.orders.response_models import order_responses

order_router = APIRouter()
order_tags = ["Создание заявки"]


@cbv(order_router)
class PlantRouter(BaseRouter):

    def __init__(self):
        super(PlantRouter, self).__init__()
        self.trees_searcher = TreesSearcher()

    @order_router.post(
        "/orders/",
        name="orders",
        summary="Определить растение по изображению",
        response_model=OrderResponse,
        responses=order_responses,
        description="POST-операция для определения растений по изображений",
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

        save_path = os.path.join("src/images", file.filename)
        tmp = tempfile.NamedTemporaryFile(delete=False, suffix=suffix)
        tmp.close()

        async with aiofiles.open(save_path, "wb") as f:
            await f.write(content)

        async with AsyncSession(self.engine, autoflush=False, expire_on_commit=False) as session:
            order = self.create_order(title, save_path)
            result = self.trees_searcher.run(content)
            await self.create_detection_results(session, order, result)
            await session.commit()

            data: dict = await self.get_data_by_response(session, order)
            return self.get_data(data)
