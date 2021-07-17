package ru.gressor.gametimer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import ru.gressor.gametimer.databinding.FragmentMainBinding
import ru.gressor.gametimer.interactor.MainInteractor
import ru.gressor.gametimer.repository.TimersRepositoryList
import ru.gressor.gametimer.states.TimerState
import ru.gressor.gametimer.vm.MainVModel
import ru.gressor.gametimer.vm.MainVModelFactory

class MainFragment : BaseFragment<FragmentMainBinding>(), MainRecyclerAdapter.ControlClickListener {
    private val vModel: MainVModel by lazy {
        ViewModelProvider(
            this, MainVModelFactory(
                MainInteractor(TimersRepositoryList())
            )
        ).get()
    }
    private lateinit var adapter: MainRecyclerAdapter

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = MainRecyclerAdapter(this)

        with(binding) {
            timersRecycler.adapter = adapter
            timersRecycler.layoutManager = LinearLayoutManager(context)

            lifecycleScope.launch {
                vModel.updateFlow
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect {
                        adapter.populate(vModel.timersStates)
                    }
            }

            addTimerButton.setOnClickListener {
                vModel.newTimer(timeEditText.text.toString())
            }
        }
    }

    override fun toggleClick(timerState: TimerState) {
        vModel.toggle(timerState)
    }

    override fun deleteClick(timerState: TimerState) {
        vModel.delete(timerState)
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMainBinding.inflate(inflater, container, false)
}