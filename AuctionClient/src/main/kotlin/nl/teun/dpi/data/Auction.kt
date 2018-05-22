package nl.teun.dpi.data

data class Auction(
        val id: Int = -1,
        val itemName: String,
        val creator: User? = null,
        val bids: MutableList<Bid> = mutableListOf()
)