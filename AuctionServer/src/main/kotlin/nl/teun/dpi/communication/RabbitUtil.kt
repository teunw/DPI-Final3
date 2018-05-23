package nl.teun.dpi.communication

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

class RabbitUtil {
    private val connection: Connection
    val channel: Channel
    val queueName: String

    init {
        val connectionFactory = ConnectionFactory()
        connectionFactory.host = CommConstants.QueueAddress
        this.connection = connectionFactory.newConnection()

        this.channel = this.connection.createChannel()

        this.channel.exchangeDeclare(CommConstants.AuctionExchange, BuiltinExchangeType.TOPIC)
        this.channel.exchangeDeclare(CommConstants.AuctionExchange, "topic")
        this.queueName = channel.queueDeclare().queue
    }
}