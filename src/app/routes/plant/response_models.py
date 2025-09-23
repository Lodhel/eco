from pydantic import BaseModel, Field
from typing import Optional


class PlantResponseData(BaseModel):
    result: str = Field(..., description="Результат распознования растения")


class PlantResponse(BaseModel):
    data: Optional[PlantResponseData]
    success: bool
