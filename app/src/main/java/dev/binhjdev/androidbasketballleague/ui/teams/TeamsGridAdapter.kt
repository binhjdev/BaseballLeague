package dev.binhjdev.androidbasketballleague.ui.teams

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import dev.binhjdev.androidbasketballleague.NavGraphDirections
import dev.binhjdev.androidbasketballleague.R
import dev.binhjdev.androidbasketballleague.databinding.TeamGridItemBinding


class TeamsGridAdapter(
    private val teams: List<UITeam>
) : RecyclerView.Adapter<TeamsGridAdapter.TeamViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder =
        TeamViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.team_grid_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(teams[position])
    }

    override fun getItemCount() = teams.size

    inner class TeamViewHolder(
        private val binding: TeamGridItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UITeam) {
            binding.apply {
                team = item
                binding.clickListener = View.OnClickListener { view ->
                    val action = NavGraphDirections.actionGoToTeam(
                        item.teamId,
                        item.teamName
                    )
                    view.findNavController().navigate(action)
                }
            }
        }
    }
}