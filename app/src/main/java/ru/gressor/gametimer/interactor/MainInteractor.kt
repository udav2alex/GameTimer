package ru.gressor.gametimer.interactor

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class MainInteractor(
    private val timersRepository: ITimersRepository
) {
    private var pusher: Long = 0L
    private val _updateListStatusFlow = MutableStateFlow(pusher to listOf<ActiveTimer>())
    val updateListStatusFlow: StateFlow<Pair<Long, List<ActiveTimer>>> =
        _updateListStatusFlow.asStateFlow()

    private val _timersList = mutableListOf<ActiveTimer>()

    fun storeTimer(timer: ActiveTimer) {
        _timersList.indexOf(timer).let {
            if (it < 0) {
                _timersList.add(timer)
            }
        }
        _updateListStatusFlow.value = ++pusher to _timersList
    }

    fun toggleTimerById(id: UUID) {
        _timersList.find {
            it.id == id
        }?.let {
            if (it.ticker.running) {
                it.ticker.stop()
            } else {
                _timersList.forEach { timer ->
                    if (timer.ticker.running) timer.ticker.stop()
                }
                it.ticker.start()
            }
            storeTimer(it)
        }
    }

    fun deleteTimerById(id: UUID) {
        _timersList.find {
            it.id == id
        }?.let {
            _timersList.remove(it)
            _updateListStatusFlow.value = ++pusher to _timersList
        }
    }

    // TODO implement backup timers later
    private fun updateTimersList() {
        _timersList.clear()
        _timersList.addAll(timersRepository.getAllTimers())
        _updateListStatusFlow.value = ++pusher to _timersList
    }
}