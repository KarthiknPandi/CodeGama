package com.example.newsapp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.data.repository.NewsRepository
import com.example.newsapp.data.db.NewsDatabase

class NewsRefreshWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {

            val db = NewsDatabase.getDatabase(applicationContext)
            val repository = NewsRepository(RetrofitInstance.api, db.articleDao())
            repository.getTopHeadlines()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

