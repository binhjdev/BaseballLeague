package dev.binhjdev.androidbasketballleague.util

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.threeten.bp.LocalDate

class MoshiLocalDateAdapter {
    @ToJson
    fun toJson(date: LocalDate?): String? = date?.let { dateFormat.format(it) }

    @FromJson
    fun fromJson(dateString: String?): LocalDate? = dateString?.let {
        LocalDate.parse(
            it,
            dateFormat
        )
    }

}