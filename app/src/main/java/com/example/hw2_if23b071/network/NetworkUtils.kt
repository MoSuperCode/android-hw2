package com.example.hw2_if23b071.network

import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object NetworkUtils {
        private const val BASE_URL ="https://api.magicthegathering.io/v1/cards"
        private const val TAG ="NetworkUtils"

    fun fetchCardData(page: Int):String? {
        val urlString = "$BASE_URL?page=$page"
        Log.d(TAG, "Fetching from URL: $urlString")

        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection

        return try {
            connection.apply {
                requestMethod ="GET"
                readTimeout = 10000
                connectTimeout = 15000
            }
            when (connection.responseCode) {
                HttpURLConnection.HTTP_OK -> {
                    val response = connection.inputStream.bufferedReader().use {  it.readText()}
                    Log.d(TAG, "Successfully received respone")
                    response
                }
                else -> {
                    Log.e(TAG, "HTTP Error: ${connection.responseCode}")
                    null
                }
            }
        }catch (e: IOException) {
            Log.e(TAG,"Network Error", e)
            null
        } finally {
            connection.disconnect()
        }
    }
}