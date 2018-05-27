package nl.teun.dpi.client.models

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import nl.teun.dpi.common.data.Bid
import tornadofx.ItemViewModel

class BidModel(bid:Bid = Bid()) : ItemViewModel<Bid>(bid) {
    val id = bind { SimpleIntegerProperty(item.id ?: -1) }
    val amount = bind { SimpleIntegerProperty(item.amount) }
    val bidderUsername = bind { SimpleStringProperty(item.bidder?.username ?: "") }
}