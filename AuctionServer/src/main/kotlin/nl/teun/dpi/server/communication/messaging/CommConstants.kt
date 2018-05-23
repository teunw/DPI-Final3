package nl.teun.dpi.server.communication.messaging

import java.nio.charset.Charset

const val QueueAddress = "teunwillems.nl"
const val AuctionExchange = "DpiAuctionExchange"
const val DefaultCharsetStr = "UTF-8"
class CommConstants {
    companion object {
        val DefaultCharset = Charset.forName(DefaultCharsetStr)!!
    }
}