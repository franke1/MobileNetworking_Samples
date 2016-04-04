var express = require('express');
var logger = require('morgan');
var timeout = require('connect-timeout');
var app = express();

app.use(timeout('5s'));
app.use(logger('dev'));

app.listen(3000, function(err) {
   console.log('Server is Running 3000');
});

function successResponse(req, res) {
   res.sendStatus(200);
}

function infiniteResponse(req, res) {
   res.write('Response does not finish.');
}


// 10초뒤 응답
function delayedResponse(req, res) {
   setTimeout(function() {
      res.status(200).send("Delayed Response");
   }, 10 * 1000);
}

function badrequestResponse(req, res) {
   res.sendStatus(400);
}

function unauthorizedReseponse(req, res) {
   res.status(401).send("Unauthorized");
}

//http://192.168.25.3:3000/infinite
app.get('/infinite', infiniteResponse);
app.get('/badreq', badrequestResponse);
app.get('/unauth', unauthorizedReseponse);
app.get('/delayed', delayedResponse);