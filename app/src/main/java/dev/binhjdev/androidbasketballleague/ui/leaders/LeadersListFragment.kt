package dev.binhjdev.androidbasketballleague.ui.leaders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dev.binhjdev.androidbasketballleague.databinding.FragmentLeadersListBinding

class LeadersListFragment(private val leaderType: LeaderType) : Fragment() {
    private val leadersListViewModel by activityViewModels<LeadersListViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLeadersListBinding.inflate(inflater)

        val leadersAdapter = LeadersAdapter()

        with(binding.recyclerViewLeaderList) {
            adapter = leadersAdapter
        }

        val leadersList =
                if (leaderType == LeaderType.Batting) {
                    leadersListViewModel.battingLeaders
                } else {
                    leadersListViewModel.pitchingLeaders
                }

        binding.swipeRefreshLayoutLeaders.setOnRefreshListener {
            if (leaderType == LeaderType.Batting) {
                leadersListViewModel.refreshBattingLeaders()
            } else {
                leadersListViewModel.refreshPitchingLeaders()
            }
        }

        leadersList?.observe(viewLifecycleOwner) { leaders ->
            leadersAdapter.submitList(leaders)
            Log.d("leadersList", leaders.toString())
            binding.swipeRefreshLayoutLeaders.isRefreshing = false
        }

        leadersListViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrBlank()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
            binding.swipeRefreshLayoutLeaders.isRefreshing = false
        }
        return binding.root
    }
}