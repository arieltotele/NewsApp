package com.example.newsapp.repository

import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.data.db.ArticleDatabase
import com.example.newsapp.data.entity.Article

class NewsRepository(val db: ArticleDatabase) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun getNewsSearched(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.getNewsByKeyWord(searchQuery, pageNumber)

    suspend fun upsertArticle(article: Article) = db.getArticleDao().upsertArticle(article)

    fun getNewsSaved() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}