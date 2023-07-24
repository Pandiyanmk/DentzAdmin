package com.app.bloodbank.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bloodbank.data.model.SampleResponse
import com.app.bloodbank.repository.MainRepository
import com.app.bloodbank.util.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class CommonViewModel constructor(private val authCheckRepository: MainRepository) : ViewModel() {

    private val _decryptedString = MutableLiveData<String>()
    val decryptedString: LiveData<String>
        get() = _decryptedString

    val loading = MutableLiveData<Boolean>()

    private val _responseContent = MutableLiveData<SampleResponse>()
    val responseContent: LiveData<SampleResponse>
        get() = _responseContent

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    /* Get Content From Api */
    fun getResponseContent(ctx: Context) {
        viewModelScope.launch {
            authCheckRepository.getResponse(ctx).flowOn(Dispatchers.IO).catch { }
                .collect { response ->
                    stopLoader()
                    when (response) {
                        is NetworkState.Success -> {
                            _responseContent.value = response.data!!
                        }

                        is NetworkState.Error -> {
                            _errorMessage.value = response.errorMessage
                        }
                    }
                }
        }
    }

    private fun stopLoader() {
        loading.value = false
    }
}