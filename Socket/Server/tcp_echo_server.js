var net = require('net');
var server = net.createServer(function (socket) {
   console.log('connect ' + socket.remoteAddress);
   socket.write('Welcome To TCP EchoService\n');
   
   // 클라이언트의 데이터 전송 이벤트
   socket.on('data', function (data) {
      console.log(socket.remoteAddress + ' : ' + data);
      socket.write(data + "\r\n");
      // 클라이언트가 한 줄 단위로 읽는다.
      // socket.write('\n'); 
   });
   
   // 접속 종료 이벤트
   socket.on('end', function () {
      console.log('connection end')
   })
});
server.listen(3000);


// 서버 IP 주소 얻기
var serverIp = require('./serverIp');

console.log('Echo server is running on ' + serverIp.getIPAddress() + ':3000');



