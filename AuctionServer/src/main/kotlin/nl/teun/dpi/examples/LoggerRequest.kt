package nl.teun.dpi.examples

import java.io.Serializable

data class LoggerRequest(
        val message: String
) : Serializable