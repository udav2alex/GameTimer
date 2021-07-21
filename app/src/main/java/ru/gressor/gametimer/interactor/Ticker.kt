package ru.gressor.gametimer.interactor

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Ticker(
    val startValue: Int,
    private val finishValue: Int = 0,
    isRunning: Boolean = false,
    private val pauseMillis: Long = 1000L
) {
    private var job: Job? = null

    private val _flow: MutableStateFlow<Int> = MutableStateFlow(startValue)
    var finished = false
        private set
    var isRunning = isRunning
        private set

    val flow: StateFlow<Int> = _flow.asStateFlow()

    private var currentValue = startValue
        set(value) {
            // TODO need to be checked?
            if (_flow.value != value) _flow.value = value
            field = value
        }

    init {
        if (isRunning) start()
    }

    fun start() {
        isRunning = true
        finished = false
        job = CoroutineScope(Dispatchers.IO).launch {
            while (!finished && currentValue != finishValue) {
                delay(pauseMillis)
                getValueThenNext()
            }
            finished = true
            isRunning = false
            resetValue()
        }
    }

    fun stop() {
        isRunning = false
        try {
            job?.cancel("Self cancelled...")
        } catch (e: Throwable) {
            // TODO
            println("Ticker Throwable: $e")
        }
    }

    private fun getValueThenNext(): Int {
        return currentValue.also { currentValue-- }
    }

    private fun resetValue() {
        currentValue = startValue
    }
}