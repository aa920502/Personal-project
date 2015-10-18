from tweepy import Stream
from tweepy import OAuthHandler
from tweepy.streaming import StreamListener

# This script collects tweets and writes to output file in json format

#consumer key, consumer secret, access token, access secret.
ckey="ducZHJELheVy9ZL8wEDzqczVT"
csecret="fbabQKlmc0z6fcv996BvwDUsMyK96hiV1pgxKSKshXSBdaVAbp"
atoken="2274893005-MwITPufGRi2p9zeMfJU8gp1tOq16ePGvKUripaB"
asecret="q2hsjzjYkwQb6tPLskkH0FAi2OxC7uuekKXxcKPddT1Av"

class listener(StreamListener):

    def on_data(self, data):
        try:
            with open('python.json', 'a') as f:
                f.write(data)
                return True
        except BaseException as e:
            print("Error on_data: %s" % str(e))
        return True

    def on_error(self, status):
        print status
        return True

auth = OAuthHandler(ckey, csecret)
auth.set_access_token(atoken, asecret)

twitterStream = Stream(auth, listener())
#twitterStream.filter(track=[])
twitterStream.sample()

