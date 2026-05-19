package com.davidsimba.vintbeats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.davidsimba.vintbeats.navigation.NavGraph
import com.davidsimba.vintbeats.navigation.Screen
import com.davidsimba.vintbeats.shared.theme.VintBeatsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VintBeatsTheme {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    startDestination = Screen.Home.route
                )
            }
        }
    }
}
