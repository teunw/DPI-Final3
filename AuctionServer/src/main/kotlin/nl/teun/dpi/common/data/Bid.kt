package nl.teun.dpi.common.data

data class Bid(
        val id: Int? = null,
        val amount: Int = 0,
        val auction: Auction? = null,
        val bidder: User? = null
) {
    override fun toString() = "€$amount by ${bidder?.username ?: ""}"
}