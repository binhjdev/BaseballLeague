package dev.binhjdev.androidbasketballleague.ui.scoreboard

import android.app.Application
import androidx.lifecycle.*
import dev.binhjdev.androidbasketballleague.data.BaseballDatabase
import dev.binhjdev.androidbasketballleague.data.BaseballRepository
import dev.binhjdev.androidbasketballleague.ui.teams.UITeam
import dev.binhjdev.androidbasketballleague.util.getErrorMessage
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class ScoreboardViewModel(application: Application) : AndroidViewModel(application) {
    private val currentDateTextFormat = DateTimeFormatter.ofPattern("EEEE, MMM d")

    private val repository: BaseballRepository

    private val selectedDate = MutableLiveData(LocalDate.now())

    val games: LiveData<List<ScheduleGame>>
    val teams: LiveData<List<UITeam>>
    val currentDateText: LiveData<String>
    val errorMessage = MutableLiveData("")

    init {
        repository = BaseballDatabase
            .getDatabase(application, viewModelScope)
            .let { db ->
                BaseballRepository.getInstance(db)
            }

        games = Transformations.switchMap(selectedDate) { selectedDate ->
            refreshScores(selectedDate)
            repository.getGamesForDate(selectedDate)
        }

        teams = Transformations.map(games) { scheduledGames ->
            scheduledGames.flatMap { game ->
                UITeam.fromTeamIds(game.homeTeamId, game.awayTeamId)
            }.filterNotNull()
        }

        currentDateText = Transformations.map(selectedDate) { currentDate ->
            currentDateTextFormat.format(currentDate)
        }
    }

    fun goToDate(daysToMove: Long = 0, monthsToMove: Long? = null) {
        selectedDate.value?.let { date ->
            selectedDate.value = if (monthsToMove != null) {
                date.plusMonths(monthsToMove)
            } else {
                date.plusDays(daysToMove)
            }
        }
    }

    private fun refreshScores(date: LocalDate) {
        viewModelScope.launch {
            repository.updateGamesForDate(date).getErrorMessage(getApplication())?.let { message ->
                errorMessage.value = message
            }
        }
    }
}