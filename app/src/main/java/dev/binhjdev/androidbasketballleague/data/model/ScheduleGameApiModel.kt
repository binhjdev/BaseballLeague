package dev.binhjdev.androidbasketballleague.data.model

import org.threeten.bp.LocalDateTime

data class ScheduleGameApiModel(
        val gameId: String,
        val gameStatus: ScheduleGameStatusApiModel,
        val gameStartTime: LocalDateTime,
        val homeTeam: ScheduleGameTeamApiModel,
        val awayTeam: ScheduleGameTeamApiModel,
        val inning: Int? = null,
        val isTopOfInning: Boolean = false,
        val outs: Int? = null,
        val occupiedBases: OccupiedBasesApiModel? = null
)

data class ScheduleGameTeamApiModel(
        val teamId: String,
        val teamNickname: String,
        val score: Int = 0,
        val wins: Int = 0,
        val losses: Int = 0,
        val startingPitcher: ScheduleGamePitcherApiModel? = null,
        val currentPitcher: ScheduleGamePitcherApiModel? = null,
        val currentBatter: ScheduleGameBatterApiModel? = null,
        val pitcherOfRecord: ScheduleGamePitcherApiModel? = null,
        val savingPitcher: ScheduleGamePitcherApiModel? = null
)

data class ScheduleGamePitcherApiModel(
        val playerId: String,
        val playerName: String,
        val wins: Int = 0,
        val losses: Int = 0,
        val era: Double = 0.0,
        val saves: Int = 0
)

data class ScheduleGameBatterApiModel(
        val playerId: String,
        val playerName: String,
        val hitsToday: Int = 0,
        val atBatsToday: Int = 0,
        val battingAverage: Double = 0.0
)

data class OccupiedBasesApiModel(
        val first: Boolean = false,
        val second: Boolean = false,
        val third: Boolean = false
)




