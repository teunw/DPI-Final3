package nl.teun.dpi.common.data.serializer

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import nl.teun.dpi.common.data.Auction
import nl.teun.dpi.common.data.Bid
import nl.teun.dpi.server.communication.rest.RestAddress
import java.lang.reflect.Type

class AuctionSerializer : JsonSerializer<Auction> {
    override fun serialize(src: Auction, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonObject().apply {
            if (src.id != -1) {
                addProperty("id", src.id)
            }
            addProperty("itemName", src.itemName)
            addProperty("creator", src.creator?.id)
            addProperty("bids", "[]")
        }
    }
}
/*

 */