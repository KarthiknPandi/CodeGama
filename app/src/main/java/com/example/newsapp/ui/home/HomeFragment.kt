package com.example.newsapp.ui.home

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.data.repository.NewsRepository
import com.example.newsapp.databinding.FragmentHomeBinding
import com.example.newsapp.data.db.NewsDatabase
import com.example.newsapp.ui.profile.ProfileViewModel
import com.example.newsapp.utils.PrefManager
import com.example.newsapp.utils.SwipeGestureCallback


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var prefManager: PrefManager? = null
    private lateinit var newsAdapter: NewsPagerAdapter
    private lateinit var viewModel: HomeViewModel
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private val profileViewModel: ProfileViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = PrefManager(requireContext())

        val imageUri = prefManager?.getProfileImageUri(requireContext())
        if (!imageUri.isNullOrEmpty()) {
            Glide.with(this)
                .load(Uri.parse(imageUri))
                .into(binding.imageProfile)
        }

        shimmerFrameLayout = binding.shimmerViewContainer
        val db = NewsDatabase.getDatabase(requireContext())
        val repository = NewsRepository(RetrofitInstance.api, db.articleDao())
        viewModel = ViewModelProvider(this, HomeViewModelFactory(repository))[HomeViewModel::class.java]

        newsAdapter = NewsPagerAdapter(viewLifecycleOwner.lifecycleScope)
        binding.viewPager.adapter = newsAdapter

        shimmerFrameLayout.startShimmer()

        val itemTouchHelper = ItemTouchHelper(SwipeGestureCallback(requireContext(), newsAdapter, findNavController()))
        itemTouchHelper.attachToRecyclerView(binding.viewPager.getChildAt(0) as RecyclerView)

        binding.swipeRefreshLayout.setOnRefreshListener {
            newsAdapter.refresh()
        }

        lifecycleScope.launch {
            launch {
                viewModel.newsHeadlines.collectLatest { pagingData ->
                    newsAdapter.submitData(pagingData)
                }
            }

            launch {
                newsAdapter.loadStateFlow.collectLatest { loadStates ->
                    val isLoading = loadStates.refresh is LoadState.Loading
                    val isNotLoading = loadStates.refresh is LoadState.NotLoading
                    val isError = loadStates.refresh is LoadState.Error
                    val hasData = newsAdapter.itemCount > 0

                    if (isLoading) {
                        shimmerFrameLayout.startShimmer()
                        shimmerFrameLayout.visibility = View.VISIBLE
                        binding.viewPager.visibility = View.GONE
                    } else if (isNotLoading && hasData) {
                        shimmerFrameLayout.stopShimmer()
                        shimmerFrameLayout.visibility = View.GONE
                        binding.viewPager.visibility = View.VISIBLE
                        binding.swipeRefreshLayout.isRefreshing = false
                    } else if (isError) {
                        shimmerFrameLayout.stopShimmer()
                        shimmerFrameLayout.visibility = View.GONE
                        binding.viewPager.visibility = View.GONE
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }

        profileViewModel.profileImageUri.observe(viewLifecycleOwner) { uri ->
            if (uri != null)
                binding.imageProfile.setImageURI(uri)
             else
                binding.imageProfile.setImageResource(R.drawable.ic_profile)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
