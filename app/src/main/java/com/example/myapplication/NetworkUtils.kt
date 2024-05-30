package com.example.myapplication

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

//---------Using Gson Library-----------

/*suspend fun fetchUsers(): ApiResponse? {
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
}*/


//------------------Without using Gson Library-----------------

suspend fun fetchUsers(): ApiResponse? {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://reqres.in/api/users")
        .build()

    return withContext(Dispatchers.IO) {
        return@withContext try {
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                response.body?.string()?.let { responseBody ->
                    return@withContext parseApiResponse(responseBody)
                }
            }
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}

fun parseApiResponse(responseBody: String): ApiResponse {
    val page = extractIntValue(responseBody, "\"page\":")
    val perPage = extractIntValue(responseBody, "\"per_page\":")
    val total = extractIntValue(responseBody, "\"total\":")
    val totalPages = extractIntValue(responseBody, "\"total_pages\":")

    val dataStartIndex = responseBody.indexOf("\"data\": [") + "\"data\": [".length
    val dataEndIndex = responseBody.indexOf("]", dataStartIndex)
    val dataArray = responseBody.substring(dataStartIndex, dataEndIndex + 1)

    val users = parseUsers(dataArray)

    val supportStartIndex = responseBody.indexOf("\"support\": {") + "\"support\": {".length
    val supportEndIndex = responseBody.indexOf("}", supportStartIndex)
    val supportObject = responseBody.substring(supportStartIndex, supportEndIndex + 1)

    val support = parseSupport(supportObject)

    return ApiResponse(page, perPage, total, totalPages, users, support)
}

fun extractIntValue(json: String, key: String): Int {
    val startIndex = json.indexOf(key) + key.length
    val endIndex = json.indexOf(",", startIndex)
    return json.substring(startIndex, endIndex).trim().toInt()
}

fun extractStringValue(json: String, key: String): String {
    val startIndex = json.indexOf(key) + key.length + 1
    val endIndex = json.indexOf("\"", startIndex + 1)
    return json.substring(startIndex, endIndex)
}

fun parseUsers(dataArray: String): List<User> {
    val users = mutableListOf<User>()
    var startIndex = 0

    while (startIndex < dataArray.length) {
        val id = extractIntValue(dataArray.substring(startIndex), "\"id\":")
        val email = extractStringValue(dataArray.substring(startIndex), "\"email\":")
        val firstName = extractStringValue(dataArray.substring(startIndex), "\"first_name\":")
        val lastName = extractStringValue(dataArray.substring(startIndex), "\"last_name\":")
        val avatar = extractStringValue(dataArray.substring(startIndex), "\"avatar\":")

        users.add(User(id, email, firstName, lastName, avatar))

        startIndex = dataArray.indexOf("{", startIndex + 1)
        if (startIndex == -1) break
    }

    return users
}

fun parseSupport(supportObject: String): Support {
    val url = extractStringValue(supportObject, "\"url\":")
    val text = extractStringValue(supportObject, "\"text\":")
    return Support(url, text)
}
