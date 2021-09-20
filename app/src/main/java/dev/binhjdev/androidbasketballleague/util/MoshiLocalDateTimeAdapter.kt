package dev.binhjdev.androidbasketballleague.util

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.threeten.bp.LocalDateTime


class MoshiLocalDateTimeAdapter {
    @ToJson
    fun toJson(dateTime: LocalDateTime?): String? =
        dateTime?.let { dateTimeHourFormat.format(it) }

    @FromJson
    fun fromJson(dateTimeString: String?): LocalDateTime? =
        dateTimeString?.let { LocalDateTime.parse(it, dateTimeHourFormat) }
}