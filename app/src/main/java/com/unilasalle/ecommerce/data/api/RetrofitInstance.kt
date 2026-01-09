package com.unilasalle.ecommerce.data.api

import com.unilasalle.ecommerce.BuildConfig

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    val api: IFakeStoreApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IFakeStoreApi::class.java)
    }
}