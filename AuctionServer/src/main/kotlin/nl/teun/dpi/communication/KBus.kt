package nl.teun.dpi.communication

import com.google.gson.Gson
import com.rabbitmq.client.*
import nl.teun.dpi.communication.CommConstants.Companion.AuctionExchange
import nl.teun.dpi.communication.CommConstants.Companion.DefaultCharset
import nl.teun.dpi.fromJson
import nl.teun.dpi.toJson
import java.io.IOException


class KBus {

    val rabbitUtil = RabbitUtil()

    inline fun <reified T : Any> sendMessage(obj: T) {
        rabbitUtil.channel.queueBind(rabbitUtil.queueName, AuctionExchange, T::class.java.canonicalName)
        rabbitUtil.channel.basicPublish(AuctionExchange, this.getQueueNameForType<T>(), null, obj.toJson().toByteArray(DefaultCharset))
    }

    inline fun <reified T : Any> subscribe(crossinline onMessage: (msg: T) -> Unit) {
        this.rabbitUtil.channel.queueBind(rabbitUtil.queueName, AuctionExchange, this.getQueueNameForType<T>())
        val consumer = object : DefaultConsumer(rabbitUtil.channel) {
            @Throws(IOException::class)
            override fun handleDelivery(consumerTag: String, envelope: Envelope,
                                        properties: AMQP.BasicProperties?, body: ByteArray?) {
                val message = String(body!!, DefaultCharset)
                val obj = Gson().fromJson<T>(message)
                onMessage(obj)
            }
        }
        rabbitUtil.channel.basicConsume(rabbitUtil.queueName, true, consumer)
    }

    inline fun <reified T> getQueueNameForType(): String = T::class.qualifiedName!!
}