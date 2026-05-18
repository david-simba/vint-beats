package com.davidsimba.vintbeats.feature.auth.domain

data class AuthToken(
    val accessToken: String,
    val refreshToken: String
)