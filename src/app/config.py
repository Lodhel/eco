import os


BASE_URL = os.getenv('BASE_URL')

DATABASE_HOST = os.getenv('SQL_HOST')
DATABASE_NAME = os.getenv('POSTGRES_DB')
DATABASE_USER = os.getenv('POSTGRES_USER')
DATABASE_PASSWORD = os.getenv('POSTGRES_PASSWORD')
DATABASE_PORT = os.getenv('SQL_PORT')

API_TOKEN = os.getenv('API_TOKEN')
