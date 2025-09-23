from src.app.orm_sender.manager_sqlalchemy import ManagerSQLAlchemy
from src.app.routes.base.general_routes import GeneralBaseRouter
from src.app.routes.mixins import MainRouterMIXIN, APIAuthMIXIN, AbstractInstanceRouterMIXIN


class BaseRouter(
    AbstractInstanceRouterMIXIN,
    MainRouterMIXIN,
    APIAuthMIXIN,
    GeneralBaseRouter,
    ManagerSQLAlchemy
):
    pass
