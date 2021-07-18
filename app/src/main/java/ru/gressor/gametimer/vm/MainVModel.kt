package ru.gressor.gametimer.vm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import ru.gressor.gametimer.interactor.ActiveTimer
import ru.gressor.gametimer.interactor.MainInteractor
import ru.gressor.gametimer.interactor.Ticker
import java.text.SimpleDateFormat
import java.util.*

class MainVModel(
    private val interactor: MainInteractor
) : ViewModel() {

    val timersList = interactor.timersList

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

    fun toggle(timer: ActiveTimer) {
        interactor.toggleTimerById(timer.id)
    }

    fun delete(timer: ActiveTimer) {
        interactor.deleteTimerById(timer.id)
    }
}