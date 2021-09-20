package dev.binhjdev.androidbasketballleague.util

import dev.binhjdev.androidbasketballleague.data.model.*
import dev.binhjdev.androidbasketballleague.ui.players.*
import dev.binhjdev.androidbasketballleague.ui.scoreboard.ScheduleGame
import dev.binhjdev.androidbasketballleague.ui.scoreboard.ScheduledGameStatus
import dev.binhjdev.androidbasketballleague.ui.scoreboard.ScoreboardPlayerInfo
import dev.binhjdev.androidbasketballleague.ui.standings.TeamStanding
import dev.binhjdev.androidbasketballleague.ui.standings.WinLoss
import dev.binhjdev.androidbasketballleague.ui.teams.Division

fun List<TeamStandingApiModel>.convertToTeamStandings(originalStandings: List<TeamStanding>): List<TeamStanding> =
        this.map { apiModel ->
            val originalStandings =
                    originalStandings.firstOrNull { original -> original.teamId == apiModel.teamId }
            TeamStanding(
                    apiModel.teamId,
                    originalStandings?.division ?: Division.Unknown,
                    apiModel.wins,
                    apiModel.losses,
                    apiModel.winsLastTen,
                    apiModel.streakCount,
                    apiModel.streakType.toWinLoss(),
                    apiModel.divisionGamesBack,
                    apiModel.leagueGamesBack
            ).apply { this.id = originalStandings?.id ?: 0 }
        }

fun WinLossApiModel.toWinLoss() = when (this) {
    WinLossApiModel.Win -> WinLoss.Win
    WinLossApiModel.Loss -> WinLoss.Loss
    else -> WinLoss.Unknown
}

fun List<ScheduleGameApiModel>.convertToScheduleGames(): List<ScheduleGame> =
        this.map { apiModel ->
            val scoreboardPlayers = apiModel.getScoreboardPlayerInfo()
            ScheduleGame(
                    apiModel.gameId,
                    apiModel.gameStatus.toScheduleGameStatus(),
                    apiModel.gameStartTime,
                    apiModel.inning,
                    apiModel.isTopOfInning,
                    apiModel.outs,
                    apiModel.homeTeam.teamId,
                    "${apiModel.homeTeam.wins} - ${apiModel.homeTeam.losses}",
                    apiModel.homeTeam.score,
                    apiModel.awayTeam.teamId,
                    "${apiModel.awayTeam.wins} - ${apiModel.awayTeam.losses}",
                    apiModel.awayTeam.score,
                    scoreboardPlayers.getOrNull(0),
                    scoreboardPlayers.getOrNull(1),
                    scoreboardPlayers.getOrNull(2)
            )
        }

fun ScheduleGameStatusApiModel.toScheduleGameStatus() =
        when (this) {
            ScheduleGameStatusApiModel.Upcoming -> ScheduledGameStatus.Upcoming
            ScheduleGameStatusApiModel.InProgress -> ScheduledGameStatus.InProgress
            ScheduleGameStatusApiModel.Completed -> ScheduledGameStatus.Completed
            else -> ScheduledGameStatus.Unknown
        }

fun ScheduleGameApiModel.getScoreboardPlayerInfo(): List<ScoreboardPlayerInfo?> =
        when (this.gameStatus) {
            ScheduleGameStatusApiModel.Completed -> {
                val homeTeamWon = this.homeTeam.score > this.awayTeam.score
                listOf(
                        (if (homeTeamWon) this.homeTeam.pitcherOfRecord else this.awayTeam.pitcherOfRecord)?.convertPitcherToScoreboardPlayerInfo(),
                        (if (homeTeamWon) this.awayTeam.pitcherOfRecord else this.homeTeam.pitcherOfRecord)?.convertPitcherToScoreboardPlayerInfo(),
                        (if (homeTeamWon) this.homeTeam.savingPitcher else this.awayTeam.savingPitcher)?.convertCloserToScoreboardPlayerInfo()
                )
            }
            ScheduleGameStatusApiModel.InProgress -> listOf(
                    (if (this.isTopOfInning) this.homeTeam.currentPitcher else this.awayTeam.currentPitcher)?.convertPitcherToScoreboardPlayerInfo(),
                    (if (this.isTopOfInning) this.awayTeam.currentBatter else this.homeTeam.currentBatter)?.convertCurrentBatterToScoreboardPlayerInfo()
            )
            ScheduleGameStatusApiModel.Upcoming -> listOf(
                    this.awayTeam.startingPitcher?.convertPitcherToScoreboardPlayerInfo(),
                    this.homeTeam.startingPitcher?.convertPitcherToScoreboardPlayerInfo()
            )
            else -> emptyList()
        }

