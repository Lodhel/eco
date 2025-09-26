import datetime

from fastapi import APIRouter, Path, Depends
from fastapi_utils.cbv import cbv
from starlette.requests import Request
from starlette.responses import Response
from starlette.responses import StreamingResponse

from sqlalchemy.ext.asyncio import AsyncSession


from io import BytesIO
from openpyxl import Workbook

from src.app.routes.general_models import GeneralHeadersModel
from src.app.routes.statement.base import BaseRouter
from src.app.routes.statement.create_statement import ManagerXLSX


statement_router = APIRouter()
statement_tags = ["Выгрузка перечетной ведомости"]


@cbv(statement_router)
class GreenPlantRouter(BaseRouter):
    manager_xlsx = ManagerXLSX

    @statement_router.get(
        "/statement/{order_id}/",
        name="download_statement",
        description="Выгрузка перечетной ведомости в формате xlsx",
        tags=statement_tags,
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
            records = await self.get_records(session, order_id)

        wb: Workbook = self.manager_xlsx().create(records)
        file_stream = BytesIO()
        wb.save(file_stream)
        file_stream.seek(0)
        file_name: str = f'vedomost_{datetime.date.today().month}-{datetime.date.today().year}'
        return StreamingResponse(
            file_stream,
            media_type="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            headers={
                "Content-Disposition": f"attachment; filename={file_name}.xlsx"
            },
        )
