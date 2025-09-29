package com.example.dtl.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf

val LocalBottomBarVisibilityHolder = compositionLocalOf { BottomBarVisibilityHolder() }

data class BottomBarVisibilityHolder(
    var isVisible: Boolean = true
)

@Composable
fun bottomBarCurrentVisibility(): Boolean {
    return LocalBottomBarVisibilityHolder.current.isVisible
}

@Composable
fun BottomBarVisibility(
    isVisible: Boolean,
) {
    LocalBottomBarVisibilityHolder.current.let {
        LaunchedEffect(null) {
            it.isVisible = isVisible
        }
    }
}