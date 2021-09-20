package dev.binhjdev.androidbasketballleague

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import androidx.preference.PreferenceManager
import dev.binhjdev.androidbasketballleague.databinding.ActivityMainBinding
import dev.binhjdev.androidbasketballleague.ui.setting.SettingFragment
import dev.binhjdev.androidbasketballleague.ui.setting.getSelectedStartingScreen
import dev.binhjdev.androidbasketballleague.ui.teams.UITeam

class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set up toolbar
        setSupportActionBar(binding.toolBar)

        // preference
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // set up navigation view
        val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.containerFragment) as NavHostFragment
        this.navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.startDestination = getSelectedStartingScreen(prefs)
        navController.graph = navGraph

        binding.navView.setupWithNavController(this.navController)

        this.appBarConfiguration = AppBarConfiguration(
                binding.navView.menu, binding.drawerLayout
        )
        setupActionBarWithNavController(this.navController, this.appBarConfiguration)

        if (prefs.getBoolean(SettingFragment.favoriteTeamColorsPreferenceKey, false)) {
            UITeam.getTeamPalette(
                    this,
                    prefs.getString(SettingFragment.favoriteTeamPreferenceKey, null)
            )?.dominantSwatch?.rgb?.let {
                window.navigationBarColor = it
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean =
            (this::navController.isInitialized &&
                    this.navController.navigateUp(this.appBarConfiguration)
                    ) || super.onSupportNavigateUp()
}