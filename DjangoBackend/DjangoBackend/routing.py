from channels.routing import ProtocolTypeRouter, URLRouter
import auction.routing

application = ProtocolTypeRouter({
    # (http->django views is added by default),
    'websocket': URLRouter(
        auction.routing.websocket_urlpatterns
    )
})
