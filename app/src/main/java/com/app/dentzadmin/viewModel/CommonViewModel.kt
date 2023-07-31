package com.app.dentzadmin.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.dentzadmin.data.model.MessageSentFromAdminToGroup
import com.app.dentzadmin.data.model.SampleResponse
import com.app.dentzadmin.data.model.SendData
import com.app.dentzadmin.dataBase.AppDatabase
import com.app.dentzadmin.repository.MainRepository
import com.app.dentzadmin.util.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommonViewModel constructor(private val authCheckRepository: MainRepository) : ViewModel() {

    private val _decryptedString = MutableLiveData<String>()
    val decryptedString: LiveData<String>
        get() = _decryptedString

    val loading = MutableLiveData<Boolean>()

    private val _responseContent = MutableLiveData<SampleResponse>()
    val responseContent: LiveData<SampleResponse>
        get() = _responseContent

    private val _errorMessage = MutableLiveData<String>()
    private val _inserted = MutableLiveData<String>()
    private val _sentFromAdmin = MutableLiveData<MessageSentFromAdminToGroup>()
    private val _groupName = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage
    val inserted: LiveData<String>
        get() = _inserted

    val sentFromAdmin: LiveData<MessageSentFromAdminToGroup>
        get() = _sentFromAdmin

    val groupName: LiveData<String>
        get() = _groupName

    val _isInserted = MutableLiveData<Boolean>(false)
    val isInserted: LiveData<Boolean>
        get() = _isInserted

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

    fun insertData(sendData: SendData, ctx: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var dataSource = AppDatabase.getDatabase(ctx).dataSourceDao()
                val messageList = dataSource.getData(sendData.message)
                var sentToServerGroups = sendData.groups
                if (messageList.isEmpty()) {
                    dataSource.insertData(sendData)
                } else {
                    val data = dataSource.getMessageData(sendData.message)
                    val getAddGroups = data.groups + "," + sendData.groups
                    dataSource.updateGroup(sendData.message, getAddGroups)
                    sentToServerGroups = getAddGroups
                }
                _inserted.postValue(sendData.message)
                val messageSentFromAdminToGroup = MessageSentFromAdminToGroup(
                    content = sendData.message, groups = sentToServerGroups
                )
                _sentFromAdmin.postValue(messageSentFromAdminToGroup)
            }
        }
    }

    fun insertDataFromFirebase(sendData: SendData, ctx: Context, size: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var dataSource = AppDatabase.getDatabase(ctx).dataSourceDao()
                dataSource.insertData(sendData)
                if (size == dataSource.getAllData().size) {
                    _isInserted.postValue(true)
                }
            }
        }
    }

    fun deletTable(ctx: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var dataSource = AppDatabase.getDatabase(ctx).dataSourceDao()
                dataSource.nukeTable()
            }
        }
    }

    fun getGroups(message: String, ctx: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var dataSource =
                    AppDatabase.getDatabase(ctx).dataSourceDao().getMessageData(message)
                if (dataSource == null) {
                    _groupName.postValue("")
                } else {
                    _groupName.postValue(dataSource.groups)
                }
            }
        }
    }

    private fun stopLoader() {
        loading.value = false
    }
}