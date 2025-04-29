package com.example.newsapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsapp.api.NewsApiService
import com.example.newsapp.model.Article
import com.example.newsapp.data.db.ArticleDao
import com.example.newsapp.data.db.ArticleEntity

class NewsPagingSource(private val apiService: NewsApiService,
                       private val articleDao: ArticleDao
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1
        return try {
            val response = apiService.getTopHeadlines(page = page, pageSize = 10)
            val articles = response.articles ?: emptyList()

            // Cache them
            if (page == 1) {
                articleDao.clearArticles()
                articleDao.insertArticles(articles.map {
                    ArticleEntity(
                        title = it.title,
                        description = it.description,
                        url = it.url,
                        urlToImage = it.urlToImage,
                        publishedAt = it.publishedAt
                    )
                })
            }

            LoadResult.Page(
                data = articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? = null
}
