package nl.teun.dpi.communication

import com.google.gson.Gson
import com.rabbitmq.client.*
import nl.teun.dpi.communication.CommConstants.Companion.AuctionExchange
import nl.teun.dpi.communication.CommConstants.Companion.DefaultCharset
import nl.teun.dpi.communication.CommConstants.Companion.QueueAddress
import nl.teun.dpi.fromJson
import nl.teun.dpi.toJson
import java.io.IOException
import java.util.*

class KBusRequestReply {

    private val connection: Connection
    val channel: Channel
    val queueName: String

    init {
        val connectionFactory = ConnectionFactory()
        connectionFactory.host = QueueAddress
        this.connection = connectionFactory.newConnection()

        this.channel = this.connection.createChannel()

        this.channel.exchangeDeclare(AuctionExchange, BuiltinExchangeType.TOPIC)
        this.channel.exchangeDeclare(AuctionExchange, "topic")
        this.queueName = channel.queueDeclare().queue
    }

    inline fun <reified ReqT : Any, reified RepT : Any> requestMessage(obj: ReqT, crossinline onReply: (reply: RepT) -> Unit) {
        val requestRoutingKey = "${this.getQueueNameForType<ReqT>()}.req.${this.getQueueNameForType<RepT>()}.${UUID.randomUUID()}.req"

        this.channel.queueBind(this.queueName, AuctionExchange, requestRoutingKey)
        val consumer = object : DefaultConsumer(channel) {
            @Throws(IOException::class)
            override fun handleDelivery(consumerTag: String, envelope: Envelope,
                                        properties: AMQP.BasicProperties?, body: ByteArray?) {
                val message = String(body!!, DefaultCharset)
                val obj = Gson().fromJson<RepT>(message)
                onReply(obj)
            }
        }
        channel.basicConsume(queueName, true, consumer)

        this.channel.basicPublish(AuctionExchange, requestRoutingKey, null, obj.toJson().toByteArray(DefaultCharset))
    }

    inline fun <reified ReqT : Any, reified RepT : Any> setupReplier(crossinline replier: (req: ReqT) -> RepT) {
        val routingKey = "${this.getQueueNameForType<ReqT>()}.req.${this.getQueueNameForType<RepT>()}.*"

        this.channel.queueBind(this.queueName, AuctionExchange, routingKey)
        val consumer = object : DefaultConsumer(channel) {

            @Throws(IOException::class)
            override fun handleDelivery(consumerTag: String, envelope: Envelope,
                                        properties: AMQP.BasicProperties?, body: ByteArray?) {
                val message = String(body!!, DefaultCharset)
                val obj = Gson().fromJson<ReqT>(message)

                val replyObj = replier(obj)

                this.channel.basicPublish(AuctionExchange, envelope.routingKey, null, replyObj.toJson().toByteArray(DefaultCharset))
            }

        }
        channel.basicConsume(queueName, true, consumer)
    }

    inline fun <reified T> getQueueNameForType(): String = T::class.qualifiedName!!
}