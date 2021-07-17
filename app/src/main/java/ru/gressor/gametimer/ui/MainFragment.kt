package ru.gressor.gametimer.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.gressor.gametimer.databinding.FragmentMainBinding

class MainFragment: BaseFragment<FragmentMainBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMainBinding.inflate(inflater, container, false)
}