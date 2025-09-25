import os
from fastapi import APIRouter, Path, HTTPException
from fastapi_utils.cbv import cbv
from loguru import logger
from starlette.requests import Request
from starlette.responses import Response
from starlette.responses import FileResponse

from src.app.routes.orders.base import BaseRouter

image_router = APIRouter()
image_tags = ["Получение изображения"]

UPLOAD_DIRECTORY = "src/images"


@cbv(image_router)
class ImageRouter(BaseRouter):

    @image_router.get(
        "/images/{filename}/",
        name="get_image",
        summary="Получить изображение",
        tags=image_tags,
        description="GET-операция для получения изображения по имени файла"
    )
    async def get(
        self,
        request: Request,
        response: Response,
        filename: str = Path(
            ...,
            title="название файла",
            description="название файла",
            example="9be15cad5969bcf020d21950673bdb51.jpg"
        )
    ):
        file_path = os.path.join(UPLOAD_DIRECTORY, filename)
        if not os.path.exists(file_path):
            raise HTTPException(status_code=404, detail="File not found")

        return FileResponse(file_path)
