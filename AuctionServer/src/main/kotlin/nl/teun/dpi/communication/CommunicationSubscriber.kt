package nl.teun.dpi.communication

import com.google.gson.Gson
import com.rabbitmq.client.*
import nl.teun.dpi.builder.AuctionTopicBuilder
import nl.teun.dpi.communication.CommConstants.Companion.AuctionExchange
import nl.teun.dpi.communication.CommConstants.Companion.DefaultCharset
import nl.teun.dpi.communication.CommConstants.Companion.QueueAddress
import nl.teun.dpi.fromJson
import nl.teun.dpi.toJson
import java.io.IOException
import java.nio.charset.Charset
import kotlin.concurrent.thread

@Deprecated("Use KBus")
class CommunicationSubscriber(
        private val auctionTopic: String
) {

    private val connection: Connection
    val channel: Channel
    val queueName: String

    constructor(auctionTopicBuilder: AuctionTopicBuilder) : this(auctionTopicBuilder.build())

    init {
        val connectionFactory = ConnectionFactory()
        connectionFactory.host = QueueAddress
        this.connection = connectionFactory.newConnection()

        this.channel = this.connection.createChannel()

        this.channel.exchangeDeclare(AuctionExchange, BuiltinExchangeType.TOPIC)
        this.channel.exchangeDeclare(AuctionExchange, "topic")
        this.queueName = channel.queueDeclare().queue

        this.channel.queueBind(this.queueName, AuctionExchange, this.auctionTopic)
    }


    fun sendMessage(obj: Any) {
        this.sendMessage(obj.toJson())
    }

    fun sendMessage(str: String) {
        this.channel.basicPublish(AuctionExchange, this.auctionTopic, null, str.toByteArray(DefaultCharset))
    }

    inline fun <reified T> subscribe(crossinline onMessage: (msg: T) -> Unit) {
        this.subscribeAdvanced<T> { msg, _ -> onMessage(msg) }
    }

    inline fun <reified T> subscribeAdvanced(crossinline onMessage: (msg: T, envelope: Envelope) -> Unit) {
        val consumer = object : DefaultConsumer(channel) {
            @Throws(IOException::class)
            override fun handleDelivery(consumerTag: String, envelope: Envelope,
                                        properties: AMQP.BasicProperties?, body: ByteArray?) {
                val message = String(body!!, DefaultCharset)
                val obj = Gson().fromJson<T>(message)
                onMessage(obj, envelope)
            }
        }
        channel.basicConsume(queueName, true, consumer)
    }

    companion object {

    }
}