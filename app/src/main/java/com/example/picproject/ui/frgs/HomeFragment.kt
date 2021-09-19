package com.example.picproject.ui.frgs

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.picproject.R
import com.example.picproject.data.Resource
import com.example.picproject.databinding.HomeFragmentBinding
import com.example.picproject.ui.DEFAULT_LIST_KEY
import com.example.picproject.ui.DEFAULT_LIST_TYPE
import com.example.picproject.ui.adapters.TopicAdapter
import com.example.picproject.ui.vm.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.home_fragment) {
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = HomeFragmentBinding.bind(view)

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height: Int = width / 2

        val adapter = TopicAdapter(width, height) {
            val action =
                HomeFragmentDirections.actionHomeFragmentToListFragment(
                    it.id,
                    ListType.TOPIC,
                    it.title
                )
            findNavController().navigate(action)
        }

        viewModel.topic.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Error -> {
                }
                is Resource.Success -> {
                    binding.apply {
                        topicRecyclerview.setHasFixedSize(true)
                        topicRecyclerview.itemAnimator = null
                        topicRecyclerview.adapter = adapter
                        adapter.submitList(it.data)
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}