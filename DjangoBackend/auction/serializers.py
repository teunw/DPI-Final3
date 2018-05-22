from django.contrib.auth.models import User
from rest_framework import serializers
from rest_framework.fields import Field

from auction.models import Auction, Bid


class UserSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = User
        fields = ('url', 'username')


class BidSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Bid
        fields = ('url', 'amount', 'bidder', 'auction')


class SimpleUserSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = User
        fields = ('url', 'username')


class SimpleBidSerializer(serializers.HyperlinkedModelSerializer):
    bidder = SimpleUserSerializer(
        read_only=True
    )

    class Meta:
        model = Bid
        fields = ('url', 'amount', 'bidder')


class AuctionSerializer(serializers.HyperlinkedModelSerializer):
    bids = SimpleBidSerializer(
        many=True,
        read_only=True
    )

    creator = SimpleUserSerializer(
        read_only=True
    )

    class Meta:
        model = Auction
        fields = ('url', 'itemName', 'creator', 'bids')
