package com.example.picproject.ui.frgs

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.picproject.R
import com.example.picproject.databinding.SavedFragmentBinding
import com.example.picproject.ui.adapters.SavedPhotoAdapter
import com.example.picproject.ui.vm.SavedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.saved_fragment.*

@AndroidEntryPoint
class SavedFragment : Fragment(R.layout.saved_fragment) {
    private var _binding: SavedFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SavedViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = SavedFragmentBinding.bind(view)

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics);
        val width = displayMetrics.widthPixels / 2
        val height: Int = width

        val adapter = SavedPhotoAdapter(width, height) {
            val action = SavedFragmentDirections.actionSavedFragmentToDetailFragment(it)
            findNavController().navigate(action)
        }

        val layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        binding.apply {
            saved_recycler.layoutManager = layoutManager
            saved_recycler.setHasFixedSize(true)
            saved_recycler.itemAnimator = null
            saved_recycler.adapter = adapter
        }

        viewModel.photos.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}