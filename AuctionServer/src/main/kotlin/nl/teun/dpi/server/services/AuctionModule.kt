package nl.teun.dpi.server.services

import com.google.inject.AbstractModule
import nl.teun.dpi.server.communication.rest.AuctionRestClient

class AuctionModule : AbstractModule() {
    override fun configure() {
        super.configure()

        bind(AuctionCache::class.java)
        bind(AuctionRestClient::class.java)
    }
}