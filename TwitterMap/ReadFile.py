import json
import pandas as pd
import matplotlib.pyplot as plt


# This script reads tweets which has valid 'geo' field from a file

tweets_data_path = '123.txt'

tweets_data = []
with open(tweets_data_path, "r") as tweets_file:
	for line in tweets_file:
		try:
			tweet = json.loads(line) 
			keys = tweet.keys()
			if type(tweet['geo']) is dict:
				# print line
			 	tweets_data.append(tweet)
		except:
			continue

print len(tweets_data)

# fileW = ('locations.txt', "w")
# output = str(tweets_data)
# fileW.write(output)
# fileW.close()



tweets = pd.DataFrame()
#tweets['text'] = map(lambda tweet: tweet['text'], tweets_data)
tweets['lang'] = map(lambda tweet: tweet['lang'], tweets_data)
# tweets['created_at'] = map(lambda tweet: tweet['created_at'], tweets_data)
# tweets['country'] = map(lambda tweet: tweet['place']['country'] if tweet['place'] != None else None, tweets_data)

tweets_by_lang = tweets['lang'].value_counts()
# tweets_by_country = tweets['country'].value_counts();
# tweets_by_time = tweets['created_at'].value_counts();


fig, ax = plt.subplots()
ax.tick_params(axis='x', labelsize=15)
ax.tick_params(axis='y', labelsize=10)
ax.set_xlim([0, 10])
ax.set_ylim([0,200])
ax.set_xlabel('Languages', fontsize=15)
ax.set_ylabel('Number of tweets' , fontsize=15)
ax.set_title('Top 5 languages', fontsize=15, fontweight='bold')
tweets_by_lang[:10].plot(ax=ax, kind='bar', color='red')
#tweets_by_time[:5].plot(ax=ax, kind='bar', color='red')
# tweets_by_country[:5].plot(ax=ax, kind='bar', color='red')
plt.show()


# Return true if a word is found in text
def word_in_text(word, text):
    word = word.lower()
    text = text.lower()
    match = re.search(word, text)
    if match:
        return True
    return False


