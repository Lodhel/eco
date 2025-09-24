from fastapi import APIRouter

from src.app.routes.orders.orders import order_router

router = APIRouter()


router.include_router(order_router, prefix='/api/v1')
