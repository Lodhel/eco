package com.example.dtl.domain

sealed class ResourceState<out T> {
    object Loading : ResourceState<Nothing>()
    data class Success<out T>(val data: T) : ResourceState<T>()
    data class Error(val msg: String) : ResourceState<Nothing>()
}