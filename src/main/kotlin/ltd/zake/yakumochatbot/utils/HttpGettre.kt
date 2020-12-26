package ltd.zake.yakumochatbot.utils

import okhttp3.OkHttpClient
import okhttp3.Request

class HttpGettre {
    fun httpGet(url: String): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()
        val response = client.newCall(request).execute()
        val body = response.body()?.string()
        return body
    }
}