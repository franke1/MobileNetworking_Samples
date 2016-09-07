var dgram = require('dgram');

var port = 3001;
var memberships = [];

var socket = dgram.createSocket('udp4', function(msg, rinfo) {
   console.log('Received : ', msg.toString('utf-8'), 'from : ', rinfo.address);

   // echo
	socket.send(msg, 0, msg.length, rinfo.port, rinfo.address);
});
socket.bind(port);

// 서버 IP 주소 얻기
var serverIp = require('./serverIp');
socket.on('listening', function() {
	console.log('listening @ ' + serverIp.getIPAddress() + ':' + port);
});

socket.on('close', function() {
	console.log('Socket closed');
});

socket.on('error', function(err) {
   console.error('Error', err);
});
