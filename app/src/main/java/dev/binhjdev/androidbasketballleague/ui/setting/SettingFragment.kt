package dev.binhjdev.androidbasketballleague.ui.setting

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.*
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dev.binhjdev.androidbasketballleague.R
import dev.binhjdev.androidbasketballleague.services.getDefaultABLService
import dev.binhjdev.androidbasketballleague.ui.teams.UITeam
import kotlinx.coroutines.launch

class SettingFragment : PreferenceFragmentCompat() {

    private lateinit var favoriteTeamPreference: DropDownPreference
    private lateinit var favoriteTeamColorPreference: SwitchPreferenceCompat
    private lateinit var startingScreenPreference: DropDownPreference
    private lateinit var userNamePreference: EditTextPreference

    override fun onCreatePreferences(
            savedInstanceState: Bundle?,
            rootKey: String?
    ) {
        val ctx = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(ctx)

        this.userNamePreference = EditTextPreference(ctx).apply {
            key = usernamePreferenceKey
            title = getString(R.string.user_name)
            summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        loadSettings(newValue?.toString())
                        true
                    }
        }
        screen.addPreference(userNamePreference)

        this.favoriteTeamPreference = DropDownPreference(ctx).apply {
            key = favoriteTeamPreferenceKey
            title = getString(R.string.favorite_team)
            entries =
                    (listOf(getString(R.string.none)) + UITeam.allTeams.map { it.teamName }).toTypedArray()
            entryValues = (listOf("") + UITeam.allTeams.map { it.teamId }).toTypedArray()
            setDefaultValue("")
            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        }

        this.favoriteTeamColorPreference = SwitchPreferenceCompat(ctx).apply {
            key = favoriteTeamColorsPreferenceKey
            title = getString(R.string.team_color_nav_bar)
            setDefaultValue(false)
        }

        favoriteTeamPreference.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val teamId = newValue?.toString()

                    if (favoriteTeamColorPreference.isChecked) {
                        setNavBarColorForTeam(teamId)
                    }

                    if (teamId != null) {
                        favoriteTeamPreference.icon = getIconForTeam(teamId)
                    }

                    saveSettings(favoriteTeam = teamId)

                    true
                }

        screen.addPreference(favoriteTeamPreference)

        favoriteTeamColorPreference.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val useFavoriteTeamColor = newValue as? Boolean
                    setNavBarColorForTeam(
                            if (useFavoriteTeamColor == true) {
                                favoriteTeamPreference.value
                            } else null
                    )

                    saveSettings(useFavoriteTeamColor = useFavoriteTeamColor)

                    true
                }

        screen.addPreference(favoriteTeamColorPreference)

        this.startingScreenPreference = DropDownPreference(ctx).apply {
            key = startingScreenPreferenceKey
            title = getString(R.string.starting_location)
            entries = startingScreens.keys.toTypedArray()
            entryValues = startingScreens.keys.toTypedArray()
            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            setDefaultValue(R.id.scoreboardFragment.toString())
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                saveSettings(startingScreen = newValue?.toString())
                true
            }
        }

        screen.addPreference(startingScreenPreference)

        preferenceScreen = screen
    }

    override fun onBindPreferences() {
        favoriteTeamPreference.icon = getIconForTeam(favoriteTeamPreference.value)
    }

    private fun loadSettings(userName: String? = null) {
        if (userName != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val apiResult = getDefaultABLService().getAppSettingsForUser(userName)


                    with(favoriteTeamPreference) {
                        value = apiResult.favoriteTeamId
                        icon = getIconForTeam(apiResult.favoriteTeamId)
                    }

                    setNavBarColorForTeam(
                            if (apiResult.useTeamColorNavBar) {
                                apiResult.favoriteTeamId
                            } else null
                    )

                    favoriteTeamColorPreference.isChecked = apiResult.useTeamColorNavBar

                    startingScreenPreference.value = apiResult.startingLocation

                } catch (ex: Exception) {
                    Log.i(
                            TAG,
                            """Settings not found.
                        || This may just mean the haven't been initialized yet.
                    || """.trimMargin()
                    )
                    saveSettings(userName)
                }
            }
        }
    }

    private fun saveSettings(
            userName: String? = userNamePreference.text,
            favoriteTeam: String? = favoriteTeamPreference.value,
            useFavoriteTeamColor: Boolean? = favoriteTeamColorPreference.isChecked,
            startingScreen: String? = startingScreenPreference.value
    ) {
        if (userName != null) {
            WorkManager.getInstance(requireContext()).enqueue(
                    OneTimeWorkRequestBuilder<SaveSettingsWorker>()
                            .setInputData(
                                    workDataOf(
                                            SaveSettingsWorker.userNameKey to userName,
                                            SaveSettingsWorker.favoriteTeamKey to favoriteTeam,
                                            SaveSettingsWorker.favoriteTeamColorCheckKey to useFavoriteTeamColor,
                                            SaveSettingsWorker.startingScreenKey to startingScreen
                                    )
                            ).build()
            )
        }
    }

    private fun getIconForTeam(teamId: String) =
            UITeam.fromTeamId(teamId)?.let { team ->
                ContextCompat.getDrawable(requireContext(), team.logoId)
            }

    private fun setNavBarColorForTeam(teamId: String?) {
        val color = UITeam.getTeamPalette(requireContext(), teamId)
                ?.dominantSwatch
                ?.rgb
                ?: getDefaultColor()

        activity?.window?.navigationBarColor = color
    }

    private fun getDefaultColor(): Int {
        val colorValue = TypedValue()

        activity?.theme?.resolveAttribute(
                R.attr.colorPrimary,
                colorValue,
                true
        )

        return colorValue.data
    }

    companion object {
        const val TAG = "SettingFragment"

        const val favoriteTeamPreferenceKey = "favoriteTeam"
        const val startingScreenPreferenceKey = "startingScreen"
        const val favoriteTeamColorsPreferenceKey = "useFavoriteTeamColors"
        const val usernamePreferenceKey = "username"
    }
}