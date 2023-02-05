package com.smkn9.semarang.sip_smk9.network

import android.content.Context
import android.view.View
import com.google.gson.GsonBuilder
import com.smkn9.semarang.sip_smk9.helper.Constant
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceNetwork {

    fun getService(view: View): ServiceClient{
        val sp = view.context.getSharedPreferences(Constant.MASTER_SISWA_HEADER, Context.MODE_PRIVATE)
        val serverSiswa = sp.getString(Constant.SISWA_SERVER,"")

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder().setLenient().create()

        val retrofit = Retrofit.Builder()
            .baseUrl(""+serverSiswa)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val service = retrofit.create(ServiceClient::class.java)

        return service

    }
}