package com.example.dtl.presentation.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dtl.presentation.theme.Green

@Composable
fun BottomNavBar(navController: NavHostController, modifier: Modifier = Modifier) {
    val navItems = listOf(
        BottomNavRoutes.Request,
        BottomNavRoutes.Archive,
    )
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier.height(60.dp),
        containerColor = Color.White,
    ) {
        navItems.forEach { item ->
            val parentRoute = currentRoute?.substring(0, ("$currentRoute/").indexOf("/"))
            NavigationBarItem(
                selected = item.route == parentRoute,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painterResource(item.icon),
                        contentDescription = item.label,
                        modifier = Modifier.size(32.dp),
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = Green,
                    unselectedIconColor = Gray
                )
            )
        }
    }
}