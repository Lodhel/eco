import datetime
import os
import uuid

import aiofiles
from sqlalchemy import select
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
                confidence=_['confidence'],
                bbox_norm=_['bbox_norm'],
                bbox_abs=_['bbox_abs']
            ) for _ in data
        ]

        session.add_all(results)
        await session.flush()

    @staticmethod
    async def save_annotated_image(content, filename):
        save_path = os.path.join("app/images", filename)
        async with aiofiles.open(save_path, "wb") as f:
            await f.write(content)

        return filename

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
