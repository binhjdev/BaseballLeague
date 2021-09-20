package dev.binhjdev.androidbasketballleague.data

import androidx.room.TypeConverter
import dev.binhjdev.androidbasketballleague.ui.players.Hand
import dev.binhjdev.androidbasketballleague.ui.players.Position
import dev.binhjdev.androidbasketballleague.ui.scoreboard.OccupiedBases
import dev.binhjdev.androidbasketballleague.ui.scoreboard.ScheduledGameStatus
import dev.binhjdev.androidbasketballleague.ui.standings.WinLoss
import dev.binhjdev.androidbasketballleague.ui.teams.Division
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromDivision(division: Division?) =
            division?.ordinal ?: Division.Unknown.ordinal

    @TypeConverter
    fun toDivision(divisionOrdinal: Int?) =
            if (divisionOrdinal != null) {
                Division.values()[divisionOrdinal]
            } else {
                Division.Unknown
            }

    @TypeConverter
    fun fromWinLoss(winLoss: WinLoss?) =
            winLoss?.ordinal ?: WinLoss.Unknown.ordinal

    @TypeConverter
    fun toWinLoss(winLossOrdinal: Int?) =
            if (winLossOrdinal != null) {
                WinLoss.values()[winLossOrdinal]
            } else {
                WinLoss.Unknown
            }

    @TypeConverter
    fun toLocalDateTime(value: String?) = value?.let {
        formatter.parse(it, LocalDateTime::from)
    }

    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime?) = date?.format(formatter)

    @TypeConverter
    fun fromScheduledGameStatus(status: ScheduledGameStatus) = status.ordinal

    @TypeConverter
    fun toScheduledGameStatus(statusOrdinal: Int) = ScheduledGameStatus.values()[statusOrdinal]

    @TypeConverter
    fun fromOccupiedBases(bases: OccupiedBases?) = bases?.toStringList()

    @TypeConverter
    fun toOccupiedBases(basesStringList: String?) =
            OccupiedBases.fromStringList(basesStringList)

    @TypeConverter
    fun fromHand(hand: Hand?) =
            hand?.ordinal ?: Hand.Right.ordinal

    @TypeConverter
    fun toHand(handOrdinal: Int?) =
            if (handOrdinal != null) {
                Hand.values()[handOrdinal]
            } else {
                Hand.Right
            }

    @TypeConverter
    fun fromPosition(position: Position?) =
            position?.ordinal ?: Position.Unknown.ordinal

    @TypeConverter
    fun toPosition(positionOrdinal: Int?) =
            if (positionOrdinal != null) {
                Position.values()[positionOrdinal]
            } else {
                Position.Unknown
            }
}
