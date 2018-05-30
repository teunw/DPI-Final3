import django_filters
from django.contrib.auth.models import User
from rest_framework import viewsets, generics

from auction.models import Auction, Bid
from auction.serializers import AuctionSerializer, BidSerializer, UserSerializer


class UserViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows users to be viewed or edited.
    """
    queryset = User.objects.all()
    serializer_class = UserSerializer
    filter_backends = (django_filters.rest_framework.DjangoFilterBackend,)


class AuctionViewSet(viewsets.ModelViewSet):
    queryset = Auction.objects.all()
    serializer_class = AuctionSerializer
    filter_backends = (django_filters.rest_framework.DjangoFilterBackend,)


class BidViewSet(viewsets.ModelViewSet):
    queryset = Bid.objects.all()
    serializer_class = BidSerializer
    filter_backends = (django_filters.rest_framework.DjangoFilterBackend,)
