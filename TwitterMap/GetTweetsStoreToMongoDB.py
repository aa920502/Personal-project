import time
import json
from tweepy import Stream
from tweepy import OAuthHandler
from tweepy.streaming import StreamListener
from pymongo import MongoClient


# This script collects tweets and writes to output file in json format

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
                # Empty dictionary for storing tweet related data
                # data ={}
                # data['created_at'] = tweet.created_at
                # data['from_user'] = tweet.from_user
                # data['from_user_id'] = tweet.from_user_id
                # data['from_user_id_str'] = tweet.from_user_id_str
                # data['from_user_name'] = tweet.from_user_name
                # data['geo'] = tweet.geo
                # data['id'] = tweet.id
                # data['iso_language_code'] = tweet.iso_language_code
                # data['source'] = tweet.source
                # data['text'] = tweet.text
                # data['to_user'] = tweet.to_user
                # data['to_user_id'] = tweet.to_user_id
                # data['to_user_id_str'] = tweet.to_user_id_str
                # data['to_user_name'] = tweet.to_user_name
                # Insert 
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

