package nl.teun.dpi.common.rest

import nl.teun.dpi.common.data.User
import java.io.Serializable

data class AuthCheckResponse(
        val id: Int,
        val username: String
) : Serializable {
    fun toUser() = User(id = id, username = username)
}