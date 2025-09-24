from abc import abstractstaticmethod, abstractclassmethod


class AbstractBaseRouter:

    @abstractclassmethod
    async def get_response_by_select_rel(
        cls,
        session,
        params,
        select_rel
    ):
        pass

    @abstractstaticmethod
    def set_order_by(order_by, select_rel):
        pass

    @abstractstaticmethod
    async def get_data_by_response_created(session, instance):
        pass

    @abstractstaticmethod
    async def get_data_by_response_default(session, instance):
        pass

    @abstractstaticmethod
    async def get_data_by_response(session, instance):
        pass

    @abstractstaticmethod
    def get_data(data: dict | list):
        pass
