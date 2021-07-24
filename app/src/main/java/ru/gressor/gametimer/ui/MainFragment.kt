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
import ru.gressor.gametimer.domain.ActiveTimer
import ru.gressor.gametimer.domain.MainInteractor
import ru.gressor.gametimer.repository.TimersRepositoryList
import ru.gressor.gametimer.vm.MainVModel
import ru.gressor.gametimer.vm.MainVModelFactory

class MainFragment : BaseFragment<FragmentMainBinding>(), MainRecyclerAdapter.ControlClickListener {
    private val vModel: MainVModel by lazy {
        ViewModelProvider(
            this, MainVModelFactory(MainInteractor(TimersRepositoryList()))
        ).get()
    }
    private lateinit var listener: ActiveTimerListener
    private lateinit var adapter: MainRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (context is ActiveTimerListener) {
            listener = context as ActiveTimerListener
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = MainRecyclerAdapter(this)

        with(binding) {
            timersRecycler.adapter = adapter
            timersRecycler.layoutManager = LinearLayoutManager(context)

            lifecycleScope.launch {
                vModel.updateListStatusFlow
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect { list ->
                        adapter.submitList(list)
                        listener.updateTimersList(list)
                    }
            }

            addTimerButton.setOnClickListener {
                val number = timeEditText.text.toString().toInt()
                if (number > 0) {
                    vModel.newTimer(timeEditText.text.toString())
                } else {
                    listener.renderError("Значение должно быть строго больше 0!")
                }
            }
        }
    }

    override fun toggleClick(timer: ActiveTimer) {
        vModel.toggle(timer)
    }

    override fun deleteClick(timer: ActiveTimer) {
        vModel.delete(timer)
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMainBinding.inflate(inflater, container, false)

    interface ActiveTimerListener {
        fun updateTimersList(list: List<ActiveTimer>)
        fun renderError(message: String)
    }
}