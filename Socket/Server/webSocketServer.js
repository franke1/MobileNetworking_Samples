var express = require('express');
var app = express();
var server = require('http').createServer(app);
server.listen(3000, function() {
	console.log('Server is listening @ 3000');
});

app.use('/', function(req, res) {
	res.sendFile(__dirname + '/index.html');
});
var io = require('socket.io')(server, {transports:['websocket']});

var users = {};

io.on('connection', function(socket) {
	//console.log('Client connected : ', socket);

	var nickName = 'Guest' + Math.floor(Math.random()*100);
	users[socket.id] = nickName;

	socket.emit('chatMessage', {message:'welcome to ChatServer, ' + nickName, nick:'System'});

	socket.on('chatInput', function(data) {
		console.log('Client Input : ',data.message);

		var broadcastMsg = {message:data.message, nick:users[socket.id]};
		io.emit('chatMessage', broadcastMsg);
	});
});

