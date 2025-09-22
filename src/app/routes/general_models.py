from typing import Optional

from fastapi import Depends, HTTPException, status, Query
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials

from pydantic import BaseModel, Field

security = HTTPBearer()


class GeneralHeadersModel:
    def __init__(self, credentials: HTTPAuthorizationCredentials = Depends(security)):
        if credentials.scheme.lower() != "bearer":
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Invalid authentication scheme"
            )
        self.authorization_token = credentials.credentials


class GeneralParams:
    def __init__(
        self,
        start: int = Query(
            default=0,
            description="С какого порядкового инстанса начинать (для пагинации)"
        ),
        limit: int = Query(
            default=20,
            description="Сколько инстансов вернуть"
        ),
        order_by: str = Query(
            default='id_asc',
            description="Сортировка (created_asc, created_desc, updated_asc, updated_desc)"
        )
    ):
        self.start = start
        self.limit = limit
        self.order_by = order_by
