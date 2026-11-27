package com.davidsimba.vintbeats.navigation.navbar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.navigation.Screen
import com.davidsimba.vintbeats.core.designsystem.theme.Fondo
import com.davidsimba.vintbeats.core.designsystem.theme.TextoSecundario
import com.davidsimba.vintbeats.core.designsystem.theme.colorAcentoBarra
import com.davidsimba.vintbeats.core.designsystem.R as DsR

data class BottomNavBarItem(
    val screen: Screen,
    @StringRes val label: Int,
    val icon: ImageVector? = null,
    @DrawableRes val iconRes: Int? = null,
)

val bottomNavItems = listOf(
    BottomNavBarItem(Screen.Home, DsR.string.nav_home, Icons.Outlined.Home),
    BottomNavBarItem(Screen.Search, R.string.nav_search, Icons.Outlined.Search),
    BottomNavBarItem(Screen.Dj, R.string.nav_dj, iconRes = R.drawable.ic_dj),
    BottomNavBarItem(Screen.Library, R.string.nav_library, Icons.Outlined.LibraryMusic),
)

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Fondo)
            .padding(top = 12.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.screen.route
            val tint = if (selected) colorAcentoBarra else TextoSecundario
            val label = stringResource(item.label)

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .heightIn(min = 40.dp)
                    .clickable(role = Role.Tab) {
                        if (currentRoute != item.screen.route) {
                            if (item.screen == Screen.Home) {
                                navController.popBackStack(Screen.Home.route, inclusive = false)
                            } else {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (item.iconRes != null) {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(22.dp)
                    )
                } else if (item.icon != null) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = label,
                    color = tint,
                    fontSize = 10.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
