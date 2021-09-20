package dev.binhjdev.androidbasketballleague.util

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class DateTimeQueryConverterFactory : Converter.Factory() {
    override fun stringConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, String>? = when (type) {
        LocalDate::class.java -> Converter<LocalDate, String> { date -> dateFormat.format(date) }
        LocalDateTime::class.java -> Converter<LocalDateTime, String> { dateTime ->
            dateTimeFormat.format(
                dateTime
            )
        }
        else -> super.stringConverter(type, annotations, retrofit)
    }
}