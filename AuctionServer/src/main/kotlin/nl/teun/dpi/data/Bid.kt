package nl.teun.dpi.data

data class Bid(
        val id: Int?,
        val amount: Int,
        val auction: Auction
)