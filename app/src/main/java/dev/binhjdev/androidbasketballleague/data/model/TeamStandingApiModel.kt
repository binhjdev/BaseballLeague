package dev.binhjdev.androidbasketballleague.data.model

data class TeamStandingApiModel(
    val teamId: String,
    val wins: Int,
    val losses: Int,
    val winsLastTen: Int,
    val streakCount: Int,
    val streakType: WinLossApiModel,
    val divisionGamesBack: Double,
    val leagueGamesBack: Double
)
