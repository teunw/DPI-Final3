package nl.teun.dpi.common.rest

import com.google.gson.Gson
import nl.teun.dpi.client.rest.AuthTokenRequestBody
import nl.teun.dpi.client.rest.AuthTokenResponse
import nl.teun.dpi.common.data.User
import nl.teun.dpi.common.fromJson
import nl.teun.dpi.common.toJson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import javax.naming.AuthenticationException

class AuthenticationHandler {

    private val httpClient by lazy {
        OkHttpClient.Builder().build()
    }

    var token: String? = null
        private set
    var username: String? = null
        private set
    var id: Int? = null
        private set

    fun getUser() = User(id = this.id!!, username = this.username!!)

    @Throws(AuthenticationException::class)
    fun login(authTokenRequestBody: AuthTokenRequestBody): String {
        val authUrl = "$RestAddress/auth/token/"
        val body = RequestBody.create(ApplicationJson, authTokenRequestBody.toJson())
        val request = Request.Builder()
                .url(authUrl)
                .post(body)
                .build()
        val response = this.httpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            throw AuthenticationException("Could not authenticate user ${authTokenRequestBody.username}")
        }

        val token = Gson().fromJson<AuthTokenResponse>(response.body()!!.string()).token

        println("Got an auth token for user ${authTokenRequestBody.username}: $token")
        this.username = authTokenRequestBody.username
        this.token = token
        this.checkLoginData()
        return token
    }

    fun checkLoginData(): AuthCheckResponse {
        val authUrl = "$RestAddress/auth-user/"
        val request = Request.Builder()
                .url(authUrl)
                .header("Authorization", "Token ${this.token!!}")
                .get()
                .build()
        val response = this.httpClient.newCall(request).execute()
        val check = Gson().fromJson<AuthCheckResponse>(response.body()!!.string())
        this.username = check.username
        this.id = check.id
        return check
    }

}