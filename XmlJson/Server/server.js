const express = require('express');
const bodyParser = require('body-parser');

const app = express();

app.use(bodyParser.json());

app.post('/upload', (req, res) => {
	const data = req.body.data;

	if ( ! data ) {
		res.status(400).send({msg:'No Data'});
		return;
	}

	res.send({msg:'success', data : data});
});

app.get('/', (req, res) => {
	var data = '<html><body>';
	data += '<h1>Json Request Example Server</h1>';
	data += '<ul>';
	data += '<li>method : POST</li>';
	data += '<li>url : /upload</li>';
	data += '<li>body : "data" : { ANYTHING }</li>';
	data += '</ul>';
	data += '</body></html>';
	res.send(data);
});

app.listen(3000, (err) => {
	console.log('Server is running @ 3000');
});

