package nl.teun.dpi.server.communication.rest

import com.google.gson.Gson
import nl.teun.dpi.common.data.Auction
import nl.teun.dpi.common.data.Bid
import nl.teun.dpi.common.data.serializer.AuctionSerializer
import nl.teun.dpi.common.data.serializer.BidRestSerializer
import nl.teun.dpi.common.fromJson
import nl.teun.dpi.common.toJson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class AuctionRestClient {

    var auctionCache: MutableList<Auction> = mutableListOf()
        private set

    val httpClient by lazy {
        OkHttpClient.Builder().build()!!
    }

    fun getAuctions(): List<Auction> {
        val request = Request.Builder()
                .url("$RestAddress/auctions/")
                .get()
                .build()
        val response = this.httpClient.newCall(request).execute()
        val auctions = Gson().fromJson<List<Auction>>(response.body()!!.string())

        this.auctionCache = auctions.toMutableList()
        return this.auctionCache
    }

    fun deleteAuction(auctionId: Int): Boolean {
        if (!this.auctionExists(auctionId)) {
            throw Exception("Auction does not exist")
        }
        this.auctionCache.removeIf { it.id == auctionId }

        val request = Request.Builder()
                .url("$RestAddress/auctions/$auctionId/")
                .delete()
                .build()
        val response = this.httpClient.newCall(request).execute()
        return response.isSuccessful
    }

    fun editAuction(auctionId: Int, auction: Auction): Boolean {
        if (!this.auctionExists(auctionId)) {
            throw Exception("Auction does not exist")
        }

        this.auctionCache.removeIf { it.id == auctionId }

        val requestBody = RequestBody.create(ApplicationJson, auction.toJson(AuctionSerializer()))
        val request = Request.Builder()
                .url("$RestAddress/auctions/$auctionId/")
                .put(requestBody)
                .build()
        val response = this.httpClient.newCall(request).execute()
        val jsonAuction = Gson().fromJson<Auction>(response.body()!!.string())

        this.auctionCache.add(jsonAuction)
        return response.isSuccessful
    }

    fun addAuction(auction: Auction): Boolean {
        if (this.auctionExists(auction)) {
            throw Exception("Auction already exists")
        }
        this.auctionCache.add(auction)

        val requestBody = RequestBody.create(ApplicationJson, auction.toJson(AuctionSerializer()))
        val request = Request.Builder()
                .url("$RestAddress/auctions/")
                .post(requestBody)
                .build()
        val response = this.httpClient.newCall(request).execute()
        return response.isSuccessful
    }

    fun auctionExists(auction: Auction) = this.auctionExists(auction.id)
    fun auctionExists(auctionId: Int) = this.auctionCache.count { it.id == auctionId } > 0

    fun addBid(newBidCopy: Bid): Boolean {
        val reqJson = newBidCopy.toJson(BidRestSerializer())
        val requestBody = RequestBody.create(ApplicationJson, reqJson)
        val request = Request.Builder()
                .url("$RestAddress/bids/")
                .post(requestBody)
                .build()
        val response = this.httpClient.newCall(request).execute()
        return response.isSuccessful
    }

    companion object {
        val ApplicationJson = MediaType.parse("application/json")
    }
}