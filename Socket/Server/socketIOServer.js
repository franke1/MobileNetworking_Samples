const http = require('http');
const app = require('express')();

const server = http.createServer(app);
server.listen(3000, (err) => {
   console.log('Server is running @ 3000');
});

app.get('/', (req, res) => {
   res.sendFile(__dirname + '/socketIOIndex.html');
});

const io = require('socket.io')(server);


io.on('connection', socket => {
   // 닉네임 등록. socket.id로 구분
   var name = 'Guest' + Math.floor(Math.random()*100);
   socket.name = name;
   console.log(name + ' connected');

   // 개별 클라이언트에 환영 메세지
   socket.emit('chat', {name:'Admin', message:'Welcome to Socket.IO Chat Service'});

   // 채팅 메세지는 모든 클라이언트에게
   socket.on('chatInput', data => {
      const msg = data['message'];
      const name = socket.name;
      const chat = {name:name, message:msg};

      // 채팅방으로 메세지 이벤트 발생
      io.emit('chat', chat);
      console.log(name + ' >> ' + msg);
   });

   socket.on('rename', data => {
      const oldNmae = socket.name;
      socket.name = data.name;
      io.emit('chat', {name:'Admin', message:oldNmae + ' => ' + socket.name});
   });
});