package com.app.dentzadmin.repository

import android.content.Context
import com.app.dentzadmin.data.model.AdminSentMessage
import com.app.dentzadmin.data.model.GroupMessages
import com.app.dentzadmin.data.model.LoginCallResponse
import com.app.dentzadmin.data.model.Status
import com.app.dentzadmin.data.model.UserGroupMessages
import com.app.dentzadmin.data.model.ViewReportsData
import com.app.dentzadmin.data.network.RetrofitClientAndEndPoints
import com.app.dentzadmin.util.NetworkState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class MainRepository {


    /* API CALL To Get Login Response */
    suspend fun getLoginResponse(
        ctx: Context, userId: String, password: String
    ): Flow<NetworkState<LoginCallResponse>> {
        try {
            var response =
                RetrofitClientAndEndPoints.getInstance(ctx).getLoginResponse(userId, password)

            return if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    flow {
                        emit(NetworkState.Success(responseBody))
                    }
                } else {
                    flow {
                        emit(NetworkState.Error(response.message()))
                    }
                }
            } else {
                flow {
                    emit(NetworkState.Error(response.message()))
                }
            }
        } catch (e: Exception) {
            return flow {
                emit(NetworkState.Error(e.toString()))
            }
        }
    }

    /* API CALL To Get Forgot Password Response */
    suspend fun getForgotPasswordResponse(
        ctx: Context, userId: String
    ): Flow<NetworkState<LoginCallResponse>> {
        try {
            var response = RetrofitClientAndEndPoints.getInstance(ctx).getPassword(userId)

            return if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    flow {
                        emit(NetworkState.Success(responseBody))
                    }
                } else {
                    flow {
                        emit(NetworkState.Error(response.message()))
                    }
                }
            } else {
                flow {
                    emit(NetworkState.Error(response.message()))
                }
            }
        } catch (e: Exception) {
            return flow {
                emit(NetworkState.Error(e.toString()))
            }
        }
    }

    /* API CALL To Get send FCM Id */
    suspend fun sendFcmID(
        ctx: Context, userId: String, fcmId: String
    ) {
        var response = RetrofitClientAndEndPoints.getInstance(ctx).sendFcmID(userId, fcmId)
    }

    /* API CALL To  send Answer */
    suspend fun sendAnswer(
        ctx: Context, messageid: String, questionid: String, userid: String, groupid:String
    ) {
        var response =
            RetrofitClientAndEndPoints.getInstance(ctx).sendAnswer(messageid, questionid, userid,groupid)
    }

    /* API CALL To Get Messages and Groups */
    suspend fun groupMessages(
        ctx: Context
    ): Flow<NetworkState<GroupMessages>> {
        try {
            var response = RetrofitClientAndEndPoints.getInstance(ctx).groupMessages()

            return if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    flow {
                        emit(NetworkState.Success(responseBody))
                    }
                } else {
                    flow {
                        emit(NetworkState.Error(response.message()))
                    }
                }
            } else {
                flow {
                    emit(NetworkState.Error(response.message()))
                }
            }
        } catch (e: Exception) {
            return flow {
                emit(NetworkState.Error(e.toString()))
            }
        }
    }

    /* API CALL To Get Messages and Groups */
    suspend fun userGroupMessages(
        ctx: Context, groupId: String, userId: String
    ): Flow<NetworkState<UserGroupMessages>> {
        try {
            var response =
                RetrofitClientAndEndPoints.getInstance(ctx).userGroupMessages(groupId, userId)

            return if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    flow {
                        emit(NetworkState.Success(responseBody))
                    }
                } else {
                    flow {
                        emit(NetworkState.Error(response.message()))
                    }
                }
            } else {
                flow {
                    emit(NetworkState.Error(response.message()))
                }
            }
        } catch (e: Exception) {
            return flow {
                emit(NetworkState.Error(e.toString()))
            }
        }
    }

    /* API CALL To Get get Admin message */
    suspend fun getAdminSentMessage(
        ctx: Context
    ): Flow<NetworkState<AdminSentMessage>> {
        try {
            var response = RetrofitClientAndEndPoints.getInstance(ctx).getAdminSentMessage()

            return if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    flow {
                        emit(NetworkState.Success(responseBody))
                    }
                } else {
                    flow {
                        emit(NetworkState.Error(response.message()))
                    }
                }
            } else {
                flow {
                    emit(NetworkState.Error(response.message()))
                }
            }
        } catch (e: Exception) {
            return flow {
                emit(NetworkState.Error(e.toString()))
            }
        }
    }

    /* API CALL To Get send FCM Id */
    suspend fun adminSentMessage(
        ctx: Context, messageid: String, groupid: String
    ): Flow<NetworkState<Status>> {
        try {
            var response =
                RetrofitClientAndEndPoints.getInstance(ctx).adminSentMessage(messageid, groupid)

            return if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    flow {
                        emit(NetworkState.Success(responseBody))
                    }
                } else {
                    flow {
                        emit(NetworkState.Error(response.message()))
                    }
                }
            } else {
                flow {
                    emit(NetworkState.Error(response.message()))
                }
            }
        } catch (e: Exception) {
            return flow {
                emit(NetworkState.Error(e.toString()))
            }
        }
    }


    /* API CALL To Get Messages and Groups */
    suspend fun getReports(
        ctx: Context
    ): Flow<NetworkState<ViewReportsData>> {
        try {
            var response = RetrofitClientAndEndPoints.getInstance(ctx).getReports()

            return if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    flow {
                        emit(NetworkState.Success(responseBody))
                    }
                } else {
                    flow {
                        emit(NetworkState.Error(response.message()))
                    }
                }
            } else {
                flow {
                    emit(NetworkState.Error(response.message()))
                }
            }
        } catch (e: Exception) {
            return flow {
                emit(NetworkState.Error(e.toString()))
            }
        }
    }


}