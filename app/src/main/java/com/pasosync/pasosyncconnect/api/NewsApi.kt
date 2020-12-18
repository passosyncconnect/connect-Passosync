package com.pasosync.pasosyncconnect.api

import com.pasosync.pasosyncconnect.models.NewsResponse
import com.pasosync.pasosyncconnect.other.ApiKey.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("v2/top-headlines")
    suspend fun getCricketNews(
        @Query("q")
        Query: String,
        @Query("category")
        category: String = "sports",
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>
}