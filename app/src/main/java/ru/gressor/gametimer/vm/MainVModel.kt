package ru.gressor.gametimer.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.gressor.gametimer.interactor.ActiveTimer
import ru.gressor.gametimer.interactor.MainInteractor
import ru.gressor.gametimer.interactor.Ticker
import ru.gressor.gametimer.mapping.toActive
import ru.gressor.gametimer.mapping.toStated
import ru.gressor.gametimer.repository.StoredTimer
import ru.gressor.gametimer.ui.StatedTimer
import java.text.SimpleDateFormat
import java.util.*

class MainVModel(
    private val interactor: MainInteractor
) : ViewModel() {
    val timersStates: List<StateFlow<StatedTimer>>
        get() =
            interactor.timersList.map { flow ->
                flow.map { it.toStated() }
                    .stateIn(viewModelScope, SharingStarted.Eagerly, StoredTimer.empty.toStated())
            }
    val updateFlow: StateFlow<Long> = interactor.updateListFlow

    fun newTimer(time: String) {
        val df = SimpleDateFormat("yyyy-MM-DD HH:mm:ss", Locale.getDefault())
        interactor.storeTimer(
            ActiveTimer(
                UUID.randomUUID(),
                df.format(Date()),
                Ticker(time.toInt() * 60)
            )
        )
    }

    fun toggle(timer: StatedTimer) {

    }

    fun delete(timer: StatedTimer) {
        interactor.deleteTimer(timer.toActive())
    }
}