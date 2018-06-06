package nl.teun.dpi.common.data.requests

import java.io.Serializable

data class NewBidNotificationRequest(
        val auctionId: Int
) : Serializable