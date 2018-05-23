package nl.teun.dpi.common.data

data class Bid(
        val id: Int?,
        val amount: Int,
        val auction: Auction,
        val bidder: User?
) {
    override fun toString() = "€$amount by ${bidder?.username ?: ""}"
}