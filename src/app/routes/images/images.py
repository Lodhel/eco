import os
from fastapi import APIRouter, HTTPException
from fastapi_utils.cbv import cbv
from starlette.responses import FileResponse

from src.app.routes.orders.base import BaseRouter

image_router = APIRouter()
image_tags = ["Получение изображения"]

UPLOAD_DIRECTORY = "app/images"


@cbv(image_router)
class ImageRouter(BaseRouter):

    @image_router.get(
        "/images/{filename}",
        name="get_image",
        summary="Получить изображение",
        tags=image_tags,
        description="GET-операция для получения изображения по имени файла"
    )
    async def get_image(self, filename: str):
        file_path = os.path.join(UPLOAD_DIRECTORY, filename)

        if not os.path.exists(file_path):
            raise HTTPException(status_code=404, detail="File not found")

        return FileResponse(file_path)
