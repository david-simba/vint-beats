package com.davidsimba.vintbeats.feature.search.ui.components

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.davidsimba.vintbeats.core.model.ExploreCategory
import com.davidsimba.vintbeats.feature.search.R

// Nombres que llegan de la API (en minúsculas) -> recurso traducido. Si no está, se muestra el nombre original.
private val categoryNames: Map<String, Int> = mapOf(
    "chill" to R.string.search_category_chill,
    "christmas" to R.string.search_category_christmas,
    "commute" to R.string.search_category_commute,
    "energize" to R.string.search_category_energize,
    "feel good" to R.string.search_category_feel_good,
    "focus" to R.string.search_category_focus,
    "gaming" to R.string.search_category_gaming,
    "hanukkah" to R.string.search_category_hanukkah,
    "party" to R.string.search_category_party,
    "romance" to R.string.search_category_romance,
    "sleep" to R.string.search_category_sleep,
    "workout" to R.string.search_category_workout,
)

private val seasonalNames = setOf("christmas", "hanukkah", "navidad", "janucá")

private fun ExploreCategory.key() = title.trim().lowercase()

@StringRes
internal fun ExploreCategory.nameRes(): Int? = categoryNames[key()]

/** Las categorías de temporada pasan al final, sin perder el orden relativo del resto. */
internal fun List<ExploreCategory>.seasonalLast(): List<ExploreCategory> =
    sortedBy { it.key() in seasonalNames }

// Tonos de la paleta anterior, oscurecidos hasta lograr contraste >= 4.5:1 con texto blanco.
internal val exploreCardColors = listOf(
    Color(0xFFCC4444), Color(0xFFB05F28), Color(0xFF897315), Color(0xFF627E38),
    Color(0xFF327F7F), Color(0xFF5374B7), Color(0xFF817263), Color(0xFF334488),
    Color(0xFF557722), Color(0xFF227777), Color(0xFFBB5520), Color(0xFFAA2828),
)
