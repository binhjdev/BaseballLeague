package dev.binhjdev.androidbasketballleague.ui.players

import android.app.Application
import androidx.lifecycle.*
import dev.binhjdev.androidbasketballleague.data.BaseballDatabase
import dev.binhjdev.androidbasketballleague.data.BaseballRepository
import dev.binhjdev.androidbasketballleague.util.getErrorMessage
import dev.binhjdev.androidbasketballleague.util.toBattingPercentageString
import dev.binhjdev.androidbasketballleague.util.toERAString
import kotlinx.coroutines.launch

class SinglePlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BaseballRepository

    private val playerId = MutableLiveData<String>()

    val playerWithStats: LiveData<PlayerWithStats>
    val stats: LiveData<List<PlayerStatWithLabel>>
    val errorMessage = MutableLiveData("")

    init {
        repository = BaseballDatabase.getDatabase(application, viewModelScope)
                .let { db ->
                    BaseballRepository.getInstance(db)
                }

        playerWithStats = Transformations.switchMap(playerId) { playerId ->
            repository.getPlayerWithStats(playerId)
        }

        stats = Transformations.map(playerWithStats) { playerWithStats: PlayerWithStats? ->
            if (playerWithStats != null) {
                convertPlayerStatsToStatsWithLabels(
                        playerWithStats.player.position.isPitcher(),
                        playerWithStats.stats
                )
            } else listOf()

        }
    }

    fun setPlayerId(playerId: String) {
        this.playerId.value = playerId

        viewModelScope.launch {
            repository.updatePlayer(playerId).getErrorMessage(getApplication())
                    ?.let { message -> errorMessage.value = message }
        }
    }

    private fun convertPlayerStatsToStatsWithLabels(isPitcher: Boolean, playerStats: PlayerStats) =
            if (isPitcher) {
                with(playerStats.pitcherStats) {
                    listOf(
                            PlayerStatWithLabel("G", this.games.toString()),
                            PlayerStatWithLabel("W-L", "${this.wins}-${this.losses}"),
                            PlayerStatWithLabel("ERA", this.era.toERAString()),
                            PlayerStatWithLabel("WHIP", this.whip.toERAString()),
                            PlayerStatWithLabel("IP", this.inningsPitched.toString()),
                            PlayerStatWithLabel("K", this.strikeouts.toString()),
                            PlayerStatWithLabel("BB", this.baseOnBalls.toString()),
                            PlayerStatWithLabel("SV", this.saves.toString())
                    )
                }
            } else {
                with(playerStats.batterStats) {
                    listOf(
                            PlayerStatWithLabel("G", this.games.toString()),
                            PlayerStatWithLabel("AB", this.atBats.toString()),
                            PlayerStatWithLabel("HR", this.homeRuns.toString()),
                            PlayerStatWithLabel("SB", this.stolenBases.toString()),
                            PlayerStatWithLabel("R", this.runs.toString()),
                            PlayerStatWithLabel("RBI", this.rbi.toString()),
                            PlayerStatWithLabel("BA", this.battingAverage.toBattingPercentageString()),
                            PlayerStatWithLabel("OPS", this.ops.toBattingPercentageString())
                    )
                }
            }

}