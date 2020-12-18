package com.pasosync.pasosyncconnect.repositories

import com.pasosync.pasosyncconnect.api.RetrofitInstance
import com.pasosync.pasosyncconnect.newsdb.ArticleDatabase

class MainRepository(
val newsdb:ArticleDatabase
) {

    suspend fun getCricketNews(query:String)=
        RetrofitInstance.api.getCricketNews(query)

}