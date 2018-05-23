package nl.teun.dpi.communication

import java.nio.charset.Charset

class CommConstants {
    companion object {
        val QueueAddress = "teunwillems.nl"
        val DefaultCharset = Charset.forName("UTF-8")
        val AuctionExchange = "DpiAuctionExchange"
    }
}