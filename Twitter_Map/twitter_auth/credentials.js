
TwitterCredentials = function(){
        
        var secret = { 
            consumer_key : 'ducZHJELheVy9ZL8wEDzqczVT',
            consumer_secret: 'fbabQKlmc0z6fcv996BvwDUsMyK96hiV1pgxKSKshXSBdaVAbp',
            access_token_key: '2274893005-MwITPufGRi2p9zeMfJU8gp1tOq16ePGvKUripaB',
            access_token_secret: 'q2hsjzjYkwQb6tPLskkH0FAi2OxC7uuekKXxcKPddT1Av'
        };

         function getConsumerKry() {
             return secret.consumer_key; 
         }

         function getConsumerSecret() {
             return secret.consumer_secret;
         }

         function getAccessTokenKey() {
             return secret.access_token_key;
         }

         function getAccessTokenSecret() {
            return access_token_secret;
         }

         function _getSecrets() {
             return secret;
         }

         return {
            getSecrets: _getSecrets
         }
};

module.exports = TwitterCredentials;
