function getCleanData() {
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

function initialize() {
	  var mapOptions = {
	    zoom: 12,
	    center: new google.maps.LatLng(12.975788,77.617868), //TODO:how would you arrive at this?
		mapTypeId: google.maps.MapTypeId.MAP
	 };
	  
	  map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);	  
	  for (var i = 0; i < set.results.length; i++) {
		  	var location = new google.maps.LatLng(parseFloat(set.results[i].lat),parseFloat(set.results[i].long));
		  	var address = set.results[i].address;
		  	var url = set.results[i].img;
		    var contentString = "<div width=\"150px\" height=\"250px\">" +
		    		"<a href=\""+url+"\" target=\"_blank\"><img width=\"150px\" style=\"float:left\" height=\"150px\" src=\""+url+"\" alt=\"Loading...\"></a>" +
		    		"<p style=\"overflow:auto\"><b>Tagged as " + getRating(parseInt(set.results[i].score)) + "</b>"+
		    		"<br><br>" + address + "</p></div>";
		    var infowindow = new google.maps.InfoWindow({content: contentString, position: location});
		    var color = getColor(parseInt(set.results[i].score));
		    var marker = new google.maps.Marker({position: location, map: map, icon: {path: google.maps.SymbolPath.CIRCLE, strokeColor:color, scale: 8}});
		    addClickHandler(infowindow, marker, i);
	  }		  
	  function addClickHandler(infowindow, marker, i){
		  google.maps.event.addListener(marker, 'click', function() {
			  infowindow.open(map);
		  });
	  }
	  document.getElementById('legend').style.display='block';
	  map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(document.getElementById('legend'));
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