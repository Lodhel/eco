import datetime
import io
import os
import uuid

import aiofiles
from sqlalchemy import select, asc, desc
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload

from src.app.models import Order, DetectionResult
from src.app.orm_sender.manager_sqlalchemy import ManagerSQLAlchemy
from src.app.routes.base.general_routes import GeneralBaseRouter
from src.app.routes.mixins import MainRouterMIXIN, APIAuthMIXIN, AbstractInstanceRouterMIXIN


class BaseRouter(
    AbstractInstanceRouterMIXIN,
    MainRouterMIXIN,
    APIAuthMIXIN,
    GeneralBaseRouter,
    ManagerSQLAlchemy
):

    @classmethod
    def create_order(cls, title: str | None, image_path: str) -> Order:
        title: str = title if title else str(uuid.uuid4())
        return Order(
            title=title,
            image_path=image_path,
            created_at=datetime.datetime.utcnow()
        )

    @classmethod
    async def create_detection_results(cls, session: AsyncSession, order: Order, data: list):
        results = [
            DetectionResult(
                order_id=order.id,
                label=_['label'],
                season=_['season'],
                bbox=_['bbox'],
                name_plant=_['name_plant']
            ) for _ in data
        ]

        session.add_all(results)
        await session.flush()

    @staticmethod
    async def save_annotated_image(content, filename):
        filename = filename.split('/')[-1]
        save_path = os.path.join("src/images", filename)

        image_bytes = io.BytesIO()
        content.save(image_bytes, format='JPEG')
        image_bytes.seek(0)

        async with aiofiles.open(save_path, "wb") as f:
            await f.write(image_bytes.read())

        return filename

    @classmethod
    async def get_data_by_response_created(cls, session: AsyncSession, order: Order):
        return await cls.get_data_by_response(session, order)

    @staticmethod
    async def get_data_by_response(session: AsyncSession, order: Order) -> dict:
        result = await session.execute(
            select(
                Order
            ).options(
                selectinload(Order.detection_results)
            ).where(
                Order.id == order.id
            )
        )
        order = result.scalar_one_or_none()
        return order.data

    @staticmethod
    def set_order_by(order_by, select_rel):
        order_map = {
            'id_asc': asc(Order.id),
            'id_desc': desc(Order.id)
        }
        return select_rel.order_by(order_map.get(order_by)) if order_by in order_map else select_rel
