package nl.teun.dpi.examples.requestreply

import nl.teun.dpi.examples.LoggerRequest
import nl.teun.dpi.server.communication.messaging.KBusRequestReply
import java.util.*

fun main(args:Array<String>) {

    val scanner = Scanner(System.`in`)
    while (!Thread.interrupted()) {
        val str = scanner.nextLine()

        KBusRequestReply().requestMessage<LoggerRequest, LoggerResponse>(LoggerRequest(str), {
            println("Got a message from server")
            println(it.message)
        })
    }

}