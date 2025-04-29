package com.example.newsapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.newsapp.api.NewsApiService
import com.example.newsapp.model.Article
import com.example.newsapp.model.NewsResponse
import com.example.newsapp.data.db.ArticleDao
import com.example.newsapp.data.db.ArticleEntity
import com.example.newsapp.data.paging.NewsPagingSource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class NewsRepository(
    private val apiService: NewsApiService,
    private val articleDao: ArticleDao
) {
    fun getTopHeadlines(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { NewsPagingSource(apiService, articleDao) }
        ).flow
    }

    suspend fun cacheArticles(articles: List<Article>) {
        val entities = articles.map {
            ArticleEntity(
                title = it.title,
                description = it.description,
                url = it.url,
                urlToImage = it.urlToImage,
                publishedAt = it.publishedAt
            )
        }
        articleDao.clearArticles()
        articleDao.insertArticles(entities)
    }

    suspend fun searchNews(query: String): Response<NewsResponse> {
        return apiService.searchNews(query)
    }


    fun getCachedArticles(): Flow<List<ArticleEntity>> = articleDao.getAllArticles()
}
