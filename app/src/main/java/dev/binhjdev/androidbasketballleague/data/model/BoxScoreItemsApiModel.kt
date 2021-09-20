package dev.binhjdev.androidbasketballleague.data.model

data class BoxScoreItemsApiModel(
        val batting: BatterBoxScoreItemApiModel? = null,
        val pitching: PitcherBoxScoreItemApiModel? = null
)
