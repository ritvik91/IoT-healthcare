/*
 * Server.js
 * 
 * Runs the node server interacting with Android App and the board
 */
var express   = require('express'),
	app       = express();
var pug       = require('pug');
var sockets   = require('socket.io');
var path      = require('path');
var bodyParser = require('body-parser');
var appMobile = express();
var globalSocket;
appMobile.use(bodyParser.urlencoded({ extended: true }));

var conf      = require(path.join(__dirname, 'config'));
var internals = require(path.join(__dirname, 'internals'));
var reff_mqtt;
var db_handler;
var db;

// -- Setup the application
setupExpress();
setupSocket();

appMobile.post('/responder/doctor',function(req,res){
	res.contentType('application/json');

	var id = req.query.id;
	var func = req.query.func;
	

	if(func=='getVitals'){
		cursor = db.find({id: id});
		cursor.forEach( function(dtb, err) {	
		  			res.send(dtb.vitals);
		  			globalSocket.emit('debug', {
						type: 'Doctor', msg: 'Patient "' + id + '" vitals requested'
					});
		  		});
	}

	if(func=='putTreatment'){
		var treatment_notes = req.query.treatment_notes;
		var treatmentObj = { Time: new Date(), Treatment_Notes: treatment_notes};
  		db.update({id:id}, {$push:{'vitals.temp':tempObj}},{upsert:true});

  		globalSocket.emit('debug', {
						type: 'Doctor', msg: 'Patient ID: "' + id + '" Treatment notes added'
					});
  		return res.send('updated');
	}
		
	if(func=='getOxygen'){

		cursor = db.find({id: id});
		cursor.forEach( function(dtb, err) {	
		  			res.send(dtb.vitals.O2);
		  			globalSocket.emit('debug', {
						type: 'Doctor', msg: 'Patient "' + id + '" Oxygen saturation History requested'
					});
		  		});
	}

	if(func=='getCurrOxygen'){

		cursor = db.find({id: id});
		curs = db.find( {id: id}, { "vitals.O2": { $slice: -1 } } );
		var tmp;
		curs.forEach( function(dtb, err) {	
		  			tmp = dtb.vitals.O2;
		  			res.send(tmp[0]);
		  			globalSocket.emit('debug', {
						type: 'Doctor', msg: 'Patient "' + id + '" Current Oxygen saturation requested'
					});
		  		});

	}

	if(func=='getTemp'){

		cursor = db.find({id: id});
		var i =0;
		var seperator = '';
		var tempList = '';
		cursor.forEach( function(dtb, err) {
					console.log("tempobj "+dtb.vitals.temp);
					console.log("temp "+dtb.vitals.temp[i].Temp);	
					console.log("i "+i);
					var tmp = dtb.vitals.temp;
					var length = tmp.length;
					tmp.forEach(function(obj,err){
						tempList = tempList + seperator + obj.Temp;
		  				seperator = ',';
		  				console.log("temp list "+tempList + "len " + length);

		  				length--;
		  				if(length===0){
		  					res.send( JSON.parse('{"Temp": "' + tempList + '"}'));
		  				}
				});
		  			
		  			i++;
		  			globalSocket.emit('debug', {
						type: 'Doctor', msg: 'Patient "' + id + '" Temperature History requested '
					});
		  		});
	}
  
  	if(func=='getCurrTemp'){

		cursor = db.find({id: id});
		curs = db.find( {id: id}, { "vitals.temp": { $slice: -1 } } );
		var tmp;
		curs.forEach( function(dtb, err) {	
		  			tmp = dtb.vitals.temp;
		  			res.send(tmp[0]);
		  			globalSocket.emit('debug', {
						type: 'Doctor', msg: 'Patient "' + id + '" Current Temperature requested '
					});
		  		});

	}

  	if(func=='getEKG'){

  		cursor = db.find({id: id});
		cursor.forEach( function(dtb, err) {	
		  			res.send(JSON.parse('{ "EKG" :"'+dtb.vitals.EKG+'"}'));
		  			globalSocket.emit('debug', {
						type: 'Doctor', msg: 'Patient "' + id + '" EKG requested'
					});
		  		});
  	}

  	
  	if(func=='getPatientList'){

  		var docID = req.query.doc;
  		var patientList = '{ "patientList" : "';
  		var seperator = '';
  		cursor = db.find({doc: docID});
  		var length = 0;
  		cursor.count(function(error, nbDocs) {
    			length = nbDocs;
			});
  		cursor.forEach( function(dtb, err) {

  					patientList = patientList + seperator + dtb.id;
		  			seperator = ',';
		  			length--;
		  			
		  			if(length===0){
		  				patientList = patientList + '"}';
		  				res.send(JSON.parse(patientList));
		  			}
		  		
		   		});

  		console.log("list - " + patientList);
  		
  	//	res.send(patientList);
  		globalSocket.emit('debug', {
						type: 'Doctor', msg: 'Doc "' + docID + '" Patient list requested requested'
					});
  	}
});

