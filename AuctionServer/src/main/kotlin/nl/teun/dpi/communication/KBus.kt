package nl.teun.dpi.communication

import com.google.gson.Gson
import com.rabbitmq.client.*
import nl.teun.dpi.builder.AuctionTopicBuilder
import nl.teun.dpi.fromJson
import nl.teun.dpi.toJson
import java.io.IOException
import java.nio.charset.Charset


class KBus {

    private val connection: Connection
    val channel: Channel
    val queueName: String

    init {
        val connectionFactory = ConnectionFactory()
        connectionFactory.host = QueueAddress
        this.connection = connectionFactory.newConnection()

        this.channel = this.connection.createChannel()

        this.channel.exchangeDeclare(AuctionTopicBuilder.auctionExchange, BuiltinExchangeType.TOPIC)
        this.channel.exchangeDeclare(AuctionTopicBuilder.auctionExchange, "topic")
        this.queueName = channel.queueDeclare().queue
    }

    inline fun <reified T : Any> sendMessage(obj:T) {
        this.channel.queueBind(this.queueName, AuctionTopicBuilder.auctionExchange, T::class.java.canonicalName)
        this.channel.basicPublish(AuctionTopicBuilder.auctionExchange, this.getQueueNameForType<T>(), null, obj.toJson().toByteArray(DefaultCharset))
    }

    inline fun <reified T> subscribe(crossinline onMessage: (msg: T) -> Unit) {
        this.channel.queueBind(this.queueName, AuctionTopicBuilder.auctionExchange, this.getQueueNameForType<T>())
        val consumer = object : DefaultConsumer(channel) {
            @Throws(IOException::class)
            override fun handleDelivery(consumerTag: String, envelope: Envelope,
                                        properties: AMQP.BasicProperties?, body: ByteArray?) {
                val message = String(body!!, DefaultCharset)
                val obj = Gson().fromJson<T>(message)
                onMessage(obj)
            }
        }
        channel.basicConsume(queueName, true, consumer)
    }

    inline fun <reified T> getQueueNameForType():String = T::class.qualifiedName!!

    companion object {
        val QueueAddress = "teunwillems.nl"
        val DefaultCharset = Charset.forName("UTF-8")
    }
}