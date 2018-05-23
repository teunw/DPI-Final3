package nl.teun.dpi.client

import javafx.application.Application
import nl.teun.dpi.client.app.AuctionForm
import nl.teun.dpi.client.app.AuctionStyle
import tornadofx.App
import nl.teun.dpi.server.communication.messaging.KBusRequestReply
import nl.teun.dpi.common.data.replies.AuctionReply
import nl.teun.dpi.common.data.requests.AuctionRequest

fun main(args: Array<String>) {
    KBusRequestReply().requestMessage<AuctionRequest, AuctionReply>(AuctionRequest(), {
        println("Auction currently going on: ")
        it.auctions.forEach { println("${it.itemName} by ${it.creator}") }
    })

    Application.launch(CustomerApp::class.java, *args)
}

class CustomerApp : App(AuctionForm::class, AuctionStyle::class)