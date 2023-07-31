package com.app.dentzadmin.data.network

import android.content.Context
import com.app.dentzadmin.data.model.SampleResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface RetrofitClientAndEndPoints {


    @GET("fac537afc54e53bb1d4b")
    suspend fun getResponseContent(): Response<SampleResponse>

    /* Building Retrofit with Base URL */
    companion object {

        private var retrofitService: RetrofitClientAndEndPoints? = null
        fun getInstance(ctx: Context): RetrofitClientAndEndPoints {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder().baseUrl("https://api.npoint.io/")
                    .addConverterFactory(GsonConverterFactory.create()).build()
                retrofitService = retrofit.create(RetrofitClientAndEndPoints::class.java)
            }
            return retrofitService!!
        }

    }
}