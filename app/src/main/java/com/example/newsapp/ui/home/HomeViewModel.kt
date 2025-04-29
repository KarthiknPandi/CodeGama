package com.example.newsapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.newsapp.data.repository.NewsRepository

class HomeViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    val newsHeadlines = repository.getTopHeadlines().cachedIn(viewModelScope)
}