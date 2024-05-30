package com.example.myapplication

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

suspend fun fetchUsers(): ApiResponse? {
    val client = OkHttpClient() //
    val request = Request.Builder() //
        .url("https://reqres.in/api/users")
        .build()

    return withContext(Dispatchers.IO) {
        return@withContext try {
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                response.body?.string()?.let { responseBody ->
                    return@withContext Gson().fromJson(responseBody, ApiResponse::class.java) //
                }
            }
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
