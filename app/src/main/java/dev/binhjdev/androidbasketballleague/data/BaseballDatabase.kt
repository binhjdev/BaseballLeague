package dev.binhjdev.androidbasketballleague.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.binhjdev.androidbasketballleague.ui.players.Player
import dev.binhjdev.androidbasketballleague.ui.players.PlayerKeys
import dev.binhjdev.androidbasketballleague.ui.players.PlayerListItem
import dev.binhjdev.androidbasketballleague.ui.players.PlayerStats
import dev.binhjdev.androidbasketballleague.ui.scoreboard.ScheduleGame
import dev.binhjdev.androidbasketballleague.ui.standings.TeamStanding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
        entities = [
            Player::class,
            PlayerListItem::class,
            PlayerKeys::class,
            PlayerStats::class,
            ScheduleGame::class,
            TeamStanding::class
        ],
        exportSchema = false,
        version = 1
)
@TypeConverters(Converters::class)
abstract class BaseballDatabase : RoomDatabase() {
    abstract fun baseballDao(): BaseballDao
    abstract fun playerKeysDao(): PlayerKeysDao

    companion object {
        @Volatile
        private var Instance: BaseballDatabase? = null

        fun getDatabase(
                context: Context,
                scope: CoroutineScope
        ): BaseballDatabase =
                Instance ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                            context,
                            BaseballDatabase::class.java,
                            "BaseballDatabase"
                    ).addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            scope.launch {
                                Instance
                                        ?.baseballDao()
                                        ?.insertStandings(TeamStanding.mockTeamStandings)
                            }
                        }
                    }).build()

                    Instance = instance

                    instance // This is returned from the synchronized block
                }
    }
}
