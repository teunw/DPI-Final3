package nl.teun.dpi.server.services

import com.google.inject.Singleton
import nl.teun.dpi.server.communication.rest.AuctionRestClient
import nl.teun.dpi.common.data.Auction
import nl.teun.dpi.common.toJson

@Singleton
class AuctionCache(
        private val auctions: MutableSet<Auction> = mutableSetOf()
) {
    val auctionRestClient = AuctionRestClient()

    fun init() {
        val restAuctions = this.auctionRestClient.getAuctions()
        println("Received from Django ${restAuctions.toJson()}")
        this.auctions.addAll(restAuctions)
    }

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