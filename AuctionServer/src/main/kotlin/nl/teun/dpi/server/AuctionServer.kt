package nl.teun.dpi.server

import com.google.inject.Guice
import nl.teun.dpi.server.builder.AuctionTopicBuilder
import nl.teun.dpi.server.builder.CommunicationSubscriber
import nl.teun.dpi.server.communication.messaging.KBus
import nl.teun.dpi.server.communication.messaging.KBusRequestReply
import nl.teun.dpi.server.communication.rest.AuctionRestClient
import nl.teun.dpi.common.data.replies.AuctionReply
import nl.teun.dpi.common.data.requests.*
import nl.teun.dpi.server.services.AuctionModule
import nl.teun.dpi.common.toJson

fun main(args: Array<String>) {
    val injector = Guice.createInjector(AuctionModule())
    val auctionCache = injector.getInstance(AuctionRestClient::class.java)

    CommunicationSubscriber(AuctionTopicBuilder("*", "*.*.*"))
            .subscribeAdvanced<Any> { it, envelope ->
                println("Message was sent to the queue")
                println("${envelope.routingKey}: ${it.toJson()}")
            }

    KBusRequestReply().setupReplier<AuctionRequest, AuctionReply> {
        AuctionReply(auctionCache.getAuctions())
    }

    KBus().subscribe<NewAuctionRequest> {
        auctionCache.addAuction(it.auction)
        println("Added new auction: ${it.auction.toJson()}")
    }

    KBus().subscribe<AuctionDeleteRequest> {
        auctionCache.deleteAuction(it.auctionId)
        println("Removed auction #${it.auctionId}")
    }

    KBus().subscribe<NewBidRequest> {
        val newBid = it.newBid
        val auction = auctionCache.getAuctions().find { it.id == newBid.auction.id }
                ?: throw Exception("Auction not found")

        val newBidCopy = newBid.copy(auction = auction)
        auction.bids.add(newBidCopy)
        println("Added new bid ${newBid.toJson()}")

        KBus().sendMessage(newBidCopy)
    }
}