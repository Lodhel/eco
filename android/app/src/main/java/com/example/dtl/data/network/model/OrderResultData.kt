package com.example.dtl.data.network.model

data class OrderResultData(
    val id: Int,
    val image_path: String,
    val title: String,
    val created_at: String,
    val results: List<OrderResult>,
    val statement: String
)