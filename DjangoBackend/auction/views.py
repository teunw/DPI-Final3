from django.contrib.auth.models import User
from rest_framework import viewsets, permissions
from rest_framework import generics
from rest_framework.response import Response
from rest_framework.views import APIView

from auction.models import Auction, Bid
from auction.serializers import AuctionSerializer, BidSerializer, UserSerializer


class UserViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows users to be viewed or edited.
    """
    queryset = User.objects.all()
    serializer_class = UserSerializer


class AuctionViewSet(viewsets.ModelViewSet):
    queryset = Auction.objects.all()
    serializer_class = AuctionSerializer


class BidViewSet(viewsets.ModelViewSet):
    queryset = Bid.objects.all()
    serializer_class = BidSerializer


class AuthUserViewset(generics.GenericAPIView):
    serializer_class = UserSerializer
    permission_classes = (permissions.IsAuthenticated,)

    def get(self, request, *args, **kwargs):
        return Response(UserSerializer(instance=request.user, context={'request': request}).data)

