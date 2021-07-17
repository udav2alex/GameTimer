package ru.gressor.gametimer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<B : ViewBinding>(test: Int) : Fragment() {
    private var _binding: B? = null
    private val binding get() = _binding!!

    var test2: Int

    init {
        test2 = test
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = getBinding(inflater, container).root

    abstract fun getBinding(inflater: LayoutInflater, container: ViewGroup?): B

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}