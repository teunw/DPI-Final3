package nl.teun.dpi.examples.requestreply

import java.io.Serializable

data class LoggerResponse(
        val message: String
) : Serializable