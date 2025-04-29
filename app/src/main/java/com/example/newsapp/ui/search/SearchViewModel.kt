package com.example.newsapp.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.model.Article
import com.example.newsapp.data.repository.NewsRepository
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: NewsRepository) : ViewModel() {

    private val _searchResults = MutableLiveData<List<Article>>()
    val searchResults: LiveData<List<Article>> = _searchResults

    fun searchNews(query: String) {
        viewModelScope.launch {
            try {
                val response = repository.searchNews(query)
                if (response.isSuccessful) {
                    _searchResults.value = response.body()?.articles ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Search error: ${e.localizedMessage}")
            }
        }
    }
}
