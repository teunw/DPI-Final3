package nl.teun.dpi.common.data

import tornadofx.ItemViewModel
import tornadofx.getProperty
import tornadofx.property

data class Auction(
        val id: Int = -1,
        val itemName: String,
        val creator: User? = null,
        val bids: MutableList<Bid> = mutableListOf()
) {
    override fun toString() = "$itemName sold by ${creator?.username ?: ""}"
}