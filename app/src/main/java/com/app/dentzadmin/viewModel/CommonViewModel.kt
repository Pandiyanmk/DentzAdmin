package com.app.dentzadmin.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.dentzadmin.data.model.AdminSentMessage
import com.app.dentzadmin.data.model.GroupMessages
import com.app.dentzadmin.data.model.LoginCallResponse
import com.app.dentzadmin.data.model.MessageSentFromAdminToGroup
import com.app.dentzadmin.data.model.SendData
import com.app.dentzadmin.data.model.UserGroupMessages
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

    private val _responseContent = MutableLiveData<LoginCallResponse>()
    val responseContent: LiveData<LoginCallResponse>
        get() = _responseContent

    private val _groupMessageContent = MutableLiveData<GroupMessages>()
    val groupMessageContent: LiveData<GroupMessages>
        get() = _groupMessageContent


    private val _userGroupMessageContent = MutableLiveData<UserGroupMessages>()
    val userGroupMessageContent: LiveData<UserGroupMessages>
        get() = _userGroupMessageContent

    private val _adminSentMessage = MutableLiveData<AdminSentMessage>()
    val adminSentMessage: LiveData<AdminSentMessage>
        get() = _adminSentMessage

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

    /* Get Login Content From Api */
    fun getLoginResponse(ctx: Context, userId: String, password: String) {
        viewModelScope.launch {
            authCheckRepository.getLoginResponse(ctx, userId, password).flowOn(Dispatchers.IO)
                .catch { }.collect { response ->
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

    /* Get Forgot Password Content From Api */
    fun getForgotPasswordResponse(ctx: Context, userId: String) {
        viewModelScope.launch {
            authCheckRepository.getForgotPasswordResponse(ctx, userId).flowOn(Dispatchers.IO)
                .catch { }.collect { response ->
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

    fun sendFcmId(ctx: Context, userId: String, fcmId: String) {
        viewModelScope.launch {
            authCheckRepository.sendFcmID(ctx, userId, fcmId)
        }
    }

    fun adminSentMessage(ctx: Context, messageid: String, groupid: String) {
        viewModelScope.launch {
            authCheckRepository.adminSentMessage(ctx, messageid, groupid)
        }
    }

    /* Get Login Content From Api */
    fun getMessagesandGroups(ctx: Context) {
        viewModelScope.launch {
            authCheckRepository.groupMessages(ctx).flowOn(Dispatchers.IO).catch { }
                .collect { response ->
                    stopLoader()
                    when (response) {
                        is NetworkState.Success -> {
                            _groupMessageContent.value = response.data!!
                        }

                        is NetworkState.Error -> {
                            _errorMessage.value = response.errorMessage
                        }
                    }
                }
        }
    }

    fun getUserMessagesandGroups(ctx: Context, groupId: String) {
        viewModelScope.launch {
            authCheckRepository.userGroupMessages(ctx, groupId).flowOn(Dispatchers.IO).catch { }
                .collect { response ->
                    stopLoader()
                    when (response) {
                        is NetworkState.Success -> {
                            _userGroupMessageContent.value = response.data!!
                        }

                        is NetworkState.Error -> {
                            _errorMessage.value = response.errorMessage
                        }
                    }
                }
        }
    }

    /* Get get AdminSent Message From Api */
    fun getAdminSentMessage(ctx: Context) {
        viewModelScope.launch {
            authCheckRepository.getAdminSentMessage(ctx).flowOn(Dispatchers.IO).catch { }
                .collect { response ->
                    stopLoader()
                    when (response) {
                        is NetworkState.Success -> {
                            _adminSentMessage.value = response.data!!
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
                val messageList = dataSource.getData(sendData.messageid)
                var sentToServerGroups = sendData.groupid
                if (messageList.isEmpty()) {
                    dataSource.insertData(sendData)
                } else {
                    val data = dataSource.getMessageData(sendData.messageid)
                    val getAddGroups = data.groupid + "," + sendData.groupid
                    dataSource.updateGroup(sendData.messageid, getAddGroups)
                    sentToServerGroups = getAddGroups
                }
                _inserted.postValue(sendData.messageid)
                val messageSentFromAdminToGroup = MessageSentFromAdminToGroup(
                    content = sendData.messageid, groups = sentToServerGroups
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
                    _groupName.postValue(dataSource.groupid)
                }
            }
        }
    }

    private fun stopLoader() {
        loading.value = false
    }
}