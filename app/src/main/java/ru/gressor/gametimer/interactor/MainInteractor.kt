package ru.gressor.gametimer.interactor

import kotlinx.coroutines.flow.*
import ru.gressor.gametimer.entities.BaseTimer
import ru.gressor.gametimer.repository.TimersRepository
import java.text.SimpleDateFormat
import java.util.*

class MainInteractor(
    private val timersRepository: TimersRepository
) {
    private val _updateListFlow = MutableStateFlow(0L)
    val updateListFlow: StateFlow<Long> = _updateListFlow.asStateFlow()

    private val _timersList = mutableListOf<Flow<BaseTimer>>()
    val timersList: List<Flow<BaseTimer>> = _timersList
    init {
        updateTimersList()
    }

    fun storeTimer(secondsValue: Int) {
        val df = SimpleDateFormat("yyyy-MM-DD HH:mm:ss", Locale.getDefault())
        timersRepository.storeTimer(
            BaseTimer(
                nextId(),
                df.format(Date()),
                secondsValue
            )
        )
        updateTimersList()
    }

    private fun nextId(): Long =
        1L + (timersRepository.getAllTimers().map { it.id }.maxByOrNull { it } ?: 0L)

    private fun updateTimersList() {
        _timersList.clear()
        timersRepository.getAllTimers().forEach {
            _timersList.add(
                flow {
                    emit(it)
                }
            )
        }
        _updateListFlow.value = System.currentTimeMillis()
    }
}