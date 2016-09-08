var express = require('express');
var app = express();

// 로그 기록
var morgan = require('morgan');
app.use(morgan('dev'));

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

// Local Strategy
var LocalStrategy = require('passport-local').Strategy;
var strategy = new LocalStrategy( {passReqToCallback : true},
  function(req, username, password, done) {
    console.log('LocalStrategy :', username, password);
    
    if ( username == "user" && password == "1234" ) {
       var user = {
          id : username
       }
       return done(null, user);
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
  done(null, user.id);
});

// 세션에서 사용자 정보 얻어오기
passport.deserializeUser(function(id, done) {
  console.log('deserializeUser', id);
   var user = {
      id : id
   }
   done(null, user);      
});

// bodyParser : POST 메세지 파싱
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: false }))

// 인증 여부 체크
function isAuthenticated(req, res, next) {
  console.log('isAuthenticated', req.isAuthenticated());
  if (req.isAuthenticated()) {
    return next();
  }
  else {
    res.status(401).send({'msg':'Unauthorized!'});    
  }  
}

var talks = [
   "짧은 글을 작성합니다.",
   "로그인과 글쓰기 예제입니다."];

app.get('/login', showLoginForm);
app.post('/login', passport.authenticate('local'), function(req, res) {
   console.log('Success!! : ' + req.isAuthenticated());   
   res.sendStatus(200);
});

app.get('/', showWebPage);
app.get('/talks', showTalks);
app.post('/talks', isAuthenticated, postTalk);

function showTalks(req, res) {
   var result = {
      count : talks.length,
      talks : talks
   };
   res.json(result);
}

function showWebPage(req, res) {
   var html = '<html>';
   html += '<body>';
   html += '<h3>글 목록</h3>';
   html += '<ul>';
   talks.forEach(function(item) {
      html += '<li>' + item + '</li>'
   });
   html += '</ul>';
   html += '<h3>새로운 글 쓰기</h3>'
   html += '<form action="/talks">';
   html += '<input type="text" name="talk"><br/>'
   html += '<input type="submit">'
   html += '</form>'
   html += '</body>';
   html += '</html>';
   res.send(html)
}

function postTalk(req, res) {
   console.log('post talk ', req.body);
   var newTalk = req.body['talk'];
   if ( ! newTalk ) {
      res.status(401).send({'msg':'Unauthorized!'});
      return;
   }
   
   talks.push(newTalk);
   res.sendStatus(200);
}

function showTalk(req, res) {
   res.send('Hello!');
}

function showLoginForm(req, res) {
   var html = '<html><body>';
   html += '<form method="POST" action="/login">';
   html += '<input type="text" name="userid"><br />';
   html += '<input type="password" name="password"><br />';
   html += '<input type="submit" value="LogIn">';
   html += '</form>';
   html += '</body></html>';
   res.send(html);
}



// function login(req, res) {
//    var userid = req.body.userid;
//    var password = req.body.password;
//    console.log('login == id : ' + userid + ' pw : ' + password);
   
//    if ( userid == 'user' && password == '1234' )
//       res.send('Welcome');
//    else
//       res.sendStatus(401);
// }


// HTTP Server
app.listen(3000, function(err) {
   if ( err ) {
      console.error('Error', err);
   }
   else {
      console.log('Http server is listening @ 3000');
   }
});
