package com.app.dentzadmin.data.network

import android.content.Context
import com.app.dentzadmin.data.model.AdminSentMessage
import com.app.dentzadmin.data.model.GroupMessages
import com.app.dentzadmin.data.model.LoginCallResponse
import com.app.dentzadmin.data.model.Status
import com.app.dentzadmin.data.model.UserGroupMessages
import com.app.dentzadmin.data.model.ViewReportsData
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface RetrofitClientAndEndPoints {


    @GET("login.php")
    suspend fun getLoginResponse(
        @Query("userId") userId: String, @Query("password") password: String
    ): Response<LoginCallResponse>

    @GET("sendpassword.php")
    suspend fun getPassword(@Query("userId") userId: String): Response<LoginCallResponse>

    @GET("updatefcmid.php")
    suspend fun sendFcmID(
        @Query("userId") userId: String, @Query("fcmId") fcmId: String
    ): Response<Status>

    @GET("savequestionid.php")
    suspend fun sendAnswer(
        @Query("messageid") messageid: String,
        @Query("questionid") questionid: String,
        @Query("userid") userid: String,
        @Query("groupid") groupid: String
    ): Response<Status>

    @GET("groupnames.php")
    suspend fun groupMessages(
    ): Response<GroupMessages>

    @GET("reports.php")
    suspend fun getReports(
    ): Response<ViewReportsData>

    @GET("userHomePage.php")
    suspend fun userGroupMessages(
        @Query("groupid") groupid: String, @Query("userid") userId: String
    ): Response<UserGroupMessages>

    @GET("getAdminSentMessage.php")
    suspend fun getAdminSentMessage(
    ): Response<AdminSentMessage>

    @GET("adminsent.php")
    suspend fun adminSentMessage(
        @Query("messageid") messageid: String, @Query("groupid") groupid: String
    ): Response<Status>

    /* Building Retrofit with Base URL */
    companion object {

        private var retrofitService: RetrofitClientAndEndPoints? = null
        fun getInstance(ctx: Context): RetrofitClientAndEndPoints {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder().baseUrl("http://192.168.0.105/Dentz/")
                    .addConverterFactory(GsonConverterFactory.create()).build()
                retrofitService = retrofit.create(RetrofitClientAndEndPoints::class.java)
            }
            return retrofitService!!
        }

    }
}