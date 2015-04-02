function getAvgData() {
	var type = document.getElementById("gpType").value;
	var name = document.getElementById("gpName").value;
	var paramString = "?type="+type+"&"+"name="+name;
	xmlhttp=new XMLHttpRequest();	
	xmlhttp.onreadystatechange = function(){handleResponse(xmlhttp);};
	xmlhttp.open('GET','http://nirmalbharat.mybluemix.net/rest/cleaninfo'+paramString,true);
	xmlhttp.send();
	document.getElementById("input").innerHTML = "<h2>Map loading. Please wait ...</h2>";
}
var set;
function handleResponse(xmlhttp) {	
	if( xmlhttp.readyState == 4 && xmlhttp.status == 200 ) {
		document.getElementById("input").style.display="none";
		set = JSON.parse(xmlhttp.responseText);
		initialize();
	}
}


function initializeSublocalities() {
	for (var i = 0; i < set.results.length; i++) {
		var address = set.results[i].address.split(',');
		var sublocality = address[address.length-3];
		addSublocality(sublocality,parseFloat(set.results[i].lat), parseFloat(set.results[i].long), parseInt(set.results[i].score));		
	}		  

}
var sublocalities = [];
function addSublocality(sname, slat, slong, sscore) {
	var location = new google.maps.LatLng(slat,slong);
    for(var i=0; i< sublocalities.length; i++) {    	
        if(sname == sublocalities[i].name) {        	
        	sublocalities[i].ascore = (sublocalities[i].ascore + sscore)/2;
        	sublocalities[i].locations.push(location);
        	return;
        }
    }
    var locArray = [location];
    var newSublocality = {name:sname, locations:locArray, ascore:sscore};
    sublocalities.push(newSublocality);
}

function initialize() {
	
	  initializeSublocalities();	 
	  var mapOptions = {
	    zoom: 14,
	    center: new google.maps.LatLng(12.975788,77.617868),
		mapTypeId: google.maps.MapTypeId.TERRAIN
	  };
	  
	  map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);	  
	  for (var  i= 0; i < sublocalities.length; i++) {
		  	var sublocality = sublocalities[i];		  			   
		    var color = getColor(Math.round(sublocality["ascore"]));
		    if(sublocality.locations.length >= 3 ) {
		    	var polyOpts = {paths: sublocality.locations, strokeColor: color,strokeOpacity: 0.8, strokeWeight: 2, 
		    			fillColor: color, fillOpacity: 0.35, map: map, clickable:true, sub:sublocality};		    
		    	var polygon = new google.maps.Polygon(polyOpts);
		    	sublocality.poly = polygon;
		    	google.maps.event.addListener(polygon, 'click', showData);
		    }
	  } 
	  document.getElementById('legend').style.display='block';
	  map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(document.getElementById('legend'));
}

function showData(event) {			
	if (google.maps.geometry.poly.containsLocation(event.latLng,this)) {
			 var contentString = "<div><p>Name : " + this.sub.name + "</p>" + 
			 "<p>Average Score: " + this.sub.ascore + "</p></div>";
			 var infoWindow = new google.maps.InfoWindow();
			 infoWindow.setContent(contentString);
			 infoWindow.setPosition(event.latLng);
			 infoWindow.open(map);
			 return;
		}		
}

function getColor(i) {
	switch(i) {
		case 1: return "red";
		case 2: return "orange";
		case 3: return "yellow";
		case 4: return "green";
		case 5: return "blue";
	}
}

function getRating(i) {
	switch(i) {
		case 1 : return "filthy";
		case 2 : return "dirty";
		case 3 : return "unclean";
		case 4 : return "clean";
		case 5 : return "beautiful";
	}
}