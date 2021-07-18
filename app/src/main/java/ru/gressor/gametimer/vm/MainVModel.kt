package ru.gressor.gametimer.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import ru.gressor.gametimer.interactor.ActiveTimer
import ru.gressor.gametimer.interactor.MainInteractor
import ru.gressor.gametimer.interactor.Ticker
import ru.gressor.gametimer.mapping.toActive
import ru.gressor.gametimer.mapping.toStated
import ru.gressor.gametimer.ui.StatedTimer
import java.text.SimpleDateFormat
import java.util.*

class MainVModel(
    private val interactor: MainInteractor
) : ViewModel() {

    val statedTimers: List<StatedTimer>
        get() = interactor.timersList.map { it.toStated(viewModelScope) }

    val updateListStatusFlow: StateFlow<Long> = interactor.updateListStatusFlow

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
        interactor.toggleTimerById(timer.id)
    }

    fun delete(timer: StatedTimer) {
        interactor.deleteTimerById(timer.id)
    }
}