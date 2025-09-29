package com.example.dtl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.dtl.presentation.navigation.AppNavHost
import com.example.dtl.presentation.navigation.BottomNavBar
import com.example.dtl.presentation.theme.DTLTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DTLTheme {
                val navController = rememberNavController()

                Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
                    AppNavHost(
                        modifier = Modifier.padding(24.dp, 24.dp, 24.dp, 60.dp),
                        navController = navController
                    )
                    BottomNavBar(
                        navController = navController,
                        modifier = Modifier
                            .height(60.dp)
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}