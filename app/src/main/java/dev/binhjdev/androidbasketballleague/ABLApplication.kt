package dev.binhjdev.androidbasketballleague

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class ABLApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Init libs
        AndroidThreeTen.init(this);
    }
}