# Разворачивание проекта с использованием Docker

Этот проект использует **Docker** и **Docker Compose** для упрощения развертывания и управления сервисами. Следуйте этим шагам, чтобы запустить проект.

## Требования

Перед тем как начать, убедитесь, что у вас установлены следующие инструменты:

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

## Шаги для развертывания

### 1. Клонирование репозитория

Скачайте или клонируйте проект на свой локальный компьютер.

### 2. Конфигурация .env

В директории `src/` создайте файл `.env` с примером конфигурации, как показано ниже. Этот файл будет использоваться для настройки параметров вашего приложения и базы данных.

Пример `.env`:

```env
SQL_HOST=host
SQL_PORT=5432
DATABASE=postgres

POSTGRES_USER=postgres
POSTGRES_PASSWORD=password
POSTGRES_DB=postgres
DATABASE_URL=postgresql://postgres:password@host:5432/postgres 

API_TOKEN=xsdfghjkl
BASE_URL=http://127.0.0.1/
```
### 3. Запуск проекта с помощью Docker Compose

Для управления проектом используйте скрипт `manage.sh`, который предоставляет команды для запуска и остановки контейнеров. Скрипт автоматически запустит все сервисы, указанные в `docker-compose.yml`.

Команды для управления проектом:

- **Запуск всех сервисов:**
  ```bash
  ./manage.sh start
- **Остановка всех сервисов:**
  ```bash
  ./manage.sh stop
- **Перезапуск всех сервисов:**
  ```bash
  ./manage.sh restart

### 4. Adminer для администрирования БД

Контейнер для работы с базой данных через Adminer. Для доступа через веб-интерфейс используйте порт:

- **Порт:** 8110

## SQL для создания таблиц

В данном проекте используются следующие таблицы: `plants`, `orders`, и `detection_results`. Ниже приведен SQL-запрос для создания этих таблиц и вставки начальных данных в базу данных PostgreSQL.

```sql
CREATE TABLE plants (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    family VARCHAR NOT NULL,
    genus VARCHAR NOT NULL,
    growing_area VARCHAR NOT NULL,
    height VARCHAR,
    class_type VARCHAR,
    has_fruits BOOLEAN DEFAULT FALSE,
    plant_type VARCHAR NOT NULL DEFAULT 'дерево'
);

CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    image_path VARCHAR NOT NULL,
    title VARCHAR NOT NULL,
    created_at TIMESTAMP NOT NULL,
    season VARCHAR NOT NULL DEFAULT 'вегетационный'
);

CREATE TABLE detection_results (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    label VARCHAR NOT NULL,
    season VARCHAR NOT NULL DEFAULT 'вегетационный',
    name_plant VARCHAR NOT NULL DEFAULT 'Неизвестный вид',
    bbox_abs FLOAT[] NOT NULL,
    bbox_norm FLOAT[] NOT NULL,
    dry_branches_percentage FLOAT DEFAULT 0.0,
    status INTEGER DEFAULT 1,
    cond_res VARCHAR[]
);

-- Создание индекса для ускорения поиска по order_id в таблице detection_results
CREATE INDEX idx_detection_results_order_id ON detection_results(order_id);

-- Вставка данных в таблицу plants
INSERT INTO "plants" ("id", "name", "family", "genus", "growing_area", "height", "class_type", "has_fruits", "plant_type") VALUES
(2, 'дуб', 'Буковые', 'Quercus', 'Центральная Россия', NULL, 'Листопадное', '0', 'дерево'),
(3, 'ель', 'Сосновые', 'Picea', 'Центральная Россия', NULL, 'Хвойное', '0', 'дерево'),
(4, 'клен', 'Маплиевые', 'Acer', 'Центральная Россия', NULL, 'Листопадное', '0', 'дерево'),
(5, 'плодовое', 'Розовые', 'Malus', 'Центральная Россия', NULL, 'Листопадное', '1', 'дерево'),
(6, 'сосна', 'Сосновые', 'Pinus', 'Центральная Россия', NULL, 'Хвойное', '0', 'дерево'),
(7, 'тополь', 'Ивовые', 'Populus', 'Центральная Россия', NULL, 'Листопадное', '0', 'дерево'),
(8, 'ясень', 'Маслиновые', 'Fraxinus', 'Центральная Россия', NULL, 'Листопадное', '0', 'дерево'),
(1, 'берёза', 'Березовые', 'Betula', 'Центральная Россия', NULL, 'Листопадное', '0', 'дерево'),
(10, 'дерен', 'Cornaceae', 'Cornus', 'Россия, Европа', NULL, NULL, '1', 'кустарник'),
(11, 'кизильник', 'Rosaceae', 'Cotoneaster', 'Европа, Азия', NULL, NULL, '1', 'кустарник'),
(12, 'лещина', 'Betulaceae', 'Corylus', 'Европа, Азия', NULL, NULL, '1', 'кустарник'),
(13, 'можжевельник', 'Cupressaceae', 'Juniperus', 'Северное полушарие', NULL, 'хвойное', '1', 'кустарник'),
(14, 'сирень', 'Oleaceae', 'Syringa', 'Европа, Азия', NULL, 'лиственное', '0', 'кустарник'),
(15, 'спирея', 'Rosaceae', 'Spiraea', 'Европа, Азия', NULL, 'лиственное', '0', 'кустарник'),
(16, 'чубушник', 'Hydrangeaceae', 'Philadelphus', 'Европа, Кавказ, Азия', NULL, 'лиственное', '0', 'кустарник'),
(9, 'Неизвестный вид', 'Неизвестное семейство', 'Неизвестный род', 'Неизвестное место', NULL, 'Неизвестное', '0', 'Неизвестное');
