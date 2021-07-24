package ru.gressor.gametimer.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class MainInteractor(
    private val timersRepository: ITimersRepository
) {
    private val _updateListStatusFlow = MutableStateFlow(listOf<ActiveTimer>())
    val updateListStatusFlow: StateFlow<List<ActiveTimer>> =
        _updateListStatusFlow.asStateFlow()

    private val timersList = mutableListOf<ActiveTimer>()

    fun storeTimer(timer: ActiveTimer) {
        timersList.indexOf(timer).let {
            if (it < 0) {
                timersList.add(timer)
            }
        }
        _updateListStatusFlow.value = timersList.toList()
    }

    fun toggleTimerById(id: UUID) {
        timersList.find {
            it.id == id
        }?.let {
            if (it.ticker.state.isRunning) {
                it.ticker.stop()
            } else {
                timersList.forEach { timer ->
                    if (timer.ticker.state.isRunning) timer.ticker.stop()
                }
                it.ticker.start()
            }
            storeTimer(it)
        }
    }

    fun deleteTimerById(id: UUID) {
        timersList.find {
            it.id == id
        }?.let {
            timersList.remove(it)
            _updateListStatusFlow.value = timersList.toList()
        }
    }

    // TODO implement backup timers later
    private fun updateTimersList() {
        timersList.clear()
        timersList.addAll(timersRepository.getAllTimers())
        _updateListStatusFlow.value = timersList.toList()
    }
}