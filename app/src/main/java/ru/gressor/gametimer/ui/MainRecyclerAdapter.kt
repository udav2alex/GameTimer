package ru.gressor.gametimer.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import ru.gressor.gametimer.R
import ru.gressor.gametimer.databinding.ItemMainRecyclerTimerBinding
import ru.gressor.gametimer.domain.ActiveTimer
import ru.gressor.gametimer.domain.TickerState
import ru.gressor.gametimer.utils.secondsToString

class MainRecyclerAdapter(
    private val controlListener: ControlClickListener
) : ListAdapter<ActiveTimer, MainRecyclerAdapter.TimerViewHolder>(itemComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TimerViewHolder(
            ItemMainRecyclerTimerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    @InternalCoroutinesApi
    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewAttachedToWindow(holder: TimerViewHolder) {
        holder.prepareForVisibility()
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: TimerViewHolder) {
        holder.prepareForInvisibility()
        super.onViewDetachedFromWindow(holder)
    }

    private companion object {
        private val itemComparator = object : DiffUtil.ItemCallback<ActiveTimer>() {

            override fun areItemsTheSame(oldItem: ActiveTimer, newItem: ActiveTimer): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ActiveTimer, newItem: ActiveTimer): Boolean {
                return oldItem.id == newItem.id &&
                        oldItem.name == newItem.name
            }
        }
    }

    inner class TimerViewHolder(private val binding: ItemMainRecyclerTimerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var timer: ActiveTimer? = null
        private var job: Job? = null

        private val imageDrawable =
            AnimatedVectorDrawableCompat.create(itemView.context, R.drawable.animated_blinker)

        @InternalCoroutinesApi
        fun bind(timer: ActiveTimer) {
            this.timer = timer
            with(binding) {
                blinkerImageView.setImageDrawable(imageDrawable)

                deleteButton.setOnClickListener {
                    controlListener.deleteClick(timer)
                }
                timerToggleButton.setOnClickListener {
                    controlListener.toggleClick(timer)
                }

                progressCircle.currentValue = timer.ticker.flow.value.currentValue.toFloat()
                progressCircle.startValue = timer.ticker.startValue.toFloat()
                progressCircle.finishValue = 0f
                timerTextView.text = timer.ticker.flow.value.currentValue.secondsToString()

                configureViews(timer.ticker.state)
            }
        }

        fun prepareForVisibility() {
            job = CoroutineScope(Dispatchers.Main).launch {
                timer?.ticker?.let {
                    it.flow
                        .collect { state ->
                            with(binding) {
                                progressCircle.currentValue = state.currentValue.toFloat()
                                timerTextView.text = state.currentValue.secondsToString()

                                configureViews(state)
                            }
                        }
                }
            }
        }

        fun prepareForInvisibility() {
            try {
                job?.cancel()
            } catch (e: Throwable) {
            }
        }

        private fun ItemMainRecyclerTimerBinding.configureViews(state: TickerState) {
            if (state.isFinished) {
                itemView.setBackgroundColor(Color.RED)
            } else {
                itemView.setBackgroundColor(Color.WHITE)
            }
            if (state.isRunning) {
                imageDrawable?.start()
                blinkerImageView.visibility = View.VISIBLE
                timerToggleButton.text = root.context.getString(R.string.stop)
            } else {
                imageDrawable?.stop()
                blinkerImageView.visibility = View.INVISIBLE
                timerToggleButton.text = root.context.getString(R.string.start)
            }
        }
    }

    interface ControlClickListener {
        fun toggleClick(timer: ActiveTimer)
        fun deleteClick(timer: ActiveTimer)
    }
}