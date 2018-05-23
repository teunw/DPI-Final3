package nl.teun.dpi.common.data.replies

import nl.teun.dpi.common.data.Auction
import java.io.Serializable

data class AuctionReply(
        val auctions: List<Auction>
) : Serializable