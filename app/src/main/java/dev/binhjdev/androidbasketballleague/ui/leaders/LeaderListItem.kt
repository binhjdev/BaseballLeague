package dev.binhjdev.androidbasketballleague.ui.leaders

import dev.binhjdev.androidbasketballleague.ui.players.Player

data class LeaderListItem(
    val itemId: Long,
    val player: Player,
    val statCategory: String,
    val statValue: String,
    val teamName: String
)
