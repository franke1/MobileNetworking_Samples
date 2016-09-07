// https://github.com/theturtle32/WebSocket-Node

var express = require('express');
var app = express();
var server = require('http').createServer(app);
server.listen(3000, function() {
	console.log('Server is listening @ 3000');
});

app.use('/', function(req, res) {
	res.sendFile(__dirname + '/websocketIndex.html');
});

var WebSocketServer = require('websocket').server;

wsServer = new WebSocketServer({
	httpServer: server,
	autoAcceptConnections: true
});

var connections = [];

wsServer.on('connect', function(conn) {
	console.log('Connection event');

	var nickName = 'Guest' + Math.floor(Math.random()*100);
	console.log('Client connected : ', nickName);
	connections.push(conn)

	conn.send('welcome to ChatServer, ' + nickName);

	conn.on('message', function(data) {
		var message = data.utf8Data;
		var str = nickName + ' >> ' + message;
		console.log(str);

		connections.forEach(function(conn) {
			conn.sendUTF(str);
		});
	});

	conn.on('close', function(reasonCode, description) {
		console.log((new Date()) + ' Peer ' + conn.remoteAddress + ' disconnected.');
	});
});