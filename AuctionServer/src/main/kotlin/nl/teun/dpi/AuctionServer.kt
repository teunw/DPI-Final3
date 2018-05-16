package nl.teun.dpi

import com.google.inject.Guice
import nl.teun.dpi.builder.AuctionTopicBuilder
import nl.teun.dpi.communication.CommunicationSubscriber
import nl.teun.dpi.data.Auction
import nl.teun.dpi.data.Bid
import nl.teun.dpi.services.AuctionCache
import nl.teun.dpi.services.AuctionModule

fun main(args: Array<String>) {
    val injector = Guice.createInjector(AuctionModule())
    val auctionCache = injector.getInstance(AuctionCache::class.java)

    val newAuctionTopic = AuctionTopicBuilder("auction", "newauction")
    CommunicationSubscriber(newAuctionTopic)
            .subscribe<Auction> {
                auctionCache.addAuction(it)
                println("Added new auction: ${it.toJson()}")
            }

    val deleteAuctionTopic = AuctionTopicBuilder("auction", "delete")
    CommunicationSubscriber(deleteAuctionTopic)
            .subscribe<Int> { auctionId ->
                auctionCache.removeAuction(auctionId)
                println("Removed auction #$auctionId")
            }

    val newBidTopic = AuctionTopicBuilder("auction", "submitnewbid")
    CommunicationSubscriber(newBidTopic)
            .subscribe<Bid> { newBid ->
                val auction = auctionCache.getAuctions().find { it.id == newBid.auction.id }
                        ?: throw Exception("Auction not found")

                val newBidCopy = newBid.copy(auction = auction)
                auction.bids.add(newBidCopy)
                println("Added new bid ${newBid.toJson()}")

                CommunicationSubscriber(AuctionTopicBuilder("auction", "newbid"))
                        .sendMessage(newBidCopy)
            }

    val allTopic = AuctionTopicBuilder("*", "*")
    CommunicationSubscriber(allTopic)
            .subscribeAdvanced<Any> { it, envelope ->
                println("Message was sent to the queue")
                println("${envelope.routingKey}: ${it.toJson()}")
            }
}