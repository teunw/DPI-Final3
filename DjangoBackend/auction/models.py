from django.db import models
from django.contrib.auth.models import User


class Auction(models.Model):
    itemName = models.CharField(max_length=32)
    creator = models.ForeignKey(User, on_delete=models.SET_NULL, null=True)

    def __str__(self):
        return '{} by {}'.format(self.itemName, self.creator.username)


class Bid(models.Model):
    amount = models.IntegerField()
    bidder = models.ForeignKey(User, on_delete=models.SET_NULL, null=True)
    auction = models.ForeignKey(Auction, on_delete=models.SET_NULL, null=True, related_name="bids")

    def __str__(self):
        return '€{} by {} on {}\'s {}' \
            .format(self.amount,
                    self.bidder.username,
                    self.auction.creator.username,
                    self.auction.itemName)
