package com.davidsimba.vintbeats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.davidsimba.vintbeats.feature.auth.ui.AuthViewModel
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
                val authViewModel: AuthViewModel = hiltViewModel()
                val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()

                val startDestination = if (isLoggedIn) {
                    Screen.Home.route
                } else {
                    Screen.Auth.route
                }

                val navController = rememberNavController()

                NavGraph(
                    navController = navController,
                    startDestination = startDestination
                )
            }
        }
    }
}