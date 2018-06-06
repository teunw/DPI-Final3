package nl.teun.dpi.common.data.requests

import java.io.Serializable

data class NewAuctionRequest(
        val itemName: String,
        val token: String
) : Serializable