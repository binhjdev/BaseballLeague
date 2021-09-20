package dev.binhjdev.androidbasketballleague.ui.setting

import android.content.SharedPreferences
import dev.binhjdev.androidbasketballleague.R

val startingScreens = mapOf(
        "Leaders" to R.id.leadersFragment,
        "Players" to R.id.playersFragment,
        "Scoreboard" to R.id.scoreboardFragment,
        "Standings" to R.id.standingsFragment,
        "Teams" to R.id.teamsFragment
)

fun getSelectedStartingScreen(prefs: SharedPreferences) =
        prefs.getString(SettingFragment.startingScreenPreferenceKey, null).let { startId ->
            startingScreens[startId] ?: R.id.scoreboardFragment
        }