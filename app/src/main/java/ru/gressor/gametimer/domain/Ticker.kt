package ru.gressor.gametimer.domain

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Ticker(
    val startValue: Long,
    private val finishValue: Long = 0L,
    isRunning: Boolean = false,
    startedAtMillis: Long = 0
) {
    private var job: Job? = null
    private var finished = false
    private var running = isRunning
    private var currentValue = startValue

    private val _flow: MutableStateFlow<TickerState> = MutableStateFlow(getUpdatedState())
    val flow: StateFlow<TickerState> = _flow.asStateFlow()

    init {
        if (isRunning) {
            val before =
                if (startedAtMillis != 0L) System.currentTimeMillis() - startedAtMillis else 0
            currentValue = startValue - before
            start()
        }
    }

    var state: TickerState = getUpdatedState()
        private set

    fun start() {
        if (finished) {
            resetValue()
        }

        running = true
        finished = false
        pushStateToFlow()

        job = CoroutineScope(Dispatchers.IO).launch {
            var before = System.currentTimeMillis()
            while (!finished && currentValue > finishValue) {
                delay(PAUSE_MILLIS)
                val after = System.currentTimeMillis()

                currentValue -= after - before
                before = after

                if (currentValue <= 0L) {
                    currentValue = 0L
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
    ).also {
        state = it
    }

    private companion object {
        const val PAUSE_MILLIS = 50L
    }
}