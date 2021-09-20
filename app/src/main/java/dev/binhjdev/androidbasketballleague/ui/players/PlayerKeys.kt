package dev.binhjdev.androidbasketballleague.ui.players

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_keys")
data class PlayerKeys(
        @PrimaryKey
        val playerId: String,
        val previousKey: Int?,
        val nextKey: Int?
)