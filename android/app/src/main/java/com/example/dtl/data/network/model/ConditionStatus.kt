package com.example.dtl.data.network.model

import com.google.gson.annotations.SerializedName

data class ConditionStatus(
    @SerializedName("1")
    val good: Int,

    @SerializedName("2")
    val normal: Int,

    @SerializedName("3")
    val bad: Int,
)