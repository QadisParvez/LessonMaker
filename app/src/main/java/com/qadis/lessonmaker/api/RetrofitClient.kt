package com.qadis.lessonmaker.api

import com.qadis.lessonmaker.model.ConfigManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Updated to use configurable domain
    private fun getBaseUrl(): String {
        return ConfigManager.getApiBaseUrl()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val instance: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // Method to recreate client with new base URL
    fun updateBaseUrl(newBaseUrl: String) {
        ConfigManager.updateConfig(
            ConfigManager.getConfig().copy(apiBaseUrl = newBaseUrl)
        )
    }
}
