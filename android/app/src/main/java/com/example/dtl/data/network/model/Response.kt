package com.example.dtl.data.network.model

data class Response<T>(
    val data: T?,
    val success: Boolean
)
