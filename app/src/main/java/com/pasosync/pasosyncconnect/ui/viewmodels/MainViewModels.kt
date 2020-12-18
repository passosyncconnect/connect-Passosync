package com.pasosync.pasosyncconnect.ui.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasosync.pasosyncconnect.BaseApplication
import com.pasosync.pasosyncconnect.models.NewsResponse
import com.pasosync.pasosyncconnect.other.Resource
import com.pasosync.pasosyncconnect.repositories.MainRepository
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MainViewModels(
    val app: Application,
    private val mainRepository: MainRepository
) : AndroidViewModel(app) {
    val cricketNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    init {
getcricketNews("cricket")
    }

    fun getcricketNews(query:String)=viewModelScope.launch {
        safeBreakingNewsCall(query)
//        cricketNews.postValue(Resource.Loading())
//        val response=mainRepository.getCricketNews(query)
//        cricketNews.postValue(handleCricketNewsResponse(response))
    }
    private fun handleCricketNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        cricketNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = mainRepository.getCricketNews(countryCode)
                cricketNews.postValue(handleCricketNewsResponse(response))
            } else {
                cricketNews.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {

            when (t) {
                is IOException -> cricketNews.postValue(Resource.Error("Network Failure"))
                else -> cricketNews.postValue(Resource.Error("Conversion Failure"))
            }
        }
    }


    private fun hasInternetConnection(): Boolean { // you can check anywhere by this method
        val connectivityManager = getApplication<BaseApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetworkState = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetworkState) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }


        }

        connectivityManager.activeNetworkInfo.run {
            return when (type) {
                ConnectivityManager.TYPE_WIFI -> true
                ConnectivityManager.TYPE_MOBILE -> true
                ConnectivityManager.TYPE_ETHERNET -> true
                else -> false

            }


        }
        return false
    }
}