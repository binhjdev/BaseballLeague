package dev.binhjdev.androidbasketballleague.services

import com.squareup.moshi.Moshi
import dev.binhjdev.androidbasketballleague.util.DateTimeQueryConverterFactory
import dev.binhjdev.androidbasketballleague.util.MoshiLocalDateAdapter
import dev.binhjdev.androidbasketballleague.util.MoshiLocalDateTimeAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun getDefaultABLService(baseUrl: String = "https://abl.mfazio.dev/api/"): AndroidBaseballLeagueService {
    val moshi = Moshi.Builder()
        .add(MoshiLocalDateAdapter())
        .add(MoshiLocalDateTimeAdapter())
        .build()

    val clientOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()

    val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addConverterFactory(DateTimeQueryConverterFactory())
        .client(clientOkHttpClient)
        .baseUrl(baseUrl)
        .build()

    return retrofit.create(AndroidBaseballLeagueService::class.java)
}

