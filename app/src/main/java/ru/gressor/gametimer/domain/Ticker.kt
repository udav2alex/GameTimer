package ru.gressor.gametimer.domain

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Ticker(
    val startValue: Long,
    private val finishValue: Long = 0L,
    isRunning: Boolean = false,
    private val pauseMillis: Long = 1000L
) {
    private var job: Job? = null
    private var finished = false
    private var running = isRunning
    private var currentValue = startValue

    private val _flow: MutableStateFlow<TickerState> = MutableStateFlow(getUpdatedState())
    val flow: StateFlow<TickerState> = _flow.asStateFlow()

    init {
        if (isRunning) start()
    }

    val state: TickerState
        get() = getUpdatedState()

    fun start() {
        pushStateToFlow()

        if (finished) {
            resetValue()
            pushStateToFlow()
        }

        running = true
        finished = false

        job = CoroutineScope(Dispatchers.IO).launch {
            while (!finished && currentValue > finishValue) {
                delay(pauseMillis)
                currentValue--

                if (currentValue == 0L) {
                    finished = true
                    running = false
                }
                pushStateToFlow()
            }
        }
    }

    fun stop() {
        try {
            job?.cancel("Self cancelled...")
        } catch (e: Throwable) {
            // job cancellation
        }
        running = false
        pushStateToFlow()
    }

    private fun resetValue() {
        currentValue = startValue
    }

    private fun pushStateToFlow() {
        _flow.value = getUpdatedState()
    }

    private fun getUpdatedState() = TickerState(
        currentValue,
        startValue,
        finishValue,
        running,
        finished
    )
}