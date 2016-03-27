var express = require('express');
var app = express();

// 로그 기록
var morgan = require('morgan');
app.use(morgan('dev'));

// bodyParser : POST 메세지 파싱
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: false }))
app.use(bodyParser.json());

// 세션 설정
var session = require('express-session');
app.use(session({
  secret: 'Secret Key',
  resave: false,
  saveUninitialized: true
}));

// 패스포트 설정
var passport = require('passport');

app.use(passport.initialize());
app.use(passport.session());

var userInfo = {
   id : 'user',
   name : '유저'
};
       
// Local Strategy
var LocalStrategy = require('passport-local').Strategy;
var strategy = new LocalStrategy(
  function(username, password, done) {
    if ( username == "user" && password == "1234" ) {
       return done(null, userInfo);
    }
    else {
       return done(null, false, {message:'Authorization fail'});
    }            
  }
);
passport.use(strategy);

// 세션에 기록하기
passport.serializeUser(function(user, done) {
  console.log('serializeUser', user);  
  done(null, user);
});

// 세션에서 사용자 정보 얻어오기
passport.deserializeUser(function(id, done) {
  console.log('deserializeUser', id);
   done(null, userInfo);      
});


app.post('/login', function(req, res) {
   passport.authenticate('local', function(err, user, msg) {
      if ( err ) {
         res.status(400).send({msg:err});
         return;
      }      
      if ( user ) {
			req.login(user, function(err) {
				res.send({msg:'success', userInfo:user});
			});                  
      }
      else {
         res.status(401).send({msg:msg});
      }
   })(req);
});
app.delete('/logout', function(req, res) {
   req.logout();
   res.send(200);   
});
app.use(require('./talkRouter'));

app.listen(3000);