appMobile.post('/responder/nurse',function(req,res){
	

	var func = req.query.func;
	var id = req.query.id;

	console.log("func "+func);

	if(func=='putSchedule'){
		var time = req.query.time;
		var med = req.query.med;
		console.log("time med "+ time+med);
		if(time=='morning'){
			db.update({id:id}, {$set:{'schedule.morning':med}},{upsert:true});	
			globalSocket.emit('debug', {
						type: 'Nurse', msg: 'Patient "' + id + '" treatment schedule updated for' + time
					});
		}else if(time=='evening'){
			db.update({id:id}, {$set:{'schedule.evening':med}},{upsert:true});
			globalSocket.emit('debug', {
						type: 'Nurse', msg: 'Patient "' + id + '"  treatment schedule updated for '+ time
					});
		}
		
		return res.send('updated');
	}

	if(func=='getVitals'){
	cursor = db.find({id: id});
	cursor.forEach( function(dtb, err) {	
		  			res.send(dtb.vitals);
		  			globalSocket.emit('debug', {
						type: 'Nurse', msg: 'Patient "' + id + '" vitals requested'
					});
		  		});
		}
  
	console.log("inside get nurse call");	
});

appMobile.post('/responder/patient',function(req, res){

  var id = req.query.id;
  console.log("simple id " + id);
  var query = req.query.query;
  console.log("query"+query);

  res.contentType('application/json');
  
  console.log("temp : "+ req.query.temp);

  if(query=='gettreatmentHistory'){

  	 cursor = db.find({id: id});
  	 cursor.forEach( function(dtb, err) {
  	 	
  	 		var treatment = dtb.Treatment
		  			res.send(treatment);
		  			globalSocket.emit('debug', {
						type: 'patient', msg: 'Patient "' + id + '" has been served treatment history'
					});
		  		});
		
  }
  if(query=='gettreatmentSchedule'){

  	 cursor = db.find({id: id});
  	 cursor.forEach( function(dtb, err) {
  	 		var schedule = dtb.schedule;
		  			res.send(schedule);
		  			globalSocket.emit('debug', {
						type: 'patient', msg: 'Patient "' + id + '" has been served treatment schedule'
					});
		  		});
		
  }

  
  if(query=='pushTemp'){

  	console.log("in temp");
  	var temp = req.query.temp;
  	var tempObj = {Temp: temp, Time: new Date()};

  	db.update({id:id}, {$push:{'vitals.temp':tempObj}},{upsert:true});
  	res.send('updated');
  	console.log("pushed");
  }

  if(query=='pushOxygen'){
  
  	var O2 = req.query.O2;
  	console.log("in O2 "+O2);
  	var O2Obj = {Oxygen: O2, Time: new Date()};

  	db.update({id:id}, {$push:{'vitals.O2':O2Obj}},{upsert:true});
  	res.send('updated');
  	console.log("pushed");
  }
  
  if(query=='pushEKG'){
  
  	var ekg = req.query.ekg;
  	console.log("in ekg :"+ ekg);
  	var EKGObj = {EKG: ekg, Time: new Date()};

  	db.update({id:id}, {$set:{'vitals.EKG':ekg}},{upsert:true});

  	res.send('updated');
  	console.log("pushed");
  }
  
});
appMobile.listen(8090);
console.log("Server running on 8090 port");

