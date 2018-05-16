package nl.teun.dpi.services

import com.google.inject.AbstractModule

class AuctionModule : AbstractModule() {
    override fun configure() {
        super.configure()

        bind(AuctionCache::class.java)
    }
}