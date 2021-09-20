package dev.binhjdev.androidbasketballleague.ui.standings

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import dev.binhjdev.androidbasketballleague.data.BaseballDatabase
import dev.binhjdev.androidbasketballleague.data.BaseballRepository
import dev.binhjdev.androidbasketballleague.util.getErrorMessage
import kotlinx.coroutines.launch

class StandingsViewModel(application: Application) : AndroidViewModel(application) {
    val errorMessage = MutableLiveData("")
    private val repository: BaseballRepository = BaseballDatabase
        .getDatabase(application, viewModelScope)
        .let { db ->
            BaseballRepository.getInstance(db)
        }
    val standings: LiveData<List<UITeamStanding>> =
        Transformations.map(repository.getStandings()) { teamStandings ->
            teamStandings.mapNotNull { teamStanding ->
                UITeamStanding.fromTeamIdAndStandings(
                    teamStanding.teamId,
                    teamStandings
                )
            }
        }

    fun refreshStandings() {
        viewModelScope.launch {
            repository.updateStandings().getErrorMessage(getApplication())
                ?.let { message ->
                    errorMessage.value = message
                }
        }
    }
}