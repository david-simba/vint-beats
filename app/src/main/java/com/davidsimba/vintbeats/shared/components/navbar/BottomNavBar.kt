package com.davidsimba.vintbeats.shared.components.navbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.davidsimba.vintbeats.navigation.Screen
import com.davidsimba.vintbeats.shared.theme.VintageBlackMid
import com.davidsimba.vintbeats.shared.theme.VintageGrayCool
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure

data class BottomNavBarItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavBarItem(Screen.Home, "Home", Icons.Rounded.Home),
    BottomNavBarItem(Screen.Search, "Search", Icons.Rounded.Search),
    BottomNavBarItem(Screen.Profile, "Profile", Icons.Rounded.Person)
)

@Composable
fun BottomNavBar(navController: NavController) {
    val currentRoute =  navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        containerColor = VintageBlackMid
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.screen.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.screen.route) {
                        navController.navigate(item.screen.route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                                inclusive = item.screen == Screen.Home
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )

                        Text(
                            text = item.label,
                            fontSize = 12.sp
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = VintageWhitePure,
                    selectedTextColor = VintageWhitePure,
                    unselectedIconColor = VintageGrayCool,
                    unselectedTextColor = VintageGrayCool,
                    indicatorColor = Color.Transparent,
                )
            )
        }
    }
}