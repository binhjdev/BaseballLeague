package dev.binhjdev.androidbasketballleague.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.binhjdev.androidbasketballleague.services.getDefaultABLService
import dev.binhjdev.androidbasketballleague.ui.players.PlayerListItem
import dev.binhjdev.androidbasketballleague.ui.players.PlayerListRemoteMediator
import dev.binhjdev.androidbasketballleague.ui.scoreboard.ScheduleGame
import dev.binhjdev.androidbasketballleague.ui.standings.TeamStanding
import dev.binhjdev.androidbasketballleague.util.*
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate
import java.io.IOException
import java.lang.Exception

class BaseballRepository(private val baseballDatabase: BaseballDatabase) {
    private val baseballDao = baseballDatabase.baseballDao()

    fun getStandings(): LiveData<List<TeamStanding>> =
            baseballDao.getStandings()

    suspend fun updateStandings(): ResultStatus {
        val standingsResult = safeApiRequest {
            apiService.getStandings()
        }

        return if (
                standingsResult.success &&
                standingsResult.result?.any() == true
        ) {
            baseballDao.updateStandings(
                    standingsResult.result.convertToTeamStandings(
                            baseballDao.getCurrentStandings()
                    )
            )
            ResultStatus.Success
        } else {
            standingsResult.status
        }
    }

    fun getGamesForDate(date: LocalDate): LiveData<List<ScheduleGame>> =
            baseballDao.getGamesForDate("${date.toGameDateString()}%")

    suspend fun updateGamesForDate(date: LocalDate): ResultStatus {
        val gameResult = safeApiRequest {
            apiService.getGames(requestedDate = date)
        }

        return if (gameResult.success && gameResult.result?.any() == true) {
            baseballDao.insertOrUpdateGames(
                    gameResult.result.convertToScheduleGames()
            )
            ResultStatus.Success
        } else {
            gameResult.status
        }
    }

    fun getPlayerWithStats(playerId: String) = baseballDao.getPlayerWithStats(playerId)

    fun getBattersWithStats() = baseballDao.getBattersWithStats()

    fun getPitchersWithStats() = baseballDao.getPitchersWithStats()

    suspend fun updatePlayer(playerId: String): ResultStatus {
        val playerResult = safeApiRequest {
            apiService.getSinglePlayer(playerId)
        }

        return if (playerResult.success) {
            val batterPair = playerResult.result?.batting?.convertToBatterAndStats()
            val pitcherPair = playerResult.result?.pitching?.convertToPitcherAndStats()

            val player = batterPair?.first ?: pitcherPair?.first
            val playerStats = batterPair?.second ?: pitcherPair?.second

            if (player != null) {
                baseballDao.insertOrUpdatePlayer(player)
            }

            if (playerStats != null) {
                baseballDao.insertOrUpdatePlayerStats(playerStats)
            }
            ResultStatus.Success
        } else {
            playerResult.status
        }
    }

    suspend fun updateBattingLeaders(): ResultStatus {
        val leadersResult = safeApiRequest {
            apiService.getBattingLeaders()
        }

        return if (
                leadersResult.success &&
                leadersResult.result?.any() == true
        ) {
            val (players, playerStats) = leadersResult.result.convertToBattersAndStats()

            baseballDao.insertOrUpdatePlayers(players)
            baseballDao.insertOrUpdateStats(playerStats)
            Log.d("playerBatter", players.toString())
            Log.d("playerStatBatter", playerStats.toString())
            ResultStatus.Success
        } else {
            leadersResult.status
        }
    }

    suspend fun updatePitchingLeaders(): ResultStatus {
        val leadersResult = safeApiRequest {
            apiService.getPitchingLeaders()
        }

        return if (
                leadersResult.success &&
                leadersResult.result?.any() == true
        ) {
            Log.d("Resultbatting : ", leadersResult.result.toString())
            val (players, playerStats) = leadersResult.result.convertToPitchersAndStats()

            baseballDao.insertOrUpdatePlayers(players)
            baseballDao.insertOrUpdateStats(playerStats)

            ResultStatus.Success
        } else {
            leadersResult.status
        }
    }

    @ExperimentalPagingApi
    fun getPlayerListItems(
            teamId: String?,
            nameQuery: String?
    ): Flow<PagingData<PlayerListItem>> {
        val dbNameQuery = if (nameQuery != null) "%$nameQuery%" else null

        return Pager(
                config = PagingConfig(
                        pageSize = defaultPageSize
                ),
                remoteMediator = PlayerListRemoteMediator(
                        apiService,
                        baseballDatabase,
                        teamId,
                        nameQuery
                ),
                pagingSourceFactory = {
                    baseballDao.getPlayerListItems(teamId, dbNameQuery)
                }
        ).flow
    }

    enum class ResultStatus {
        Unknown,
        Success,
        NetworkException,
        RequestException,
        GeneralException
    }

    inner class ApiResult<T>(
            val result: T? = null,
            val status: ResultStatus = ResultStatus.Unknown
    ) {
        val success = status == ResultStatus.Success
    }

    private suspend fun <T> safeApiRequest(
            apiFunction: suspend () -> T
    ): ApiResult<T> =
            try {
                val result = apiFunction()
                ApiResult(result, ResultStatus.Success)
            } catch (ex: retrofit2.HttpException) {
                ApiResult(status = ResultStatus.RequestException)
            } catch (ex: IOException) {
                ApiResult(status = ResultStatus.NetworkException)
            } catch (ex: Exception) {
                ApiResult(status = ResultStatus.GeneralException)
            }

    companion object {
        private val apiService = getDefaultABLService()
        private const val defaultPageSize = 25

        @Volatile
        private var instance: BaseballRepository? = null

        fun getInstance(baseballDatabase: BaseballDatabase) =
                this.instance ?: synchronized(this) {
                    instance ?: BaseballRepository(baseballDatabase).also {
                        instance = it
                    }
                }
    }
}
