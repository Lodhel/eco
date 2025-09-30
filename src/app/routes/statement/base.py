from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from src.app.models import DetectionResult
from src.app.orm_sender.manager_sqlalchemy import ManagerSQLAlchemy
from src.app.routes.base.general_routes import GeneralBaseRouter
from src.app.routes.mixins import AbstractInstanceRouterMIXIN, MainRouterMIXIN, APIAuthMIXIN


class BaseRouter(
    AbstractInstanceRouterMIXIN,
    MainRouterMIXIN,
    APIAuthMIXIN,
    GeneralBaseRouter,
    ManagerSQLAlchemy
):

    @classmethod
    async def get_records(cls, session: AsyncSession, order_id: int) -> list:
        condition_status: dict = {
            1: 'Хорошо',
            2: 'Удовлетворительно',
            3: 'Плохо'
        }
        stmt = await session.execute(select(DetectionResult).where(DetectionResult.order_id == order_id))
        results = stmt.scalars().all()
        return [
            [
                _+1,
                result.label,
                result.name_plant,
                condition_status.get(result.status, 'Удовлетворительно'),
                result.dry_branches_percentage,
                ', '.join(map(str, result.cond_res)) if result.cond_res else '',
            ] for _, result in enumerate(results)
        ]
