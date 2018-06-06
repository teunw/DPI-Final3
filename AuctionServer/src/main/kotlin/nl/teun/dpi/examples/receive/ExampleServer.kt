package nl.teun.dpi.examples.receive

import nl.teun.dpi.examples.LoggerRequest
import nl.teun.dpi.server.communication.messaging.KBus
import nl.teun.dpi.server.communication.messaging.KBusRequestReply

fun main(args:Array<String>) {

    KBus().subscribe<LoggerRequest> {
        println("Got message from client")
        println(it.message)
    }

}