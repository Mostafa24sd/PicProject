package com.example.picproject.ui.frgs

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.picproject.R
import com.example.picproject.SortBy
import com.example.picproject.databinding.SearchFragmentBinding
import com.example.picproject.ui.DEFAULT_SEARCH_QUERY
import com.example.picproject.ui.DEFAULT_SEARCH_SORT
import com.example.picproject.ui.adapters.SearchPhotoAdapter
import com.example.picproject.ui.vm.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

val TAG = "mosix"

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.search_fragment) {

    private val viewModel by viewModels<SearchViewModel>()

    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = SearchFragmentBinding.bind(view)

        setHasOptionsMenu(true)

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels / 2
        val height: Int = width

        val adapter = SearchPhotoAdapter(width, height) {
            val action = SearchFragmentDirections.actionSearchFragmentToDetailFragment(it)
            findNavController().navigate(action)
        }

        val layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        binding.apply {
            searchRecycler.layoutManager = layoutManager
            searchRecycler.setHasFixedSize(true)
            searchRecycler.itemAnimator = null
            searchRecycler.adapter = adapter
        }

        viewModel.photos.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        (activity as AppCompatActivity?)?.supportActionBar?.title = "Search"
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        //Search
        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search photo"
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    DEFAULT_SEARCH_QUERY = it
                    viewModel.searchPhotos(DEFAULT_SEARCH_QUERY, DEFAULT_SEARCH_SORT)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        val sortItem: MenuItem = menu.findItem(R.id.action_sort)

        sortItem.setOnMenuItemClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Sort by::")
            val sizes = R.array.sort_search

            var query = searchView.query.toString()
            if (query.isNullOrEmpty())
                query = DEFAULT_SEARCH_QUERY

            builder.setItems(sizes) { dialog, which ->
                when (which) {
                    0 -> {
                        viewModel.searchPhotos(query, SortBy.LATEST)
                        DEFAULT_SEARCH_SORT = SortBy.LATEST
                    }
                    1 -> {
                        viewModel.searchPhotos(query, SortBy.POPULAR)
                        DEFAULT_SEARCH_SORT = SortBy.POPULAR
                    }
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