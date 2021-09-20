package dev.binhjdev.androidbasketballleague.data.model

data class TeamApiModel(
        val id: String,
        val location: String,
        val nickname: String,
        val division: DivisionApiModel,
        val logo: String,
        val scheduleId: String = ""
)
