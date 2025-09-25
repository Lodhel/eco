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
    async def make_analytics_data(session: AsyncSession, order_id: int):
        analytics_data = {
            "total_plants": 0,
            "total_trees": 0,
            "total_shrubs": 0,
            "shrub_types": {},
            "tree_types": {},
            "condition_status": {
                1: 0,
                2: 0,
                3: 0
            }
        }
        stmt = await session.execute(select(DetectionResult).where(DetectionResult.order_id == order_id))
        results = stmt.scalars().all()
        for result in results:
            analytics_data['total_plants'] += 1
            if result.label == 'дерево':
                analytics_data['total_trees'] += 1
                plant_count = analytics_data['tree_types'].get(result.name_plant, 0)
                analytics_data['tree_types'] = plant_count+1
            else:
                plant_count = analytics_data['shrub_types'].get(result.name_plant, 0)
                analytics_data['shrub_types'] = plant_count+1
                analytics_data['total_shrubs'] += 1

            analytics_data['condition_status'][result.status] += 1

        return analytics_data

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
