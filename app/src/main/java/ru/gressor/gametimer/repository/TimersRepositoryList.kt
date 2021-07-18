package ru.gressor.gametimer.repository

import ru.gressor.gametimer.interactor.ActiveTimer
import ru.gressor.gametimer.interactor.ITimersRepository
import ru.gressor.gametimer.mapping.toActive
import ru.gressor.gametimer.mapping.toStored

class TimersRepositoryList: ITimersRepository {
    private val list = mutableListOf<StoredTimer>()

    override fun storeTimer(timer: ActiveTimer) {
        val storedTimer = timer.toStored()

        list.indexOf(storedTimer).let {
            if (it > 0) {
                list[it] = storedTimer
            } else {
                list.add(storedTimer)
            }
        }
    }

    override fun deleteTimer(timer: ActiveTimer) {
        list.indexOf(timer.toStored()).let {
            if (it >= 0) {
                list.removeAt(it)
            }
        }
    }

    override fun getAllTimers(): List<ActiveTimer> = list.map { it.toActive() }
}