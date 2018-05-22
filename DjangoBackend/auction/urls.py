from django.conf.urls import url, include
from django.contrib.auth.models import User
from rest_framework import routers, serializers, viewsets

from auction import views

router = routers.DefaultRouter()
router.register(r'users', views.UserViewSet)
router.register(r'auctions', views.AuctionViewSet)
router.register(r'bids', views.BidViewSet)

# Wire up our API using automatic URL routing.
# Additionally, we include login URLs for the browsable API.
urlpatterns = [
    url(r'^', include(router.urls)),
    url(r'^api-auth/', include('rest_framework.urls', namespace='rest_framework'))
]
