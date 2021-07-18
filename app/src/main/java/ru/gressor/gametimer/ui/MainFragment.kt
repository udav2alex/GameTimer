package ru.gressor.gametimer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.gressor.gametimer.databinding.FragmentMainBinding
import ru.gressor.gametimer.interactor.MainInteractor
import ru.gressor.gametimer.repository.TimersRepositoryList
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = MainRecyclerAdapter(this)

        with(binding) {
            timersRecycler.adapter = adapter
            timersRecycler.layoutManager = LinearLayoutManager(context)

            lifecycleScope.launch {
                vModel.updateListStatusFlow
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect {
                        adapter.populate(vModel.statedTimers)
                    }
            }

            addTimerButton.setOnClickListener {
                vModel.newTimer(timeEditText.text.toString())
            }
        }
    }

    override fun toggleClick(timer: StatedTimer) {
        vModel.toggle(timer)
    }

    override fun deleteClick(timer: StatedTimer) {
        vModel.delete(timer)
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMainBinding.inflate(inflater, container, false)
}