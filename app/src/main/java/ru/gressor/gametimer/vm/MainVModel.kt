package ru.gressor.gametimer.vm

import androidx.lifecycle.ViewModel
import ru.gressor.gametimer.domain.ActiveTimer
import ru.gressor.gametimer.domain.MainInteractor
import ru.gressor.gametimer.domain.Ticker
import java.text.SimpleDateFormat
import java.util.*

class MainVModel(
    private val interactor: MainInteractor
) : ViewModel() {

    val updateListStatusFlow = interactor.updateListStatusFlow

    fun newTimer(time: String) {
        val df = SimpleDateFormat("MMM-dd HH:mm:ss", Locale.getDefault())
        interactor.storeTimer(
            ActiveTimer(
                UUID.randomUUID(),
                "Timer ${df.format(Date())}",
                Ticker(time.toLong() * 60_000)
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