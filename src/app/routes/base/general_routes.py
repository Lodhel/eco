from abc import ABC

from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession
from starlette.responses import JSONResponse

from src.app.routes.base.abstract_routes import AbstractBaseRouter


class GeneralBaseRouter(AbstractBaseRouter, ABC):

    @classmethod
    async def get_response_by_select_rel(
        cls,
        session: AsyncSession,
        params,
        select_rel
    ):
        instances = await cls.get_instances_by_response(session, params, select_rel)
        data: list = [
            await cls.get_data_by_response_created(session, instance) for instance in instances
        ]
        return JSONResponse(content={
            'data': data,
            'meta': {
                'total': len(data),
                'counts': await cls.get_count_by_select_rel(session, select_rel)
            }
        })

    @staticmethod
    async def get_count_by_select_rel(session: AsyncSession, select_rel) -> int:
        result = await session.execute(
            select(func.count()).select_from(select_rel.subquery())
        )
        count = result.scalar_one()
        return count

    @staticmethod
    async def get_instances_by_response(session: AsyncSession, params, select_rel):
        _query = select_rel.offset(params.start)
        if params.limit:
            _query = _query.limit(params.limit)

        result = await session.execute(_query)
        return result.scalars().unique().all()
