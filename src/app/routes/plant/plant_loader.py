import os
import tempfile

import aiofiles.os
from fastapi import APIRouter, Depends, UploadFile, File
from fastapi_utils.cbv import cbv
from starlette.requests import Request
from starlette.responses import Response

from src.app.routes.plant.base import BaseRouter

from src.app.ml_modules.trees_search.base import TreesSearcher
from src.app.routes.general_models import GeneralHeadersModel
from src.app.routes.plant.response_models import PlantResponse

plant_router = APIRouter()
plant_tags = ["Распознование по изображению.Растения"]


@cbv(plant_router)
class PlantRouter(BaseRouter):

    def __init__(self):
        super(PlantRouter, self).__init__()
        self.trees_searcher = TreesSearcher()

    @plant_router.post(
        "/plants/",
        name="plants",
        summary="Определить растение по изображению",
        response_model=PlantResponse,
        # responses=plant_definer_responses,
        description="POST-операция для определения растений по изображений",
        tags=plant_tags
    )
    async def post(
        self,
        request: Request,
        response: Response,
        file: UploadFile = File(...),
        headers: GeneralHeadersModel = Depends()
    ):
        if not await self.auth_service_client(headers.authorization_token):
            return self.make_response_by_auth_error()

        content = await file.read()
        suffix = os.path.splitext(file.filename)[1]
        tmp = tempfile.NamedTemporaryFile(delete=False, suffix=suffix)
        tmp_path = tmp.name
        tmp.close()

        async with aiofiles.open(tmp_path, "wb") as f:
            await f.write(content)

        try:
            result = self.trees_searcher.run(content)
            return self.get_data({'result': result})
        finally:
            await aiofiles.os.remove(tmp_path)
