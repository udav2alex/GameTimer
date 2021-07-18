package ru.gressor.gametimer.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import ru.gressor.gametimer.databinding.ItemMainRecyclerTimerBinding
import ru.gressor.gametimer.interactor.ActiveTimer

class MainRecyclerAdapter(
    private val controlListener: ControlClickListener
) : RecyclerView.Adapter<MainRecyclerAdapter.TimerViewHolder>() {

    private val itemsList = mutableListOf<ActiveTimer>()

    fun populate(timersFlows: List<ActiveTimer>) {
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

    @InternalCoroutinesApi
    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    override fun onViewAttachedToWindow(holder: TimerViewHolder) {
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: TimerViewHolder) {
        super.onViewDetachedFromWindow(holder)
    }

    override fun getItemCount() = itemsList.size

    inner class TimerViewHolder(private val binding: ItemMainRecyclerTimerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @InternalCoroutinesApi
        fun bind(timer: ActiveTimer) {
            with(binding) {
                deleteButton.setOnClickListener {
                    controlListener.deleteClick(timer)
                }
                timerToggleButton.setOnClickListener {
                    controlListener.toggleClick(timer)
                }

//                localScope?.launchWhenStarted {
//                    timer.timeFlow
//                        .flowWithLifecycle(localLifecycle)
//                        .collect {
//                            timerTextView.text = it
//                        }
//                }
            }
        }
    }

    interface ControlClickListener {
        fun toggleClick(timer: ActiveTimer)
        fun deleteClick(timer: ActiveTimer)
    }
}