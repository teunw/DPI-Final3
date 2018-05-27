package nl.teun.dpi.client

import nl.teun.dpi.common.data.Auction
import nl.teun.dpi.common.data.Bid
import nl.teun.dpi.common.data.User
import nl.teun.dpi.common.data.notifications.NewBidNotification
import nl.teun.dpi.common.data.replies.AuctionReply
import nl.teun.dpi.common.data.requests.AuctionRequest
import nl.teun.dpi.common.data.requests.NewBidNotificationRequest
import nl.teun.dpi.common.data.requests.NewBidRequest
import nl.teun.dpi.server.communication.messaging.KBus
import nl.teun.dpi.server.communication.messaging.KBusRequestReply
import java.util.*

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    println("Getting auctions ....")

    val auctions: MutableList<Auction> = mutableListOf()
    KBusRequestReply()
            .requestMessage<AuctionRequest, AuctionReply>(AuctionRequest(), {
                println("Got")
            })
    KBusRequestReply()
            .requestMessage<AuctionRequest, AuctionReply>(AuctionRequest(), {
                println("Current auctions:")
                var index = 0
                it.auctions.forEach {
                    println("[$index] $it")
                    index += 1
                }
                auctions.clear()
                auctions.addAll(it.auctions)

                println("Select an auction to view")

                var selectedAuction: Auction? = null
                do {
                    val auctionId = scanner.nextLine().toIntOrNull()
                    if (auctionId != null) {
                        selectedAuction = auctions[auctionId]
                        continue
                    }
                    println("Auction ID not an integer")
                } while (auctionId == null || selectedAuction == null)

                val req = NewBidNotificationRequest(selectedAuction!!.id)

                println("Subscribing to $selectedAuction")
                println("Current highest bid for this auction")
                selectedAuction.bids.sortByDescending { it.amount }
                println(selectedAuction.bids.first().toString())

                KBus().subscribe<NewBidNotification>({
                    println("New bid by ${it.bid.bidder!!.username}: €${it.bid.amount}")
                })
                val syntaxRegex = Regex("[0-9]+:[0-9]+")
                while (!Thread.interrupted()) {
                    println("You can bid with userid:amount")

                    val nextLine = scanner.nextLine()
                    if (!syntaxRegex.matches(nextLine)) {
                        println("Invalid syntax")
                        continue
                    }

                    val id = nextLine.split(":")[0]
                    val amount = nextLine.split(":")[1].toInt()
                    val bid = Bid(amount = amount, auction = selectedAuction, bidder = User(id = id.toInt(), username = ""))
                    KBus().sendMessage(NewBidRequest(bid))
                }
            })


}