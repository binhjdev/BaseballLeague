package dev.binhjdev.androidbasketballleague.ui.leaders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dev.binhjdev.androidbasketballleague.R
import dev.binhjdev.androidbasketballleague.databinding.FragmentLeadersBinding
import java.lang.IndexOutOfBoundsException

const val BATTING_LEADERS_FRAGMENT_INDEX = 0
const val PITCHING_LEADERS_FRAGMENT_INDEX = 1

class LeadersFragment : Fragment() {

    private lateinit var viewPagerLeaders: ViewPager2
    private lateinit var tabLayoutLeaders: TabLayout

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLeadersBinding.inflate(inflater)

        this.viewPagerLeaders = binding.viewPagerLeaders
        this.tabLayoutLeaders = binding.tabLayoutLeaders

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val leaderListAdapter = LeadersListAdapter(this)
        if (this::viewPagerLeaders.isInitialized && this::tabLayoutLeaders.isInitialized) {
            this.viewPagerLeaders.adapter = leaderListAdapter

            TabLayoutMediator(this.tabLayoutLeaders, this.viewPagerLeaders) { tab, position ->
                tab.icon = when (position) {
                    BATTING_LEADERS_FRAGMENT_INDEX -> context?.let { AppCompatResources.getDrawable(it, R.drawable.batter) }
                    PITCHING_LEADERS_FRAGMENT_INDEX -> context?.let { AppCompatResources.getDrawable(it, R.drawable.pitcher) }
                    else -> null
                }
                tab.text = when (position) {
                    BATTING_LEADERS_FRAGMENT_INDEX -> "Batting"
                    PITCHING_LEADERS_FRAGMENT_INDEX -> "Pitching"
                    else -> "N/A"
                }
            }.attach()
        }
    }
}


class LeadersListAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val leaderTypeMap: Map<Int, LeaderType> = mapOf(
            BATTING_LEADERS_FRAGMENT_INDEX to LeaderType.Batting,
            PITCHING_LEADERS_FRAGMENT_INDEX to LeaderType.Pitching
    )

    override fun getItemCount(): Int = leaderTypeMap.size

    override fun createFragment(position: Int): Fragment =
            leaderTypeMap[position]?.let { leaderType ->
                LeadersListFragment(leaderType)
            } ?: throw IndexOutOfBoundsException()
}

enum class LeaderType { Batting, Pitching }