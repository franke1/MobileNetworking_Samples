var express = require('express');
var logger = require('morgan');
var app = express();
app.use(logger('dev'));

app.listen(3000, function(err) {
   console.log('Server is Running 3000');
});

var num = 0;
var resFuns = [successResponse, badrequestResponse, unauthorizedReseponse, infiniteResponse ];

function successResponse(req, res) {
   res.sendStatus(200);
}

function infiniteResponse(req, res) {
   res.write('response does not finish.');
}


function delayedResponse(req, res) {
   setTimeout(function() {
      res.sendStatus(200);
   }, 10 * 1000);
}

function badrequestResponse(req, res) {
   res.sendStatus(400);
}

function unauthorizedReseponse(req, res) {
   res.sendStatus(401);
}
//http://192.168.25.3:3000/infinite
app.get('/infinite', infiniteResponse);
app.get('/badreq', badrequestResponse);
app.get('/unauth', unauthorizedReseponse);
app.get('/delayed', delayedResponse);

// app.use(function(req, res) {
//    var index = num++ % resFuns.length;
//    var handler = resFuns[index];
//    console.log('Response Handler : ' + handler.name);
//    handler(req, res);   
// });