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
        val df = SimpleDateFormat("MMM-DD HH:mm:ss", Locale.getDefault())
        interactor.storeTimer(
            ActiveTimer(
                UUID.randomUUID(),
                "Timer ${df.format(Date())}",
                Ticker(time.toLong() * 60)
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