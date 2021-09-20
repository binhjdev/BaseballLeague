package dev.binhjdev.androidbasketballleague.ui.standings

import dev.binhjdev.androidbasketballleague.ui.teams.Division

sealed class StandingsListItem {
    abstract val id: String

    data class TeamItem(val uiTeamStanding: UITeamStanding) : StandingsListItem() {
        override val id = uiTeamStanding.teamId
    }

    data class Header(val division: Division) : StandingsListItem() {
        override val id = division.name
    }
}
