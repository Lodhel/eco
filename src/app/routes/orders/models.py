from pydantic import BaseModel, Field
from typing import List


class DetectionResultResponse(BaseModel):
    id: int = Field(..., description="ID опредления конкретного объекта")
    label: str = Field(..., description="Метка объекта")
    name_plant: str = Field(..., description="Название объекта")
    season: str = Field(..., description="Сезон")
    bbox_abs: List[float] = Field(..., description="Нормализованные координаты")
    bbox_norm: List[float] = Field(..., description="Нормализованные координаты")
    dry_branches_percentage: float = Field(..., description="Процент сухих веток у растения")

    class Config:
        orm_mode = True


class OrderResponse(BaseModel):
    id: int = Field(..., description="ID заявки")
    image_path: str = Field(..., description="Путь к изображению")
    title: str = Field(..., description="Название заявки")
    created_at: str = Field(..., description="Дата и время создания заявки")
    results: List[DetectionResultResponse] = Field(..., description="Результаты детекции")
    statement: str = Field(..., description="url выгрузки перечетной ведомости")
    season: str = Field(..., description="Сезон")

    class Config:
        orm_mode = True
