package nl.teun.dpi.common.data.notifications

import nl.teun.dpi.common.data.Bid
import java.io.Serializable

data class NewBidNotification(
        val bid: Bid
) : Serializable