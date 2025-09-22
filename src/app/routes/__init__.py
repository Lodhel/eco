from fastapi import APIRouter

from src.app.routes.categories.categories import category_router
from src.app.routes.garden_plots.garden_plot import garden_plot_router
from src.app.routes.plant.plant import plant_router
from src.app.routes.plant.plant_definer import plant_definer_router
from src.app.routes.regions.region import region_router
from src.app.routes.soil_types.soil_types import soil_type_router
from src.app.routes.users.users import users_router

router = APIRouter()


router.include_router(users_router, prefix='/api/v1')
router.include_router(plant_definer_router, prefix='/api/v1')

router.include_router(soil_type_router, prefix='/api/v1')
router.include_router(category_router, prefix='/api/v1')
router.include_router(plant_router, prefix='/api/v1')
# router.include_router(region_router, prefix='/api/v1')
# router.include_router(garden_plot_router, prefix='/api/v1')
