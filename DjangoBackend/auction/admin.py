from django.contrib import admin

# Register your models here.
from auction.models import Auction, Bid

admin.site.register(Auction)
admin.site.register(Bid)