package nl.teun.dpi.services

import com.google.inject.Singleton
import nl.teun.dpi.data.Auction

@Singleton
class AuctionCache(
        private val auctions: MutableSet<Auction> = mutableSetOf()
) {

    fun getAuctions() = auctions.toList()

    fun addAuction(auction: Auction) {
        if (auction.id > 0) {
            throw Exception("Auction already has an ID!")
        }
        val insertableAuction = auction.copy(id = this.auctions.size)
        this.auctions.add(insertableAuction)
    }

    fun removeAuction(auctionId: Int) = this.auctions.removeIf { it.id == auctionId }

    /**
     * Provide the edited auction but keep the ID the same
     */
    fun editAuction(auction: Auction) {
        this.removeAuction(auction.id)
        this.addAuction(auction)
    }
}