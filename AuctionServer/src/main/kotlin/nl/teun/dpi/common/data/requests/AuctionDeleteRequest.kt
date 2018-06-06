package nl.teun.dpi.common.data.requests

import java.io.Serializable

data class AuctionDeleteRequest(
        val auctionId: Int
) : Serializable