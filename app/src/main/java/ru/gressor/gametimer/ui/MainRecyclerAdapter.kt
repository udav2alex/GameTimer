package ru.gressor.gametimer.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import ru.gressor.gametimer.databinding.ItemMainRecyclerTimerBinding
import ru.gressor.gametimer.interactor.ActiveTimer
import ru.gressor.gametimer.mapping.secondsToString

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
        holder.prepareForVisibility()
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: TimerViewHolder) {
        holder.prepareForInvisibility()
        super.onViewDetachedFromWindow(holder)
    }

    override fun getItemCount() = itemsList.size

    inner class TimerViewHolder(private val binding: ItemMainRecyclerTimerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var timer: ActiveTimer? = null
        private var job: Job? = null

        @InternalCoroutinesApi
        fun bind(timer: ActiveTimer) {
            this.timer = timer
            with(binding) {
                deleteButton.setOnClickListener {
                    controlListener.deleteClick(timer)
                }
                timerToggleButton.setOnClickListener {
                    controlListener.toggleClick(timer)
                }
                timerTextView.text = timer.ticker.flow.value.secondsToString()
            }
        }

        fun prepareForVisibility() {
            job = CoroutineScope(Dispatchers.Main).launch {
                timer?.let {
                    it.ticker.flow
                        .collect { seconds ->
                            binding.timerTextView.text = seconds.secondsToString()
                            println(seconds)
                        }
                }
            }
        }

        fun prepareForInvisibility() {
            try {
                job?.cancel()
            } catch (e: Throwable) { }
        }
    }

    interface ControlClickListener {
        fun toggleClick(timer: ActiveTimer)
        fun deleteClick(timer: ActiveTimer)
    }
}