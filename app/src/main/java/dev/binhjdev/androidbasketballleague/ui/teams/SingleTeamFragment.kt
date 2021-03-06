package dev.binhjdev.androidbasketballleague.ui.teams

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import dev.binhjdev.androidbasketballleague.NavGraphDirections
import dev.binhjdev.androidbasketballleague.databinding.FragmentSingleTeamBinding


class SingleTeamFragment : Fragment() {

    private val args: SingleTeamFragmentArgs by navArgs()
    private val singleTeamViewModel by activityViewModels<SingleTeamViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentSingleTeamBinding
                .inflate(inflater, container, false)
                .apply {
                    vm = singleTeamViewModel.apply {
                        setTeam(args.teamId)
                    }
                    viewRosterButtonClickListener = View.OnClickListener { view ->
                        val action = NavGraphDirections.actionGoToTeamRoster(
                                args.teamId
                        )

                        view.findNavController().navigate(action)
                    }

                    lifecycleOwner = viewLifecycleOwner
                }

        return binding.root
    }
}