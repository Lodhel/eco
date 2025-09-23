from fastapi import APIRouter

from src.app.routes.plant.plant_loader import plant_router

router = APIRouter()


router.include_router(plant_router, prefix='/api/v1')
