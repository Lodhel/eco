from fastapi import FastAPI
from starlette.middleware.cors import CORSMiddleware

from src.app.routes import router


def create_app() -> FastAPI:
    app = FastAPI(
        title="MVP v.0.1",
        version="0.1.0",
        description="API MVP services.",
        docs_url=None,
        redoc_url='/openapi'
    )

    app.include_router(router)
    origins = ['*']

    app.add_middleware(
        CORSMiddleware,
        allow_origins=origins,
        allow_credentials=False,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    return app


application: FastAPI = create_app()
