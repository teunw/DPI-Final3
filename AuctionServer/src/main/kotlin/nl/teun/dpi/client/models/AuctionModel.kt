package nl.teun.dpi.client.models

import javafx.beans.property.*
import nl.teun.dpi.common.data.Auction
import tornadofx.*
import java.time.LocalDate

class AuctionModel(auction: Auction) : ItemViewModel<Auction>(auction) {
    val id = bind { SimpleIntegerProperty(auction.id) }
    val itemName = bind { SimpleStringProperty(auction.itemName) }
    val creator = bind { SimpleStringProperty(auction.creator?.username ?:  "") }
    val bids = bind { SimpleListProperty(auction.bids.map { BidModel(it) }.observable()) }
}