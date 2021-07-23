package ru.gressor.gametimer.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import ru.gressor.gametimer.R
import ru.gressor.gametimer.databinding.ItemMainRecyclerTimerBinding
import ru.gressor.gametimer.interactor.ActiveTimer
import ru.gressor.gametimer.interactor.Ticker
import ru.gressor.gametimer.utils.secondsToString

// TODO Migrate to ListAdapter
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

                progressCircle.currentValue = timer.ticker.flow.value.toFloat()
                progressCircle.startValue = timer.ticker.startValue.toFloat()
                progressCircle.finishValue = 0f
                timerTextView.text = timer.ticker.flow.value.secondsToString()

                configureViews(timer.ticker)
            }
        }

        fun prepareForVisibility() {
            job = CoroutineScope(Dispatchers.Main).launch {
                timer?.ticker?.let {
                    it.flow
                        .collect { seconds ->
                            with(binding) {
                                progressCircle.currentValue = seconds.toFloat()
                                timerTextView.text = seconds.secondsToString()

                                configureViews(it)
                            }
                        }
                }
            }
        }

        fun prepareForInvisibility() {
            try {
                job?.cancel()
            } catch (e: Throwable) { }
        }

        private fun ItemMainRecyclerTimerBinding.configureViews(ticker: Ticker) {
            if (ticker.finished) {
                itemView.setBackgroundColor(Color.RED)
            } else {
                itemView.setBackgroundColor(Color.WHITE)
            }
            if (ticker.running) {
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