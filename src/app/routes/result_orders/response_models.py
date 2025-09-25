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
    }
}
result_order_responses = {
    200: {
        "description": "Успешный запрос",
        "content": {
            "application/json": {
                "examples": {
                    "success_example": {
                        "summary": "Успешный ответ",
                        "value": {
                            "data": {
                                "total_plants": 0,
                                "total_trees": 0,
                                "total_shrubs": 0,
                                "shrub_types": {},
                                "tree_types": {},
                                "condition_status": {
                                    1: 0,
                                    2: 0,
                                    3: 0
                                }
                            },
                            "success": True
                        }
                    }
                }
            }
        }
    }
}
