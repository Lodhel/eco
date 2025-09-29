package com.example.dtl.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.dtl.presentation.feature.AnalysisDetailsScreen
import com.example.dtl.presentation.feature.MakeRequestScreen
import com.example.dtl.presentation.feature.MyRequestsScreen
import com.example.dtl.presentation.feature.PlantDetailsScreen
import com.example.dtl.presentation.feature.RequestDetailsScreen

@Composable
fun AppNavHost(modifier: Modifier, navController: NavHostController) {
    NavHost(navController, startDestination = "make_request") {
        composable(
            route = "my_requests/request_details/plant_details/{orderId}/{resultId}",
            arguments = listOf(
                navArgument("orderId") { type = NavType.IntType },
                navArgument("resultId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId")
            val resultId = backStackEntry.arguments?.getInt("resultId")
            if (orderId != null && resultId != null) {
                PlantDetailsScreen(
                    modifier = modifier,
                    orderId = orderId,
                    resultId = resultId,
                    onBackPressed = { navController.popBackStack() }
                )
            }
        }
        composable(
            "my_requests/request_details/analysis_details/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("orderId")
            id?.let { AnalysisDetailsScreen(
                modifier = modifier,
                orderId = it,
                onBackPressed = { navController.popBackStack() }
            )}
        }
        composable(
            "my_requests/request_details/{orderId}/{imagePath}",
            arguments = listOf(
                navArgument("orderId") { type = NavType.IntType },
                navArgument("imagePath") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId")
            val imagePath = Uri.decode(
                backStackEntry.arguments?.getString("imagePath") ?: ""
            )
            if (orderId != null && imagePath != null) { RequestDetailsScreen(
                modifier = modifier,
                orderId = orderId,
                imagepath = imagePath,
                onPlantClick = { resultId ->
                    navController.navigate(
                        "my_requests/request_details/plant_details/${orderId}/${resultId}"
                    )
                },
                onBackPressed = { navController.popBackStack() },
                onAnalyticsClick = {
                    navController.navigate(
                        "my_requests/request_details/analysis_details/${orderId}"
                    )
                },
            )}
        }
        composable("my_requests") {
            MyRequestsScreen(modifier = modifier) { orderId, imagePath ->
                navController.navigate(
                    "my_requests/request_details/${orderId}/${Uri.encode(imagePath)}"
                )
            }
        }
        composable("make_request") {
            MakeRequestScreen(modifier = modifier) {
                navController.navigate("my_requests") {
                    popUpTo("make_request") {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }
}