// -- Socket Handler
// ----------------------------------------------------------------------------
function socket_handler(socket, mqtt) {
	// Called when a client connects
	mqtt.on('clientConnected', client => {
		console.log('New client connected: ' + client.id);
		socket.emit('debug', {
			type: 'CLIENT', msg: 'New client connected: ' + client.id
		});
		socket.on('published', function(){
			publishToMbed();
		});
	});
	
	// Called when a client disconnects
	mqtt.on('clientDisconnected', client => {
		socket.emit('debug', {
			type: 'CLIENT', msg: 'Client "' + client.id + '" has disconnected'
		});
	});

	// Called when a client publishes data i.e mbed publishes
	mqtt.on('published', (data, client) => {
		if (!client) return;
		
		var tp = data['topic'];
		var cursor;
		var publish_msg;
		

		cursor = db_handler.find({id: tp});

		//process each row that corresponds to a given ID
		
		//update the counter in the row and if exceeds threshhold report
		cursor.forEach( function(dtb, err) {
				
				db_handler.update({id:tp}, {$set:{'vitals.bpm':data['payload']}});
				
		});
		console.log("Debug point: successful run till here");
		
	});

	// Called when a client subscribes i.e when mbed connects for the first time
	mqtt.on('subscribed', (topic, client) => {
		if (!client) return;

		socket.emit('debug', {
			type: 'SUBSCRIBE',
			msg: 'Client "' + client.id + '" subscribed to "' + topic + '"'
		});
	});

	// Called when a client unsubscribes
	mqtt.on('unsubscribed', (topic, client) => {
		if (!client) return;

		socket.emit('debug', {
			type: 'SUBSCRIBE',
			msg: 'Client "' + client.id + '" unsubscribed from "' + topic + '"'
		});
	});
}


function publishToMbed(topicName){
	var message = {
		topic: topicName,
		payload : 'alexaHi',
		qos: 0,
		retain: false
	};
	
	reff_mqtt.publish(message, function(){
		console.log("done");
	});
}

// Helper functions
function setupExpress() {
	app.set('view engine', 'pug'); // Set express to use pug for rendering HTML

	// Setup the 'public' folder to be statically accessable
	var publicDir = path.join(__dirname, 'public');
	app.use(express.static(publicDir));

	// Setup the paths
	// ------------------------------------------------------------------------
	// Home page
	app.get('/', (req, res) => {
		res.render('index', {title: 'MQTT Tracker'});
	});

	// Basic 404 Page
	app.use((req, res, next) => {
		var err = {
			stack: {},
			status: 404,
			message: "Error 404: Page Not Found '" + req.path + "'"
		};

		
		next(err);
	});

	// Error handler
	app.use((err, req, res, next) => {
		console.log("Error found: ", err);
		res.status(err.status || 500);

		res.render('error', {title: 'Error', error: err.message});
	});
	// ------------------------------------------------------------------------

	// Handle killing the server
	process.on('SIGINT', () => {
		internals.stop();
		process.kill(process.pid);
	});
}

function setupSocket() {
	var server = require('http').createServer(app);
	var io = sockets(server);
	
	// Setup the internals
	console.log("before");
	internals.start(mqtt => {

		io.on('connection', socket => {
			socket_handler(socket, mqtt);
			console.log("here");
			globalSocket = socket;
			reff_mqtt = mqtt;
			
			db_handler = mqtt.ascoltatore.db.collection('ascoltatori');
			db = mqtt.ascoltatore.db.collection('healthApp');
		});
	});
	 	
	server.listen(conf.PORT, conf.HOST, () => { 
		console.log("Listening on: " + conf.HOST + ":" + "8090");
		
	});
}
