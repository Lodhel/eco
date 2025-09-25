import datetime
import os
import uuid

import aiofiles
from sqlalchemy import select, asc, desc
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload

from src.app.models import Order, DetectionResult, Plant
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

    @staticmethod
    async def get_plant_by_name(session: AsyncSession, name: str):
        try:
            result = await session.execute(select(Plant).where(Plant.name == name))
            return result.scalar_one_or_none()
        except Exception as ex:
            return None

    @classmethod
    async def get_data_by_response_created(cls, session: AsyncSession, order: Order):
        return await cls.get_data_by_response(session, order)

    @staticmethod
    async def get_data_by_response(session: AsyncSession, plant: Plant) -> dict:
        return plant.data
