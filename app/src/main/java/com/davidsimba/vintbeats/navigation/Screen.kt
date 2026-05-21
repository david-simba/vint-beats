package com.davidsimba.vintbeats.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object CustomizeCassette : Screen("customize_cassette")
    data object Library : Screen("library")
    data object Player : Screen("player/{cassetteId}") {
        fun route(id: Int) = "player/$id"
    }
}
