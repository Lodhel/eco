result_orders_responses = {
    200: {
        "description": "Успешный запрос",
        "content": {
            "application/json": {
                "examples": {
                    "success_example": {
                        "summary": "Успешный ответ",
                        "value": {
                            "data": {
                                "id": 1,
                                "name": "Береза",
                                "family": "Березовые",
                                "genus": "Betula",
                                "growing_area": "Центральная Россия",
                                "height": None,
                                "class_type": "Листопадное",
                                "has_fruits": False,
                                "dry_branches_percentage": 30.33
                            },
                            "success": True
                        }
                    }
                }
            }
        }
    },
    400: {
        "description": "Ошибка клиента",
        "content": {
            "application/json": {
                "examples": {
                    "invalid_data": {
                        "summary": "Некорректные данные",
                        "value": {
                            "error": "Отсутствуют обязательные параметры: name, family или genus"
                        }
                    }
                }
            }
        }
    }
}
