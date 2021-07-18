package ru.gressor.gametimer.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.StateFlow
import ru.gressor.gametimer.databinding.ItemMainRecyclerTimerBinding

class MainRecyclerAdapter(
    private val controlListener: ControlClickListener
) : RecyclerView.Adapter<MainRecyclerAdapter.TimerViewHolder>() {

    private val itemsList = mutableListOf<StateFlow<StatedTimer>>()

    fun populate(timersFlows: List<StateFlow<StatedTimer>>) {
        itemsList.clear()
        itemsList.addAll(timersFlows)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TimerViewHolder(
            ItemMainRecyclerTimerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    override fun getItemCount() = itemsList.size

    inner class TimerViewHolder(private val binding: ItemMainRecyclerTimerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(state: StateFlow<StatedTimer>) {
            with(binding) {
                deleteButton.setOnClickListener {
                    controlListener.deleteClick(state.value)
                }
                timerToggleButton.setOnClickListener {
                    controlListener.toggleClick(state.value)
                }
                timerTextView.text = state.value.time
            }
        }
    }

    interface ControlClickListener {
        fun toggleClick(timer: StatedTimer)
        fun deleteClick(timer: StatedTimer)
    }
}