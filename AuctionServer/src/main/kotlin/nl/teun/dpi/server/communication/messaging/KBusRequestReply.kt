package nl.teun.dpi.server.communication.messaging

import com.google.gson.Gson
import com.rabbitmq.client.*
import nl.teun.dpi.server.communication.messaging.CommConstants.Companion.DefaultCharset
import nl.teun.dpi.common.fromJson
import nl.teun.dpi.common.printDebug
import nl.teun.dpi.common.replaceLast
import nl.teun.dpi.common.toJson
import java.io.IOException
import java.util.*

class KBusRequestReply {

    private val connection: Connection
    val channel: Channel
    val requestQueue: String
    val replyQueue: String

    init {
        val connectionFactory = ConnectionFactory()
        connectionFactory.host = QueueAddress
        this.connection = connectionFactory.newConnection()

        this.channel = this.connection.createChannel()

        this.channel.exchangeDeclare(AuctionExchange, BuiltinExchangeType.TOPIC)
        this.channel.exchangeDeclare(AuctionExchange, "topic")
        this.requestQueue = channel.queueDeclare().queue
        this.replyQueue = channel.queueDeclare().queue
    }

    inline fun <reified ReqT : Any, reified RepT : Any> requestMessage(obj: ReqT, crossinline onReply: (reply: RepT) -> Unit) {
        val uuid = UUID.randomUUID()
        val requestRoutingKey = "${this.getQueueNameForType<ReqT>()}.kbus.${this.getQueueNameForType<RepT>()}.$uuid.req"
        val replyRoutingKey = "${this.getQueueNameForType<ReqT>()}.kbus.${this.getQueueNameForType<RepT>()}.$uuid.rep"

        this.channel.queueBind(this.replyQueue, AuctionExchange, replyRoutingKey)

        // Process reply when ready
        val consumer = object : DefaultConsumer(channel) {
            @Throws(IOException::class)
            override fun handleDelivery(consumerTag: String, envelope: Envelope,
                                        properties: AMQP.BasicProperties?, body: ByteArray?) {
                val message = String(body!!, DefaultCharset)

                printDebug("Got from ${envelope.routingKey}")
                printDebug(message)

                val obj = Gson().fromJson<RepT>(message)
                onReply(obj)
            }
        }
        printDebug("Reading from: $replyRoutingKey")
        channel.basicConsume(replyQueue, true, consumer)

        // Send the request
        printDebug("Published to: $requestRoutingKey")
        printDebug(obj.toJson())
        this.channel.basicPublish(AuctionExchange, requestRoutingKey, null, obj.toJson().toByteArray(DefaultCharset))
    }

    inline fun <reified ReqT : Any, reified RepT : Any> setupReplier(crossinline replier: (req: ReqT) -> RepT) {
        val routingKey = "${this.getQueueNameForType<ReqT>()}.kbus.${this.getQueueNameForType<RepT>()}.*.req"

        this.channel.queueBind(this.requestQueue, AuctionExchange, routingKey)
        val consumer = object : DefaultConsumer(channel) {

            @Throws(IOException::class)
            override fun handleDelivery(consumerTag: String, envelope: Envelope,
                                        properties: AMQP.BasicProperties?, body: ByteArray?) {
                val message = String(body!!, DefaultCharset)
                val obj = Gson().fromJson<ReqT>(message)

                val replyObj = replier(obj)

                val publishRouting = envelope.routingKey.replaceLast("req", "rep")
                printDebug("Published to: $publishRouting")
                printDebug(replyObj.toJson())
                this.channel.basicPublish(AuctionExchange, publishRouting, null, replyObj.toJson().toByteArray(DefaultCharset))
            }

        }
        channel.basicConsume(requestQueue, true, consumer)
    }

    inline fun <reified T> getQueueNameForType(): String = T::class.qualifiedName!!

}