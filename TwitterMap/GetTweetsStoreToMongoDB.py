import time
import json
from tweepy import Stream
from tweepy import OAuthHandler
from tweepy.streaming import StreamListener
from pymongo import MongoClient


""" This script collects tweets and store them into MongoDB """

#consumer key, consumer secret, access token, access secret.
ckey="ducZHJELheVy9ZL8wEDzqczVT"
csecret="fbabQKlmc0z6fcv996BvwDUsMyK96hiV1pgxKSKshXSBdaVAbp"
atoken="2274893005-MwITPufGRi2p9zeMfJU8gp1tOq16ePGvKUripaB"
asecret="q2hsjzjYkwQb6tPLskkH0FAi2OxC7uuekKXxcKPddT1Av"

auth = OAuthHandler(ckey, csecret)
auth.set_access_token(atoken, asecret)


class listener(StreamListener):

    def on_data(self, data):

        client = MongoClient('localhost', 27017)
        db = client['twitter_db']
        collection = db['twitter_collection']

        try:
            if data[0].isdigit():
                pass
            else:
                tweet = json.loads(data)
                collection.insert(tweet)

        except BaseException, e:
                print 'failed on data,', str(e)
                time.sleep(5)
                pass
        return True

    def on_error(self, status):
        print status
        return True



twitterStream = Stream(auth, listener())
#twitterStream.filter(track=[])
twitterStream.sample()

