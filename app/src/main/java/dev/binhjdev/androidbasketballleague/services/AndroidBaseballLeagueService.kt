package dev.binhjdev.androidbasketballleague.services

import dev.binhjdev.androidbasketballleague.data.model.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import retrofit2.http.*

interface AndroidBaseballLeagueService {

    @GET("teams")
    suspend fun getTeams(): List<TeamApiModel>

    @GET("games")
    suspend fun getGames(
        @Query("currentDateTime") currentDateTime: LocalDateTime? = null,
        @Query("requestedDate") requestedDate: LocalDate? = null,
        @Query("teamId") teamId: String? = null
    ): List<ScheduleGameApiModel>

    @GET("players")
    suspend fun getPlayers(
        @Query("pageNumber") pageNumber: Int? = null,
        @Query("pageSize") pageSize: Int? = null,
        @Query("query") query: String? = null,
        @Query("teamId") teamId: String? = null,
        @Query("position") position: PositionApiModel? = null,
        @Query("isPitcher") isPitcher: Boolean? = null,
        @Query("isOutfielder") isOutfielder: Boolean? = null
    ): List<PlayerApiModel>

    @GET("players/{playerId}")
    suspend fun getSinglePlayer(
        @Path("playerId") playerId: String,
        @Query("currentDate") currentDate: LocalDate? = null
    ): BoxScoreItemsApiModel

    @GET("stats/batting")
    suspend fun getBattingStats(
        @Query("currentDate") currentDate: LocalDate? = null,
        @Query("pageNumber") pageNumber: Int? = null,
        @Query("pageSize") pageSize: Int? = null,
        @Query("sortStat") sortStat: String? = null,
        @Query("sortDescending") sortDescending: Boolean? = null,
        @Query("teamId") teamId: String? = null,
        @Query("position") position: PositionApiModel? = null
    ): List<BatterBoxScoreItemApiModel>

    @GET("stats/pitching")
    suspend fun getPitchingStats(
        @Query("currentDate") currentDate: LocalDate? = null,
        @Query("pageNumber") pageNumber: Int? = null,
        @Query("pageSize") pageSize: Int? = null,
        @Query("sortStat") sortStat: String? = null,
        @Query("sortDescending") sortDescending: Boolean? = null,
        @Query("teamId") teamId: String? = null,
        @Query("position") position: PositionApiModel? = null
    ): List<PitcherBoxScoreItemApiModel>

    @GET("leaders/batting")
    suspend fun getBattingLeaders(
        @Query("currentDate") currentDate: LocalDate? = null
    ): List<BatterBoxScoreItemApiModel>

    @GET("leaders/pitching")
    suspend fun getPitchingLeaders(
        @Query("currentDate") currentDate: LocalDate? = null
    ): List<PitcherBoxScoreItemApiModel>

    @GET("standings")
    suspend fun getStandings(@Query("currentDate") currentDate: LocalDate? = null): List<TeamStandingApiModel>

    @GET("app/settings")
    suspend fun getAppSettingsForUser(
        @Query("userId") userId: String
    ): AppSettingsApiModel

    @POST("app/settings")
    suspend fun saveAppSettings(
        @Body settings: AppSettingsApiModel
    ): AppSettingsApiModel
}