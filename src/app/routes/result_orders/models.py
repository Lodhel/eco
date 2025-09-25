from pydantic import BaseModel, Field
from typing import Optional


class PlantResponse(BaseModel):
    id: int = Field(..., description="ID растения")
    name: str = Field(..., description="Название растения")
    family: str = Field(..., description="Семейство растения")
    genus: str = Field(..., description="Род растения")
    growing_area: str = Field(..., description="Место произрастания растения")
    height: Optional[str] = Field(None, description="Высота растения")
    class_type: Optional[str] = Field(None, description="Тип растения (например, хвойное или лиственное)")
    has_fruits: bool = Field(..., description="Наличие плодов у растения")
    dry_branches_percentage: Optional[float] = Field(None, description="Процент сухих веток у растения")

    class Config:
        orm_mode = True
