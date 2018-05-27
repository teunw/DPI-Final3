package nl.teun.dpi.client.app

import io.reactivex.Single
import javafx.scene.layout.GridPane
import nl.teun.dpi.client.models.BidModel
import nl.teun.dpi.client.models.UserModel
import nl.teun.dpi.common.contains
import nl.teun.dpi.common.data.Auction
import nl.teun.dpi.common.data.Bid
import nl.teun.dpi.common.data.User
import nl.teun.dpi.common.data.notifications.NewBidNotification
import nl.teun.dpi.common.data.replies.AuctionReply
import nl.teun.dpi.common.data.requests.AuctionRequest
import nl.teun.dpi.common.data.requests.NewBidRequest
import nl.teun.dpi.common.toJson
import nl.teun.dpi.server.communication.messaging.KBus
import nl.teun.dpi.server.communication.messaging.KBusRequestReply
import tornadofx.*
import kotlin.concurrent.thread

class AuctionList : View("Auction List") {

    override val root = GridPane()

    val auctions = mutableListOf<Auction>().observable()
    var selectedAuction = Auction(itemName = "test")
    val bids = mutableListOf<Bid>().observable()

    val newBidModel: BidModel by inject()
    val userModel: UserModel by inject()

    init {
        with(root) {
            hbox {
                listview(auctions) {
                    onUserSelect {
                        println("Set new auction to ${it.toJson()}")
                        this@AuctionList.selectedAuction = it
                        bids.removeAll { true }
                        bids.addAll(it.bids)
                    }
                }
                form {

                    label("Current bids")
                    listview(bids) { }

                    label("Bid on auction")
                    fieldset {
                        label("Username")
                        textfield (userModel.username) { }

                        label("Amount")
                        textfield (newBidModel.amount) { }
                    }
                    fieldset {
                        button("Bid on auction") {
                            action {
                                thread {
                                    println("Button clicked")
                                    val newBid = NewBidRequest(Bid(auction = selectedAuction, amount = 420, bidder = User(username = userModel.username.value)))
                                    KBus().sendMessage(newBid)
                                    println("Done")
                                }
                            }
                        }
                    }

                }
                form {

                }
            }
        }

        KBusRequestReply().requestMessage<AuctionRequest, AuctionReply>(AuctionRequest()) {
            this.auctions.removeAll { true }
            this.auctions.addAll(it.auctions)
        }

        KBus().subscribe<NewBidNotification> { newBid ->
            val auction = this.auctions.find { it.id == newBid.bid.auction?.id } ?: throw Exception("Auction not find for id ${newBid.bid.auction!!.id}")
            auction.bids.add(newBid.bid)
        }
    }

}