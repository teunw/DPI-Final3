package nl.teun.dpi.common.data.serializer

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import nl.teun.dpi.common.data.Bid
import java.lang.reflect.Type

class BidRestSerializer : JsonSerializer<Bid> {
    override fun serialize(src: Bid, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement =
        JsonObject().apply {
            if (src.id != -1) {
                addProperty("id", src.id)
            }
            addProperty("amount", src.amount)
            addProperty("auction", src.auction?.id)
            addProperty("bidder", src.bidder?.id)
        }
}