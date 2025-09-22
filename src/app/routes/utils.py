import secrets

from starlette.responses import JSONResponse


def make_data_by_response(func):
    def wrapper(*args, **kwargs):
        data = func(*args, **kwargs)
        if isinstance(data, JSONResponse):
            return data

        if isinstance(data, dict) and data.get('error'):
            status_code = 400
            response = {'data': data, 'success': False}
        else:
            status_code = 200
            response = {'data': data, 'success': True}

        return JSONResponse(content=response, status_code=status_code)

    return wrapper


def generate_secret_key() -> str:
    return secrets.token_urlsafe(32)
