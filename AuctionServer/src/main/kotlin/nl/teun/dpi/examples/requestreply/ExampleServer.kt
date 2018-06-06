package nl.teun.dpi.examples.requestreply

import nl.teun.dpi.examples.LoggerRequest
import nl.teun.dpi.server.communication.messaging.KBusRequestReply

fun main(args:Array<String>) {

    KBusRequestReply().setupReplier<LoggerRequest, LoggerResponse> {
        println("Got from client")
        println(it.message)
        LoggerResponse("Here is your message back: ${it.message}")
    }

}