package com.example.flickrview.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.flickrview.R
import com.example.flickrview.databinding.FragmentPhotoListBinding
import com.example.flickrview.domain.model.Photo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "PhotoListFragment"

@AndroidEntryPoint
class PhotoListFragment : Fragment() {

    private var _binding: FragmentPhotoListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PhotoListViewModel by viewModels()
    private lateinit var photoAdapter: PhotoPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        _binding = FragmentPhotoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        setupRecyclerView()
        setupSearchView()
        observePhotos()
        observeLoadState()
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoPagingAdapter { photo ->
            Log.d(TAG, "Photo clicked: ${photo.id}")
            openPhotoDetail(photo)
        }

        val spanCount = resources.getInteger(R.integer.photo_grid_columns)
        Log.d(TAG, "Grid columns: $spanCount")

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), spanCount)
            adapter = photoAdapter
            setHasFixedSize(true)
        }

        // SwipeRefresh
        binding.swipeRefresh.setOnRefreshListener {
            Log.d(TAG, "Refresh triggered")
            photoAdapter.refresh()
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotBlank()) {
                        Log.d(TAG, "Searching for: $it")
                        viewModel.searchPhotos(it.trim())
                        binding.searchView.clearFocus()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun observePhotos() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.photos.collectLatest { pagingData ->
                Log.d(TAG, "Submitting paging data")
                photoAdapter.submitData(pagingData)
            }
        }
    }

    private fun observeLoadState() {
        viewLifecycleOwner.lifecycleScope.launch {
            photoAdapter.loadStateFlow.collectLatest { loadStates ->
                Log.d(TAG, "LoadState - refresh: ${loadStates.refresh}, append: ${loadStates.append}")
                Log.d(TAG, "Adapter item count: ${photoAdapter.itemCount}")

                // Показываем/скрываем прогресс
                val isLoading = loadStates.refresh is LoadState.Loading
                binding.progressBar.isVisible = isLoading && photoAdapter.itemCount == 0
                binding.swipeRefresh.isRefreshing = isLoading && photoAdapter.itemCount > 0

                // Обработка ошибок
                val errorState = loadStates.refresh as? LoadState.Error
                    ?: loadStates.append as? LoadState.Error
                    ?: loadStates.prepend as? LoadState.Error

                errorState?.let {
                    Log.e(TAG, "Error loading photos", it.error)
                    Toast.makeText(
                        requireContext(),
                        "Error: ${it.error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                val isEmpty = loadStates.refresh is LoadState.NotLoading
                        && photoAdapter.itemCount == 0
                binding.emptyTextView.isVisible = isEmpty

                if (isEmpty) {
                    Log.d(TAG, "No photos loaded")
                }
            }
        }
    }

    private fun openPhotoDetail(photo: Photo) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PhotoDetailFragment.newInstance(photo))
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}