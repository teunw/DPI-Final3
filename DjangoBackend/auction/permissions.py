from django.http import HttpRequest
from rest_framework import permissions
from rest_framework.request import Request

from auction.models import Auction, Bid


class IsAuctionOwnerOrReadonly(permissions.BasePermission):
    def has_object_permission(self, request:Request, view, obj: Auction):
        if request.method in permissions.SAFE_METHODS:
            return True

        return obj.creator == request.user


class IsBidOwnerOrReadonly(permissions.BasePermission):

    def has_object_permission(self, request:Request, view, obj: Bid):
        if request.method in permissions.SAFE_METHODS:
            return True

        return obj.bidder == request.user
