package dev.binhjdev.androidbasketballleague.data.model

data class AppSettingsApiModel(
    val userId: String,
    val favoriteTeamId: String,
    val useTeamColorNavBar: Boolean,
    val startingLocation: String
)
