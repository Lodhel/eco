from fastapi import APIRouter

from src.app.routes.images.images import image_router
from src.app.routes.orders.orders import order_router
from src.app.routes.result_orders.result_orders import result_order_router
from src.app.routes.statement.statement import statement_router

router = APIRouter()


router.include_router(order_router, prefix='/api/v1')
router.include_router(image_router, prefix='/api/v1')
router.include_router(result_order_router, prefix='/api/v1')
router.include_router(statement_router, prefix='/api/v1')
