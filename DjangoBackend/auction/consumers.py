from channels.generic.websocket import WebsocketConsumer


class BidConsumer(WebsocketConsumer):

    def connect(self):
        self.accept()
        print("Websocket connect")

    def disconnect(self, close_code):
        print("Websocket disconnect")

    def receive(self, text_data, **kwargs):
        print("Received")
        print(text_data)
        self.send(text_data)
