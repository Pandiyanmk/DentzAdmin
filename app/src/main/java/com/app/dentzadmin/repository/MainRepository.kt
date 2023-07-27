package com.app.dentzadmin.repository

import android.content.Context
import com.app.dentzadmin.data.model.SampleResponse
import com.app.dentzadmin.data.network.RetrofitClientAndEndPoints
import com.app.dentzadmin.util.NetworkState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class MainRepository {


    /* API CALL To Get Response */
    suspend fun getResponse(ctx: Context): Flow<NetworkState<SampleResponse>> {
        try {
            var response = RetrofitClientAndEndPoints.getInstance(ctx).getResponseContent()

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