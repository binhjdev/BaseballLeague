package dev.binhjdev.androidbasketballleague.ui.standings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import dev.binhjdev.androidbasketballleague.databinding.FragmentStandingsBinding

class StandingsFragment : Fragment() {

    private val standingsViewModel by activityViewModels<StandingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentStandingsBinding.inflate(inflater)

        val standingsAdapter = StandingsAdapter()

        binding.recyclerViewStandingList.adapter = standingsAdapter

        binding.standingSwipeRefreshLayout.setOnRefreshListener {
            standingsViewModel.refreshStandings()
        }

        standingsViewModel.standings.observe(viewLifecycleOwner) { standings ->
            standingsAdapter.addHeadersAndBuildStandings(standings)
            binding.standingSwipeRefreshLayout.isRefreshing = false
        }

        standingsViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrBlank()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                Log.d("errorMessFragment", errorMessage)
            }
            binding.standingSwipeRefreshLayout.isRefreshing = false
        }

        standingsViewModel.refreshStandings()

        return binding.root
    }
}