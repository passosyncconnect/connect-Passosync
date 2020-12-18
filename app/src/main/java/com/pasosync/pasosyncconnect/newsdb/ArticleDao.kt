package com.pasosync.pasosyncconnect.newsdb

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pasosync.pasosyncconnect.models.Article

@Dao
interface ArticleDao {

    /*onConflict is used when an article is already
        present in database it will gonna replace that database with nw one
       */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article):Long


    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}