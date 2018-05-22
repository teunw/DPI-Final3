package nl.teun.dpi

import com.google.inject.Guice
import nl.teun.dpi.builder.AuctionTopicBuilder
import nl.teun.dpi.communication.CommunicationSubscriber
import nl.teun.dpi.communication.KBus
import nl.teun.dpi.data.Auction
import nl.teun.dpi.data.Bid
import nl.teun.dpi.data.User
import nl.teun.dpi.data.requests.AuctionDeleteRequest
import nl.teun.dpi.data.requests.NewAuctionRequest
import nl.teun.dpi.data.requests.NewBidRequest
import nl.teun.dpi.services.AuctionCache
import nl.teun.dpi.services.AuctionModule
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    val injector = Guice.createInjector(AuctionModule())
    val auctionCache = injector.getInstance(AuctionCache::class.java)

    KBus().subscribe<NewAuctionRequest> {
        auctionCache.addAuction(it.auction)
        println("Added new auction: ${it.auction.toJson()}")
    }

    KBus().subscribe<AuctionDeleteRequest> {
        auctionCache.removeAuction(it.auctionId)
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

    val allTopic = AuctionTopicBuilder("*", "*.*.*")
    CommunicationSubscriber(allTopic)
            .subscribeAdvanced<Any> { it, envelope ->
                println("Message was sent to the queue")
                println("${envelope.routingKey}: ${it.toJson()}")
            }

    thread {
        var i = 0
        while (!Thread.interrupted()) {
            val auction = Auction(itemName = "Dikke ferrari #$i", creator = User("Putin"))
            KBus().sendMessage(NewAuctionRequest(auction))
            println(auction.toJson())
            Thread.sleep(1000)
            i++
        }
    }
}