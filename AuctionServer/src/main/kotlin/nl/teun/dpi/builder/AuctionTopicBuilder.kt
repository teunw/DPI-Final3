package nl.teun.dpi.builder

import java.util.*

class AuctionTopicBuilder(
        val auctionTarget: String = "*",
        val messageType: String = "*",
        val idTarget: Int? = null
) {

    fun getIdTargetIfNotNull() = if (idTarget == null) "" else ".$idTarget"

    fun build() = "${this.auctionTarget}.${this.messageType}${this.getIdTargetIfNotNull()}"

    companion object {
        val auctionExchange = "DpiAuctionExchange"
    }
}