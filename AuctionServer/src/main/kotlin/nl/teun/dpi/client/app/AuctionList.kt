package nl.teun.dpi.client.app

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import javafx.collections.FXCollections
import javafx.scene.Parent
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import nl.teun.dpi.common.data.Auction
import nl.teun.dpi.common.data.Bid
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
    var selectedAuction = Single.create<Auction> { Auction(itemName = "test") }
    val bids = mutableListOf<Bid>().observable()

    val newBidAmountTextField : TextField? = null

    init {
        with(root) {
            hbox {
                listview(auctions) {
                    onUserSelect {
                        println("Set new auction to ${it.toJson()}")
                        selectedAuction.to { it }
                        bids.removeAll { true }
                        bids.addAll(it.bids)
                    }
                }
                form {

                    label("Current bids")
                    listview(bids) {  }

                    label("Bid on auction")
                    fieldset {
                        label("Amount")
                        textfield(newBidAmountTextField) {
                        }
                    }
                    fieldset {
                        button("Bid on auction") {
                            action {
                                println("Button clicked")
                                val newBid = NewBidRequest(Bid(amount = ))
                                KBus().sendMessage(amountfield)
                                KBusRequestReply().requestMessage<AuctionRequest, AuctionReply>(AuctionRequest(), {
                                    println("Auction currently going on: ")
                                    it.auctions.forEach { println("${it.itemName} by ${it.creator}") }
                                })
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
    }

}