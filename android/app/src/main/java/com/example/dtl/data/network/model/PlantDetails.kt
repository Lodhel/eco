package com.example.dtl.data.network.model

data class PlantDetails(
    val id: Int,
    val name: String,
    val family: String,
    val genus: String,
    val growing_area: String,
    val height: String?,
    val class_type: String?,
    val has_fruits: Boolean,
    val dry_branches_percentage: Double?,
    val status: Int
)