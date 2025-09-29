package com.example.dtl.data.network.model

data class AnalysisResult(
    val total_plants: Int,
    val total_trees: Int,
    val total_shrubs: Int,
    val shrub_types: Map<String, Int>,
    val tree_types: Map<String, Int>,
    val condition_status: ConditionStatus,
    val season: String
)