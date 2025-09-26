order_responses = {
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
                                "image_path": "sample.jpg",
                                "title": "Заявка на определение растения",
                                "created_at": "2023-09-25T12:00:00",
                                "results": [
                                    {
                                        "name_plant": "дуб",
                                        "season": "вегетационный",
                                        "label": "дерево",
                                        "confidence": 0.95,
                                        "bbox": [120, 50, 900, 700]
                                    }
                                ]
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
                    "order_not_found": {
                        "summary": "Заявка не найдена",
                        "value": {
                            "error": "Заявка с указанным ID не найдена"
                        }
                    }
                }
            }
        }
    }
}
