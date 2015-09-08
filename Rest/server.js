var express = require('express');
var bodyParser = require('body-parser');
var movies = require('./movie_router');
var morgan = require('morgan');

var app = express();
app.use(bodyParser.urlencoded({ extended: false }))

// 정적 파일 처리
app.use(express.static(__dirname + '/images'));

// Logging
app.use(morgan('dev'));

app.get('/movies', showMovieList);
app.post('/moives', addMovie);
app.get('/movies/:movie_id', showMovieDetail);
app.delete('/movies/:movie_id', deleteMovie);
app.put('/movies/:movie_id', editMovie);		
app.post('/reviews/:movie_id', addReview);	

// 여기까지 오면 - 그냥 목록 출력
app.use('/', showMovieList);

app.listen(3000, function() {
	console.log('Moviest Server is listening @ 3000');
});


var movies = {
	"아바타":{
		"director":"제임스 카메론",
		"year":2009,
		"synopsis":"인류의 마지막 희망, 행성 판도라! 이 곳을 정복하기 위한 ‘아바타 프로젝트’가 시작된다!",
		"reviews":[]		
	}
}


function showMovieList(req, res) {
   var list = [];
   for( var movie_id in movies ) {
      var item = movies[movie_id];
      list.push({title:item.title, movie_id:item.movie_id});      
   }
   
   var result = {
      count : list.length,
      movies : list
   };
   return res.json(result);
}

function addMovie(req, res) {
   var title = req.body.title;
   var director = req.body.director;
   var year = parseInt(req.body.year);
	if ( ! year ) {
		res.sendStatus(400);
		return;
	}
	var synopsis = req.body.synopsis;

	movies[title] = {'director':director, 'year':year, 'synopsis':synopsis, 'reviews':[]};
   
   var result = {'movie_id':title};
	
	res.json(result);
}

function showMovieDetail(req, res) {
   
}

function deleteMovie(req, res) {
   
}

function editMovie(req, res) {
   
}

function addReview(req, res) {
   
}