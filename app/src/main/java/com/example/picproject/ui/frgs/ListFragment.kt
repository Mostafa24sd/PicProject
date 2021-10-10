package com.example.picproject.ui.frgs

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.picproject.R
import com.example.picproject.SortBy
import com.example.picproject.databinding.ListFragmentBinding
import com.example.picproject.ui.adapters.SearchPhotoAdapter
import com.example.picproject.ui.vm.ListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment(R.layout.list_fragment) {

    private val viewModel by viewModels<ListViewModel>()

    private val args: ListFragmentArgs by navArgs()

    private var _binding: ListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ListFragmentBinding.bind(view)

        setHasOptionsMenu(true)

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels / 2
        val height: Int = width

        val adapter = SearchPhotoAdapter(width, height) {
            val action = ListFragmentDirections.actionListFragmentToDetailFragment(it)
            findNavController().navigate(action)
        }

        val layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        binding.apply {
            listRecyclerview.layoutManager = layoutManager
            listRecyclerview.setHasFixedSize(true)
            listRecyclerview.itemAnimator = null
            listRecyclerview.adapter = adapter
        }

        viewModel.filteredData.observe(viewLifecycleOwner, {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        })

        when (args.type) {
            ListType.TOPIC -> (activity as AppCompatActivity?)?.supportActionBar?.title =
                "Topic: ${args.title}"
            ListType.USER -> (activity as AppCompatActivity?)?.supportActionBar?.title =
                "Photos by: ${args.title}"
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)

        val sortItem: MenuItem = menu.findItem(R.id.action_sort)

        sortItem.setOnMenuItemClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Sort By:")
            val sort = when (args.type) {
                ListType.TOPIC -> R.array.sort_list_topic
                ListType.USER -> R.array.sort_list_user
            }

            builder.setItems(sort) { dialog, which ->
                if (args.type == ListType.USER)
                    when (which) {
                        0 -> viewModel.setOrder(SortBy.LATEST)
                        1 -> viewModel.setOrder(SortBy.OLDEST)
                        2 -> viewModel.setOrder(SortBy.POPULAR)
                        3 -> viewModel.setOrder(SortBy.VIEWS)
                        4 -> viewModel.setOrder(SortBy.DOWNLOADS)
                    }
                else
                    when (which) {
                        0 -> viewModel.setOrder(SortBy.LATEST)
                        1 -> viewModel.setOrder(SortBy.OLDEST)
                        2 -> viewModel.setOrder(SortBy.POPULAR)
                    }
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

            true
        }

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

enum class ListType {
    USER, TOPIC
}