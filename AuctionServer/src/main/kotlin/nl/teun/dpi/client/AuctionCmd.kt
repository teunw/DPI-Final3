package nl.teun.dpi.client

import nl.teun.dpi.client.rest.AuthTokenRequestBody
import nl.teun.dpi.common.rest.AuthenticationHandler
import nl.teun.dpi.common.data.Auction
import nl.teun.dpi.common.data.Bid
import nl.teun.dpi.common.data.User
import nl.teun.dpi.common.data.notifications.NewBidNotification
import nl.teun.dpi.common.data.replies.AuctionReply
import nl.teun.dpi.common.data.replies.NewAuctionReply
import nl.teun.dpi.common.data.requests.AuctionRequest
import nl.teun.dpi.common.data.requests.NewAuctionRequest
import nl.teun.dpi.common.data.requests.NewBidNotificationRequest
import nl.teun.dpi.common.data.requests.NewBidRequest
import nl.teun.dpi.server.communication.messaging.KBus
import nl.teun.dpi.server.communication.messaging.KBusRequestReply
import java.util.*

class AuctionCmd {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            AuctionCmd().start(args)
        }
    }

    val scanner = Scanner(System.`in`)
    val authHandler = AuthenticationHandler()

    fun start(args: Array<String>) {
        println("Before you can continue, you should login")
        login()

        println("Getting auctions ....")

        val auctions: MutableList<Auction> = mutableListOf()
        KBusRequestReply()
                .requestMessage<AuctionRequest, AuctionReply>(AuctionRequest(), {
                    println("Current auctions:")
                    it.auctions.forEachIndexed { index, auction -> println("[$index] $auction") }

                    auctions.clear()
                    auctions.addAll(it.auctions)

                    println("Select an auction to view or type something non-numeric to start an auction")

                    val nextLine = scanner.nextLine()
                    val auctionId = nextLine.toIntOrNull()
                    try {
                        if (auctionId == null) {
                            createAuction(nextLine)
                        } else {
                            viewAuction(auctions[auctionId])
                        }
                    } catch (e:Exception) {
                        println(e.message)
                    }
                })
    }

    fun login() {
        println("Login using your username and password")
        print("Username: ")
        val username = scanner.nextLine()

        print("Password: ")
        val password = scanner.nextLine()

        try {
            authHandler.login(AuthTokenRequestBody(username, password))
        } catch (e: Exception) {
            println("Invalid username or password, try again")
            println()
            login()
        }
    }

    fun createAuction(itemName: String) {
        val newAuctionRequest = NewAuctionRequest(itemName, authHandler.token!!)
        KBusRequestReply().requestMessage<NewAuctionRequest, NewAuctionReply>(newAuctionRequest, {
            if (!it.accepted) {
                println("Auction could not be created")
                println(it.reason)
            } else {
                viewAuction(it.updatedAuction!!)
            }
        })
        AuctionCmd.main(emptyArray())
    }

    fun viewAuction(selectedAuction: Auction) {
        val req = NewBidNotificationRequest(selectedAuction.id)

        println("Subscribing to $selectedAuction")
        println("Current highest bid for this auction")
        selectedAuction.bids.sortByDescending { it.amount }
        println(selectedAuction.bids.first().toString())

        KBus().subscribe<NewBidNotification>({
            println("New bid by ${it.bid.bidder?.username ?: "NULL"}: €${it.bid.amount}")
        })
        val syntaxRegex = Regex("[0-9]+")
        while (!Thread.interrupted()) {
            println("You can bid by typing a number")

            val nextLine = scanner.nextLine()
            if (!syntaxRegex.matches(nextLine)) {
                println("Invalid syntax")
                continue
            }

            val amount = nextLine.toInt()
            val user = authHandler.getUser()
            val bid = Bid(amount = amount, auction = selectedAuction, bidder = user)
            KBus().sendMessage(NewBidRequest(bid))
        }
    }
}