package com.app.bloodbank.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.bloodbank.repository.MainRepository

@Suppress("UNCHECKED_CAST")
class CommonViewModelFactory constructor(private val authCheckRepository: MainRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CommonViewModel::class.java)) {
            CommonViewModel(this.authCheckRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}