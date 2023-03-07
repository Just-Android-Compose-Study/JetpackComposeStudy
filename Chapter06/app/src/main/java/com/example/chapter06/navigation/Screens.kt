package com.example.chapter06.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.chapter06.R

sealed class Screens(
    val route: String,
    @StringRes val label: Int? = null,
    @DrawableRes val icon: Int? = null
) {
    companion object {
        val mainScreens = listOf(
            Splash,
            Home
        )

        val homeScreens = listOf(
            Temperature,
            Distances
        )

        const val splash = "splash"
        const val home = "home"

        const val temperature = "temperature"
        const val distances = "distances"
    }

    private object Home : Screens(home)
    private object Splash : Screens(splash)

    private object Temperature : Screens(
        temperature,
        R.string.temperature,
        R.drawable.baseline_thermostat_24
    )

    private object Distances : Screens(
        distances,
        R.string.distances,
        R.drawable.baseline_square_foot_24
    )
}
