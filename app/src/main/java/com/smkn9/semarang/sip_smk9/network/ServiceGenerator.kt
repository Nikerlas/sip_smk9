package com.smkn9.semarang.sip_smk9.network

import com.google.gson.GsonBuilder

import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by pertambangan on 17/02/18.
 */

object ServiceGenerator {

    //ini server Induk ujian Gate 1
    private val BASE_URL = "https://script.google.com/macros/s/AKfycbwqSAdHu9uV1EGHS_joAw2_tYEZEkbeQ5jXiekR4pI5b-y-xI-n/"
    private val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .build()
    private val gson = GsonBuilder().setLenient().create()
    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    fun <S> createService(serviceClass: Class<S>): S {
        return retrofit.create(serviceClass)
    }
}


//https://script.google.com/macros/s/AKfycbyldop574S09ZwAsivetQ9-sYSGHhvAx06rZtsHD9eZtFDE-8cr/exec