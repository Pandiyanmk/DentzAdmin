package com.app.bloodbank.repository

import android.content.Context
import com.app.bloodbank.data.model.SampleResponse
import com.app.bloodbank.data.network.RetrofitClientAndEndPoints
import com.app.bloodbank.util.NetworkState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response


class MainRepository {


    /* API CALL To Get Response */
    suspend fun getResponse(ctx: Context): Flow<NetworkState<SampleResponse>> {
        try {
            val sharedPreference = ctx.getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
            var response: Response<SampleResponse>? = null
            val isLan = sharedPreference.getString("isLanguage", "en")
            if (isLan.equals("en")) {
                response = RetrofitClientAndEndPoints.getInstance(ctx).getResponseContent()
            } else {
                response = RetrofitClientAndEndPoints.getInstance(ctx).getResponseContentNepali()
            }

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