var net = require('net');
var server = net.createServer(function (socket) {
   console.log('create server ' + socket.remoteAddress);
   console.log('connect ' + socket.remoteAddress);

   socket.on('connect', function () {

   });
   socket.on('data', function (data) {
      console.log(socket.remoteAddress + ' : ' + data);
      socket.write(data);
   });
   socket.on('end', function () {
      console.log('connection end')
   })

});

server.listen(3000);


// 서버 IP 주소 얻기
var serverIp = require('./serverIp');

console.log('Echo server is running on ' + serverIp.getIPAddress() + ':3000');



