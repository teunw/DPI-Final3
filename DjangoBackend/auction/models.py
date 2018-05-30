from django.db import models
from django.contrib.auth.models import User


class Auction(models.Model):
    itemName = models.CharField(max_length=32)
    creator = models.ForeignKey(User, on_delete=models.SET_NULL, null=True, blank=True)

    def __str__(self):
        username = ""
        if self.creator is not None:
            username = " by {}".format(self.creator.username)
        return '{}{}'.format(self.itemName, username)


class Bid(models.Model):
    amount = models.IntegerField()
    bidder = models.ForeignKey(User, on_delete=models.SET_NULL, null=True)
    auction = models.ForeignKey(Auction, on_delete=models.SET_NULL, null=True, blank=True, related_name="bids")

    def __str__(self):
        username = ""
        if self.auction.creator is not None:
            username = self.auction.creator.username

        return '€{} by {} on: {}' \
            .format(self.amount,
                    self.bidder.username,
                    self.auction)
