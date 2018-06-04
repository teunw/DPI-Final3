package nl.teun.dpi.client.rest

import java.io.Serializable

class AuthTokenRequestBody(
        val username: String,
        val password: String
) : Serializable