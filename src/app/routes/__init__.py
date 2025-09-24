from fastapi import APIRouter

from src.app.routes.images.images import image_router
from src.app.routes.orders.orders import order_router

router = APIRouter()


router.include_router(order_router, prefix='/api/v1')
router.include_router(image_router, prefix='/api/v1')