fun ScheduleGamePitcherApiModel.convertPitcherToScoreboardPlayerInfo() =
        ScoreboardPlayerInfo(this.playerName, "${this.wins} - ${this.losses}, ${this.era}")

fun ScheduleGamePitcherApiModel.convertCloserToScoreboardPlayerInfo() =
        ScoreboardPlayerInfo(this.playerName, this.saves.toString())

fun ScheduleGameBatterApiModel.convertCurrentBatterToScoreboardPlayerInfo() =
        ScoreboardPlayerInfo(
                this.playerName,
                "${this.hitsToday} - ${this.atBatsToday}, ${this.battingAverage}"
        )

fun PlayerApiModel.convertToPlayer() = Player(
        this.playerId,
        this.teamId,
        this.firstName,
        this.lastName,
        this.number,
        this.bats.toHand(),
        this.throws.toHand(),
        this.position.toPosition(),
        this.boxScoreLastName ?: this.lastName
)

fun List<PlayerApiModel>.convertToPlayers() = this.map(PlayerApiModel::convertToPlayer)

fun HandApiModel.toHand() = when (this) {
    HandApiModel.Left -> Hand.Left
    HandApiModel.Right -> Hand.Right
    else -> Hand.Unknown
}

fun PositionApiModel.toPosition() = Position.values().firstOrNull { it.shortName == this.shortName }
        ?: Position.Unknown

fun List<BatterBoxScoreItemApiModel>.convertToBattersAndStats() =
        this.associate { apiModel ->
            apiModel.convertToBatterAndStats()
        }.let { statsMap ->
            Pair(statsMap.keys.toList(), statsMap.values.toList())
        }

fun BatterBoxScoreItemApiModel.convertToBatterAndStats() =
        Player(
                this.playerId,
                this.teamId,
                this.firstName,
                this.lastName,
                this.number,
                this.bats.toHand(),
                this.throws.toHand(),
                this.position.toPosition(),
                this.boxScoreLastName
        ) to PlayerStats(
                this.playerId,
                batterStats = BatterStats(
                        this.games,
                        this.plateAppearances,
                        this.atBats,
                        this.runs,
                        this.hits,
                        this.doubles,
                        this.triples,
                        this.homeRuns,
                        this.rbi,
                        this.strikeouts,
                        this.baseOnBalls,
                        this.hitByPitch,
                        this.stolenBases,
                        this.caughtStealing,
                        this.gidp,
                        this.sacrificeHits,
                        this.sacrificeFlies,
                        this.errors,
                        this.passedBalls
                )
        )

fun List<PitcherBoxScoreItemApiModel>.convertToPitchersAndStats() =
        this.associate { apiModel ->
            apiModel.convertToPitcherAndStats()
        }.let { statsMap ->
            Pair(statsMap.keys.toList(), statsMap.values.toList())
        }

fun PitcherBoxScoreItemApiModel.convertToPitcherAndStats() =
        Player(
                this.playerId,
                this.teamId,
                this.firstName,
                this.lastName,
                this.number,
                this.bats.toHand(),
                this.throws.toHand(),
                this.position.toPosition(),
                this.boxScoreLastName
        ) to PlayerStats(
                this.playerId,
                pitcherStats = PitcherStats(
                        this.games,
                        this.gamesStarted,
                        this.outs,
                        this.hits,
                        this.doubles,
                        this.triples,
                        this.homeRuns,
                        this.runs,
                        this.earnedRuns,
                        this.baseOnBalls,
                        this.hitByPitches,
                        this.strikeouts,
                        this.errors,
                        this.wildPitches,
                        this.battersFaced,
                        this.wins,
                        this.losses,
                        this.saves,
                        this.blownSaves,
                        this.holds
                )
        )