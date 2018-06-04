package nl.teun.dpi.client.rest

import com.google.gson.Gson
import nl.teun.dpi.common.fromJson
import nl.teun.dpi.common.rest.ApplicationJson
import nl.teun.dpi.common.rest.RestAddress
import nl.teun.dpi.common.toJson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class AuthenticationHandler {

    private val httpClient by lazy {
        OkHttpClient.Builder().build()
    }

    var token: String? = null
        private set
    var username: String? = null
        private set

    fun login(authTokenRequestBody: AuthTokenRequestBody): String {
        val authUrl = "$RestAddress/auth/token/"
        val body = RequestBody.create(ApplicationJson, authTokenRequestBody.toJson())
        val request = Request.Builder()
                .url(authUrl)
                .post(body)
                .build()
        val response = this.httpClient.newCall(request).execute()
        val token = Gson().fromJson<AuthTokenResponse>(response.body()!!.string()).token

        println("Got an auth token for user ${authTokenRequestBody.username}: $token")
        this.username = authTokenRequestBody.username
        this.token = token
        return token
    }

}