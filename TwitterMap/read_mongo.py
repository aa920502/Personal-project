from pymongo import MongoClient

""" This script reads from MongoDB and query on tweets """

# Connection to Mongo DB
try:
    conn = MongoClient('localhost', 27017)
    print "Connected successfully!!!"
except pymongo.errors.ConnectionFailure, e:
	print "Could not connect to MongoDB: %s" % e 

# get specific database
db = conn['twitter_db']
# get specific collection in database
twitter_collection = db['twitter_collection']

print "%d entries in twitter_collection" % twitter_collection.count()

# Retrive items in collection which "geo" is an object  (not null)
geo_valid_cursor = db.twitter_collection.find( {"place" : { "$type" : 3 } } )  

print "%d records with valid geo information" % geo_valid_cursor.count()

for document in geo_valid_cursor:
 	print(document.get("place").get("full_name"))


print conn.database_names()
print db.collection_names()
print twitter_collection



# doc = {"name":"Alberto","surname":"Negron","twitter":"@Altons"}
# collection.insert(doc)
# print doc


