package dev.binhjdev.androidbasketballleague.util

import android.app.Application
import dev.binhjdev.androidbasketballleague.R
import dev.binhjdev.androidbasketballleague.data.BaseballRepository.ResultStatus
import dev.binhjdev.androidbasketballleague.ui.scoreboard.ScheduleGame
import dev.binhjdev.androidbasketballleague.ui.scoreboard.ScheduledGameStatus
import java.text.DecimalFormat

fun Int.withOrdinal() =
        this.toString() + if (this % 100 in 11..13) "th" else when (this % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }

fun Double.toERAString() = DecimalFormat("0.00").format(this)

fun Double.toBattingPercentageString() = DecimalFormat("#.000").format(this)

val ScheduleGame?.inningText: String
    get() = if (this != null) "${if (this.isTopOfInning == true) "Top" else "Bot"} ${this.inning?.withOrdinal()}"
    else "N/A"

val ScheduleGame?.gameStartTimeText: String
    get() = if (this != null) ScheduleGame.startTimeFormat.format(this.gameStartTime) else "N/A"

fun ScheduleGame?.getPlayerLabel(playerNum: Int) =
        when (this?.gameStatus) {
            ScheduledGameStatus.Upcoming -> listOf(this.awayTeamId, this.homeTeamId, null)[playerNum]
            ScheduledGameStatus.InProgress -> listOf("P", "AB", null)[playerNum]
            ScheduledGameStatus.Completed -> listOf("W", "L", "S")[playerNum]
            else -> null
        }
fun ResultStatus.getErrorMessage(application: Application) = when (this) {
    ResultStatus.NetworkException ->
        application.resources.getString(R.string.network_exception_message)
    ResultStatus.RequestException ->
        application.resources.getString(R.string.request_exception_message)
    ResultStatus.GeneralException ->
        application.resources.getString(R.string.general_exception_message)
    else -> null
}