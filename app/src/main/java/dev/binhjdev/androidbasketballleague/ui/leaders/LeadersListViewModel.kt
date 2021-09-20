package dev.binhjdev.androidbasketballleague.ui.leaders

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import dev.binhjdev.androidbasketballleague.data.BaseballDatabase
import dev.binhjdev.androidbasketballleague.data.BaseballRepository
import dev.binhjdev.androidbasketballleague.ui.players.PlayerWithStats
import dev.binhjdev.androidbasketballleague.ui.teams.UITeam
import dev.binhjdev.androidbasketballleague.util.getErrorMessage
import dev.binhjdev.androidbasketballleague.util.toBattingPercentageString
import dev.binhjdev.androidbasketballleague.util.toERAString
import kotlinx.coroutines.launch

class LeadersListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BaseballRepository = BaseballDatabase
            .getDatabase(application, viewModelScope)
            .let { db ->
                BaseballRepository.getInstance(db)
            }

    val battingLeaders: LiveData<List<LeaderListItem>>

    val pitchingLeaders: LiveData<List<LeaderListItem>>

    val errorMessage = MutableLiveData("")

    init {
        battingLeaders = Transformations.map(repository.getBattersWithStats()) { battersWithStats ->
            battingStatCategories.mapIndexed { index, handler ->
                battersWithStats
                        ?.sortedWith(handler.statComparator)
                        ?.firstOrNull()?.let { batterWithStats ->
                            LeaderListItem(
                                    index.toLong(),
                                    batterWithStats.player,
                                    handler.category,
                                    handler.formattedStatValue(batterWithStats),
                                    UITeam.fromTeamId(batterWithStats.player.teamId)?.nickname
                                            ?: "N/A"
                            )
                        }
            }.filterNotNull()
        }

        pitchingLeaders = Transformations.map(repository.getPitchersWithStats()) { pitchersWithStats ->
            pitchingStatCategories.mapIndexed { index, handler ->
                pitchersWithStats
                        ?.sortedWith(handler.statComparator)
                        ?.firstOrNull()?.let { pitcherWithStats ->
                            LeaderListItem(
                                    index.toLong(),
                                    pitcherWithStats.player,
                                    handler.category,
                                    handler.formattedStatValue(pitcherWithStats),
                                    UITeam.fromTeamId(pitcherWithStats.player.teamId)?.nickname
                                            ?: "N/A"
                            )
                        }
            }.filterNotNull()
        }

        refreshBattingLeaders()
        refreshPitchingLeaders()

        Log.d("battingLeaders.value", battingLeaders.value.toString())
    }

    fun refreshBattingLeaders() {
        refreshLeaders { repository.updateBattingLeaders() }
    }

    fun refreshPitchingLeaders() {
        refreshLeaders { repository.updatePitchingLeaders() }
    }

    private fun refreshLeaders(updateFunction: suspend () -> BaseballRepository.ResultStatus) {
        viewModelScope.launch {
            updateFunction().getErrorMessage(getApplication())
                    ?.let { message -> errorMessage.value = message }
        }
    }

    private val battingStatCategories = listOf(
            PlayerStatHandler(
                    category = "AVG",
                    statComparator = { batterA, batterB -> -batterA.stats.batterStats.battingAverage.compareTo(batterB.stats.batterStats.battingAverage) },
                    formattedStatValue = { batter -> batter.stats.batterStats.battingAverage.toBattingPercentageString() }
            ),
            PlayerStatHandler(
                    category = "HR",
                    statComparator = { batterA, batterB -> -batterA.stats.batterStats.homeRuns.compareTo(batterB.stats.batterStats.homeRuns) },
                    formattedStatValue = { batter -> batter.stats.batterStats.homeRuns.toString() }
            ),
            PlayerStatHandler(
                    category = "RBI",
                    statComparator = { batterA, batterB -> -batterA.stats.batterStats.rbi.compareTo(batterB.stats.batterStats.rbi) },
                    formattedStatValue = { batter -> batter.stats.batterStats.rbi.toString() }
            ),
            PlayerStatHandler(
                    category = "SB",
                    statComparator = { batterA, batterB -> -batterA.stats.batterStats.stolenBases.compareTo(batterB.stats.batterStats.stolenBases) },
                    formattedStatValue = { batter -> batter.stats.batterStats.stolenBases.toString() }
            ),
            PlayerStatHandler(
                    category = "R",
                    statComparator = { batterA, batterB -> -batterA.stats.batterStats.runs.compareTo(batterB.stats.batterStats.runs) },
                    formattedStatValue = { batter -> batter.stats.batterStats.runs.toString() }
            ),
            PlayerStatHandler(
                    category = "OBP",
                    statComparator = { batterA, batterB -> -batterA.stats.batterStats.onBasePercentage.compareTo(batterB.stats.batterStats.onBasePercentage) },
                    formattedStatValue = { batter -> batter.stats.batterStats.onBasePercentage.toBattingPercentageString() }
            ),
            PlayerStatHandler(
                    category = "SLG",
                    statComparator = { batterA, batterB -> -batterA.stats.batterStats.sluggingPercentage.compareTo(batterB.stats.batterStats.sluggingPercentage) },
                    formattedStatValue = { batter -> batter.stats.batterStats.sluggingPercentage.toBattingPercentageString() }
            ),
            PlayerStatHandler(
                    category = "OPS",
                    statComparator = { batterA, batterB -> -batterA.stats.batterStats.ops.compareTo(batterB.stats.batterStats.ops) },
                    formattedStatValue = { batter -> batter.stats.batterStats.ops.toBattingPercentageString() }
            )
    )

    private val pitchingStatCategories = listOf(
            PlayerStatHandler(
                    "ERA",
                    { pitcherA, pitcherB -> pitcherA.stats.pitcherStats.era.compareTo(pitcherB.stats.pitcherStats.era) },
                    { pitcher -> pitcher.stats.pitcherStats.era.toERAString() }
            ),
            PlayerStatHandler(
                    "W",
                    { pitcherA, pitcherB -> -pitcherA.stats.pitcherStats.wins.compareTo(pitcherB.stats.pitcherStats.wins) },
                    { pitcher -> pitcher.stats.pitcherStats.wins.toString() }
            ),
            PlayerStatHandler(
                    "L",
                    { pitcherA, pitcherB -> pitcherA.stats.pitcherStats.losses.compareTo(pitcherB.stats.pitcherStats.losses) },
                    { pitcher -> pitcher.stats.pitcherStats.losses.toString() }
            ),
            PlayerStatHandler(
                    "SO",
                    { pitcherA, pitcherB -> -pitcherA.stats.pitcherStats.strikeouts.compareTo(pitcherB.stats.pitcherStats.strikeouts) },
                    { pitcher -> pitcher.stats.pitcherStats.strikeouts.toString() }
            ),
            PlayerStatHandler(
                    "SV",
                    { pitcherA, pitcherB -> -pitcherA.stats.pitcherStats.saves.compareTo(pitcherB.stats.pitcherStats.saves) },
                    { pitcher -> pitcher.stats.pitcherStats.saves.toString() }
            ),
            PlayerStatHandler(
                    "WHIP",
                    { pitcherA, pitcherB -> pitcherA.stats.pitcherStats.whip.compareTo(pitcherB.stats.pitcherStats.whip) },
                    { pitcher -> pitcher.stats.pitcherStats.whip.toERAString() }
            ),
            PlayerStatHandler(
                    "IP",
                    { pitcherA, pitcherB -> -pitcherA.stats.pitcherStats.inningsPitched.compareTo(pitcherB.stats.pitcherStats.inningsPitched) },
                    { pitcher -> pitcher.stats.pitcherStats.inningsPitched.toString() }
            )
    )

    private data class PlayerStatHandler(
            val category: String,
            val statComparator: Comparator<PlayerWithStats>,
            val formattedStatValue: (PlayerWithStats) -> String
    )
}