from django.contrib.auth.models import User
from rest_framework import serializers

from auction.models import Auction, Bid


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('url', 'id', 'username')


class BidSerializer(serializers.ModelSerializer):
    class Meta:
        model = Bid
        fields = ('url', 'id', 'amount', 'bidder', 'auction')


class SimpleUserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('url', 'id', 'username')


class SimpleBidSerializer(serializers.ModelSerializer):
    bidder = SimpleUserSerializer(
        read_only=True
    )

    class Meta:
        model = Bid
        fields = ('url', 'id', 'amount', 'bidder')


class AuctionSerializer(serializers.ModelSerializer):
    bids = SimpleBidSerializer(
        many=True,
        read_only=True
    )

    creator = SimpleUserSerializer(
        read_only=True
    )

    class Meta:
        model = Auction
        fields = ('url', 'id', 'itemName', 'creator', 'bids')
