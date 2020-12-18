package com.pasosync.pasosyncconnect.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pasosync.pasosyncconnect.BaseApplication
import com.pasosync.pasosyncconnect.repositories.MainRepository

class MainViewModelProvidefactory(
    val app:Application,
    val mainRepository: MainRepository
): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModels(app,mainRepository) as T
    }
}