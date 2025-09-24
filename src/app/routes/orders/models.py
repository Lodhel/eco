from pydantic import BaseModel, Field
from typing import List


class DetectionResultResponse(BaseModel):
    label: str = Field(..., description="Метка объекта")
    confidence: float = Field(..., description="Уверенность модели")
    bbox_norm: List[float] = Field(..., description="Нормализованные координаты")
    bbox_abs: List[float] = Field(..., description="Абсолютные координаты")

    class Config:
        orm_mode = True


class OrderResponse(BaseModel):
    id: int = Field(..., description="ID заявки")
    image_path: str = Field(..., description="Путь к изображению")
    title: str = Field(..., description="Название заявки")
    created_at: str = Field(..., description="Дата и время создания заявки")
    results: List[DetectionResultResponse] = Field(..., description="Результаты детекции")

    class Config:
        orm_mode = True
