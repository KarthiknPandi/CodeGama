package com.example.newsapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.newsapp.databinding.ItemNewsPageBinding
import com.example.newsapp.model.Article
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NewsPagerAdapter(
    private val lifecycleScope: CoroutineScope
) : PagingDataAdapter<Article, NewsPagerAdapter.NewsViewHolder>(DiffCallback) {


    inner class NewsViewHolder(val binding: ItemNewsPageBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsPageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = getItem(position)
        article?.let {
            holder.binding.apply {
                titleTextView.text = article.title
                descriptionTextView.text = article.description

                Glide.with(newsImageView.context)
                    .load(article.urlToImage)
                    .into(newsImageView)
            }
        }
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }
        }
    }


    fun removeItem(position: Int) {
        val currentList = snapshot().items.toMutableList()
        currentList.removeAt(position)

        lifecycleScope.launch {
            submitData(PagingData.from(currentList))
        }
    }

    fun getItemAtPosition(position: Int): Article {
        return snapshot().items[position]
    }

}