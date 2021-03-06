from django.conf.urls import url, include
from rest_framework import routers
from rest_framework.authtoken import views as authviews
from .views import AuthUserViewset

from auction import views

router = routers.DefaultRouter()
router.register(r'users', views.UserViewSet)
router.register(r'auctions', views.AuctionViewSet)
router.register(r'bids', views.BidViewSet)

# Wire up our API using automatic URL routing.
# Additionally, we include login URLs for the browsable API.
urlpatterns = [
    url(r'^', include(router.urls)),
    url(r'^auth-user/', AuthUserViewset.as_view()),
    url(r'^auth/', include('rest_framework.urls', namespace='rest_framework')),
    url(r'^auth/token', authviews.obtain_auth_token)
]
