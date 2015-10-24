    
    
    sys = require('sys');
    events = require('events');
    
    require('./credentials');

    var twitter = require('ntwitter');

    var MongoClient = require('mongodb').MongoClient;
    var assert = require('assert');
    var mongoURL = 'mongodb://localhost:27017/tstream';
    
    
    function TwitterEventStreamer() {
        events.EventEmitter.call(this);
    }

    sys.inherits(TwitterEventStreamer, events.EventEmitter);

    TwitterEventStreamer.prototype.stream = function(keyword) {
        var self = this;
        
        var twitterCredentials = new TwitterCredentials(); 
        var twit = new twitter(twitterCredentials.getSecrets());

        counter = 0;
             
        twit.stream('statuses/filter',{track: keyword}, function(stream) {
                  stream.on('data', function(tweet) {
                        self.emit('tweet', tweet);
                  });
                  
                   stream.on('data', function (data) {
                        console.log(data);
                        console.log("\n\n\n\n");
                        MongoClient.connect(mongoURL, function(err,db){
                            if(!err) {
                            var collection = db.collection('tweets_tail', function(err, collection) {});
                            console.log("We are connected");
                            collection.insert(data)
                          }

                        });
                        
                        
                    });

                  stream.on('error', function(error,statusCode) {
                        console.log('Error was this %j', error);
                        console.log('Error was this ' + statusCode);
                        self.emit('error','Error occured on Twitter maybe?');
                  });
            }
        );
    }
    module.exports = TwitterEventStreamer;
    
