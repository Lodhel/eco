from fastapi import APIRouter, Depends, Path
from fastapi_utils.cbv import cbv
from sqlalchemy.ext.asyncio import AsyncSession
from starlette.requests import Request
from starlette.responses import Response

from src.app.models import DetectionResult

from src.app.routes.general_models import GeneralHeadersModel
from src.app.routes.result_orders.base import BaseRouter
from src.app.routes.result_orders.models import PlantResponse
from src.app.routes.result_orders.response_models import result_orders_responses

result_order_router = APIRouter()
result_order_tags = ["Результат заявки по дереву"]


@cbv(result_order_router)
class PersonalResultOrderRouter(BaseRouter):

    @result_order_router.get(
        "/result-orders/{result_id}/",
        name="get_result_order_by_id",
        summary="Получить анализ растения по ID",
        response_model=PlantResponse,
        responses=result_orders_responses,
        description="GET-операция для получения анализа растений по ID",
        tags=result_order_tags
    )
    async def get(
        self,
        request: Request,
        response: Response,
        result_id: int = Path(
            ...,
            title="ID анализа растения",
            description="ID анализа растения",
            example="1"
        ),
        headers: GeneralHeadersModel = Depends()
    ):
        if not await self.auth_service_client(headers.authorization_token):
            return self.make_response_by_auth_error()

        async with AsyncSession(self.engine, autoflush=False, expire_on_commit=False) as session:
            detection_result = await self.get_instance_by_id(session, DetectionResult, result_id)
            if not detection_result:
                return self.make_response_by_error_not_exists()

            plant = await self.get_plant_by_name(session, detection_result.name)
            if not plant:
                return self.make_response_by_error_not_exists()

            data: dict = await self.get_data_by_response_created(session, plant)
            data['dry_branches_percentage'] = detection_result.dry_branches_percentage
            return self.get_data(data)
