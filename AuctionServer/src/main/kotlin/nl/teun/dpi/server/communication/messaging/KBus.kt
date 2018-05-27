package nl.teun.dpi.server.communication.messaging

import com.google.gson.Gson
import com.rabbitmq.client.*
import nl.teun.dpi.server.communication.messaging.CommConstants.Companion.DefaultCharset
import nl.teun.dpi.common.fromJson
import nl.teun.dpi.common.toJson
import java.io.IOException


class KBus {

    val rabbitUtil = RabbitUtil()

    inline fun <reified T : Any> sendMessage(obj: T) {
        rabbitUtil.channel.queueBind(rabbitUtil.queueName, AuctionExchange, this.getQueueNameForType<T>())
        rabbitUtil.channel.basicPublish(AuctionExchange, this.getQueueNameForType<T>(), null, obj.toJson().toByteArray(DefaultCharset))
    }

    inline fun <reified T : Any> subscribe(crossinline onMessage: (msg: T) -> Unit) {
        this.rabbitUtil.channel.queueBind(rabbitUtil.queueName, AuctionExchange, this.getQueueNameForType<T>())
        rabbitUtil.channel.basicConsume(
                rabbitUtil.queueName,
                true,
                { _, message ->
                    try {
                        val message = String(message.body!!, DefaultCharset)
                        val obj = Gson().fromJson<T>(message)
                        onMessage(obj)
                    } catch (e:Exception) {
                        println("Consumer ex")
                        println(e.toJson())
                    }
                },
                { println("Cancel") },
                { consumerTag, sig ->
                    println("Shutdown ${sig.reason.protocolMethodName()}")
                })
    }

    inline fun <reified T> getQueueNameForType(): String = T::class.qualifiedName!!
}