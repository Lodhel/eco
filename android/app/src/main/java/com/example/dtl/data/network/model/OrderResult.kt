package com.example.dtl.data.network.model

data class OrderResult(
    val id: Int,
    val label: String,
    val name_plant: String,
    val season: String,
    val bbox_norm: List<Double>,
    val dry_branches_percentage: Double,
)