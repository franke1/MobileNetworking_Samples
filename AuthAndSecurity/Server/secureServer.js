var fs = require('fs');
var https = require('https');

// HTTPS Server
var options = {
    key: fs.readFileSync('key.pem'),
    cert: fs.readFileSync('cert.pem')
};


var secureServer = https.createServer(options, app);
secureServer.listen(3001, function(err) {
   if ( err ) {
      console.error('Error', err);
   }
   else {
      console.log('Https server is listening @ 3001');
   }   
});
