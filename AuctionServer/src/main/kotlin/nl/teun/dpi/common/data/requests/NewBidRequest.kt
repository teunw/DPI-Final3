package nl.teun.dpi.common.data.requests

import nl.teun.dpi.common.data.Bid
import java.io.Serializable

data class NewBidRequest(
        val newBid: Bid
) : Serializable