var express = require('express');
var router = express.Router();

router.get('/', showWebPage);
router.get('/talks', showTalks);
router.post('/talks', isAuthenticated, postTalk);

var talks = [
   "짧은 글을 작성합니다.",
   "로그인과 글쓰기 예제입니다."];

function showWebPage(req, res) {
   res.sendFile(__dirname + '/public/index.html');
}

function showTalks(req, res) {
   var result = {
      count : talks.length,
      talks : talks
   };
   res.json(result);
}

function postTalk(req, res) {
   var newTalk = req.body.talk;
   console.log('posting new toak : ', newTalk);
   talks.push(newTalk);
   res.sendStatus(200);
}

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

module.exports = router;