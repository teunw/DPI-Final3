package nl.teun.dpi.builder

import java.util.*

@Deprecated("Use KBus")
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