from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from src.app.config import API_TOKEN
from src.app.routes.utils import make_data_by_response


class APIAuthMIXIN:

    @classmethod
    async def auth_service_client(
        cls, api_token: str | None
    ) -> bool:
        return api_token == API_TOKEN


class MainRouterMIXIN:

    @classmethod
    def make_response_by_ok(cls):
        data: dict = {'ok': 'success'}
        result = cls.get_data(data)
        return result

    @classmethod
    def make_response_by_error_already_exists(cls):
        data: dict = {'error': 'Значение уже существует'}
        result = cls.get_data(data)
        return result

    @classmethod
    def make_response_by_error_not_exists(cls):
        data: dict = {'error': 'Инстанс не найден'}
        result = cls.get_data(data)
        return result

    @classmethod
    def make_response_by_error_not_exists_by_instance_id(cls, name: str, instance_id: int | set):
        instance_id = instance_id if isinstance(instance_id, int) else ', '.join(map(str, instance_id))
        data: dict = {'error': f'Инстанс {name} ID которого {instance_id} не найден'}
        result = cls.get_data(data)
        return result

    @classmethod
    def make_response_by_auth_error(cls):
        data: dict = {'error': 'Ошибка аунтефикации'}
        result = cls.get_data(data)
        return result

    @classmethod
    def make_response_by_parent_error(cls):
        data: dict = {'error': 'Значение родительского ID не валидно'}
        result = cls.get_data(data)
        return result

    @staticmethod
    @make_data_by_response
    def get_data(data: dict | list):
        return data


class AbstractInstanceRouterMIXIN:

    @staticmethod
    async def get_instance_by_id(session: AsyncSession, model, model_id: int):
        result = await session.execute(select(model).where(model.id == model_id))
        return result.scalar_one_or_none()

    @staticmethod
    async def get_missing_ids_by_model(session: AsyncSession, model, ids: list[int]) -> set[int] | None:
        result = await session.execute(
            select(model.id).where(model.id.in_(ids))
        )
        existing_ids = {row[0] for row in result.fetchall()}
        missing_ids = set(ids) - existing_ids
        return missing_ids if missing_ids else None
