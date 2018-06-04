package nl.teun.dpi.client.rest

import java.io.Serializable

data class AuthTokenResponse(
        val token: String
) : Serializable