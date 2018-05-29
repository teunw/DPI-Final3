package nl.teun.dpi.common.data.replies

import nl.teun.dpi.common.data.Auction
import java.io.Serializable

data class NewAuctionReply(
        val accepted: Boolean = false,
        val reason: String? = null,
        val updatedAuction: Auction
) : Serializable