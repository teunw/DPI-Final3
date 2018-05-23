package nl.teun.dpi.server.builder

@Deprecated("Use KBus")
class AuctionTopicBuilder(
        val auctionTarget: String = "*",
        val messageType: String = "*",
        val idTarget: Int? = null
) {

    fun getIdTargetIfNotNull() = if (idTarget == null) "" else ".$idTarget"

    fun build() = "${this.auctionTarget}.${this.messageType}${this.getIdTargetIfNotNull()}"

    companion object {
    }
}