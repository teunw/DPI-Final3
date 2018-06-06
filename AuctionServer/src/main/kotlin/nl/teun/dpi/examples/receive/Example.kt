package nl.teun.dpi.examples.receive

import nl.teun.dpi.examples.LoggerRequest
import nl.teun.dpi.server.communication.messaging.KBus
import nl.teun.dpi.server.communication.messaging.KBusRequestReply
import java.util.*

fun main(args:Array<String>) {

    val scanner = Scanner(System.`in`)
    while (!Thread.interrupted()) {
        val str = scanner.nextLine()

        KBus().sendMessage(LoggerRequest(str))
    }

}