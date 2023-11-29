package com.example.frontend

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class AddToHistoryTask(private val context: Context): AsyncTask<String, Void, String>() {

    private lateinit var AUTH_TOKEN: String

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: String?): String {
        val userID = params[0] ?: return "No userID provided"
        val address = params[1] ?: return "No address provided"
        val timestamp = params[2] ?: return "No timestamp provided"

        try {
            val url = URL("http://navmtl-543ba0ee6069.herokuapp.com/history")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")

            // Use the AUTH_TOKEN obtained from shared preferences
            val sharedPrefs: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            AUTH_TOKEN = sharedPrefs.getString("token", null) ?: ""
            connection.setRequestProperty("Authorization", "Bearer $AUTH_TOKEN")

            val postData = "{\"userID\":\"$userID\",\"addresse\":\"$address\",\"temps\":$timestamp}"
            connection.doOutput = true
            val outputStream = DataOutputStream(connection.outputStream)
            outputStream.write(postData.toByteArray(Charsets.UTF_8))
            outputStream.flush()
            outputStream.close()

            val responseCode = connection.responseCode
            return if (responseCode == HttpURLConnection.HTTP_OK) {
                "Search added to history"
            } else {
                "Error adding search to history. Response code: $responseCode"
            }
        } catch (e: Exception) {
            return "Error adding search to history: ${e.message}"
        }
    }
}
