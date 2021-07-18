package ru.gressor.gametimer.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.gressor.gametimer.entities.BaseTimer
import ru.gressor.gametimer.interactor.MainInteractor
import ru.gressor.gametimer.mapping.toBase
import ru.gressor.gametimer.mapping.toState
import ru.gressor.gametimer.states.TimerState

class MainVModel(
    private val interactor: MainInteractor
) : ViewModel() {
    val timersStates: List<StateFlow<TimerState>> get() =
        interactor.timersList.map { flow ->
            flow.map { it.toState() }
                .stateIn(viewModelScope, SharingStarted.Eagerly, BaseTimer.empty.toState())
        }
    val updateFlow: StateFlow<Long> = interactor.updateListFlow

    fun newTimer(time: String) {
        interactor.storeTimer(time.toInt() * 60)
    }

    fun toggle(timerState: TimerState) {

    }

    fun delete(timerState: TimerState) {
        interactor.deleteTimer(timerState.toBase())
    }
}