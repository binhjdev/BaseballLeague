package dev.binhjdev.androidbasketballleague.ui.scoreboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import dev.binhjdev.androidbasketballleague.databinding.FragmentScoreBoardBinding

class ScoreBoardFragment : Fragment() {

    private val scoreboardViewModel by activityViewModels<ScoreboardViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding = FragmentScoreBoardBinding.inflate(inflater, container, false)
                .apply {
                    vm = scoreboardViewModel

                    ivScoreboardChangeYesterday.setOnClickListener { scoreboardViewModel.goToDate(-1) }
                    ivScoreboardChangeYesterday.setOnLongClickListener {
                        scoreboardViewModel.goToDate(monthsToMove = -1)
                        true
                    }

                    ivScoreboardChangeTomorrow.setOnClickListener { scoreboardViewModel.goToDate(1) }
                    ivScoreboardChangeTomorrow.setOnLongClickListener {
                        scoreboardViewModel.goToDate(monthsToMove = 1)
                        true
                    }

                    lifecycleOwner = viewLifecycleOwner
                }
        scoreboardViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}