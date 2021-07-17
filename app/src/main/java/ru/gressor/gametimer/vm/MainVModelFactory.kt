package ru.gressor.gametimer.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.gressor.gametimer.interactor.MainInteractor

class MainVModelFactory(
    private val mainInteractor: MainInteractor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(MainVModel::class.java)) {
            MainVModel(mainInteractor) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class $modelClass")
        }
}