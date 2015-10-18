from pymongo import MongoClient

# Connection to Mongo DB
try:
    conn = MongoClient('localhost', 27017)
    print "Connected successfully!!!"
except pymongo.errors.ConnectionFailure, e:
   print "Could not connect to MongoDB: %s" % e 

# db = conn.mydb
# db

db = conn['twitter_db']
cursor = db.twitter_collection.find()

print "%d entries in collection" % cursor.count()

for document in cursor:
	if type(document.get("geo")) is dict:
	 	print(document.get("geo"))


# collection = db.my_collection
# print collection
# print db.collection_names()

# # doc = {"name":"Alberto","surname":"Negron","twitter":"@Altons"}
# # collection.insert(doc)
# # print doc

# print conn.database_names()

