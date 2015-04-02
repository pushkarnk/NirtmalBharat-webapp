var xmlhttp;
var cookie;


function addUser() {
	var username = document.getElementById("username").value;
	var emailid = document.getElementById("emailid").value;	
    if (validate(username,emailid) == false) {
    	return;
    }
    cookie = "nbuser="+username;
	xmlhttp=new XMLHttpRequest();	
	xmlhttp.onreadystatechange = function(){handleResponse(xmlhttp);};
	xmlhttp.open('POST','http://nirmalbharat.mybluemix.net/rest/useradd',true);
	xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	xmlhttp.send("username="+username+"&"+"emailid="+emailid);	
} 

function handleResponse(xmlhttp) {
	var next = "<br><br><button onclick=\"location.href='twitter_login.html'\">Continue</button>";
	if( xmlhttp.readyState == 4 && xmlhttp.status == 200 ) {			
		if(xmlhttp.responseText == "DONE") {
			document.getElementById("user").style.display = 'none';
			document.getElementById("response").innerHTML = "User added! <br>" + next;
			document.cookie = cookie;
		}
		if(xmlhttp.responseText == "ERROR")
			document.getElementById("response").innerHTML="Username exists. Please try again!";		
	}	
} 

function validate(username,emailid) {
	
	if (username=="" || emailid == "") {
		alert("Please ensure that there are no empty fields");
		return false;
	}
	
	if(/[a-zA-Z0-9.]*@[a-zA-Z0-9.]*[.][a-zA-Z0-9.]+/.test(emailid) == false) {
		alert("Please enter a valid email");
		return false;
	}
	
	if(/[a-zA-Z0-9]+/.test(username) == false) {
		alert("User name can only have alphbets and numerals");
		return false;
	}
	
	if(username.length > 10) {
		alert("Username can have a maximum of 10 characters");
		return false;
	}
	
	return true;
	
}

function checkCookie() {
	    var theCookies = document.cookie.split(';');
	    for (var i = 0 ; i < theCookies.length; i++) {	   
	        if(theCookies[i].indexOf("nbuser",0)==0) {
	        	var html = "Hi, " + theCookies[i].split("=")[1] + "!"
	        	         + "<br><br> Welcome back!"
	        	         + "<br><br><button onclick=\"location.href='checkin.html'\">Check in cleanliness info</button>";
	        	if(theCookies[i+1] == undefined || theCookies[i+1].indexOf("twitter,0")!=0) {
	        		html += "<br><br><br><br>You haven't connected to Twitter yet!";
	        		html += "<br><button onclick=\"location.href='twitter_login.html'\">Sign-in to Twitter</button>";
	        	}	        		
	        	document.getElementById("user").innerHTML = html;
	        }
	    }
}

// second page
function initSecondPage() {
}	

function getUsername() {
	return document.cookie.split(';')[0].split("=")[1];
}
function getTwitterAuthURL() {
	xmlhttp=new XMLHttpRequest();	
	xmlhttp.onreadystatechange = function(){handleResponse1(xmlhttp);};
	xmlhttp.open('GET','http://nirmalbharat.mybluemix.net/rest/twauth',true);
	xmlhttp.send();
	document.getElementById("authButton").innerHTML = "Contacting Twitter ...";
}

function handleResponse1(xmlhttp) {
	if( xmlhttp.readyState == 4 && xmlhttp.status == 200 ) {
		
		document.getElementById("authButton").style.display='none';
		
		var instruction = "<b>Please click the link below.</b>" +
				      "<br><br>You will be redirected to a Twitter page for authentication." +
			          "<br><br>After authentication, Twitter will give you 7-digit a pin. " +
			          "<br><br>Please enter it in the box below.<br><br>"; 
		document.getElementById("authURL").innerHTML = instruction + "<a href=\""+xmlhttp.responseText + "\" target=\"_blank\">Click here</a>";
		
		var pinBox = "<br><br><label>Enter the PIN here&emsp;</label><input type=\"text\" id=\"pin\">";
		    pinBox += "<br><br><button onclick=\"submitPin();\">Continue</button>";
		document.getElementById("pinBox").innerHTML = pinBox;
	}	
}

function submitPin() {
	var pin = document.getElementById("pin").value;
	xmlhttp=new XMLHttpRequest();	
	xmlhttp.onreadystatechange = function(){handleResponse2(xmlhttp);};
	xmlhttp.open('POST','http://nirmalbharat.mybluemix.net/rest/twauth',true);
	xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	xmlhttp.send("username="+getUsername()+"&"+"pin="+pin);
	document.getElementById("pinBox").innerHTML = "Contacting Twitter ...";
}

function handleResponse2(xmlhttp) {
	if( xmlhttp.readyState == 4 && xmlhttp.status == 200 ) {		
		document.getElementById("authButton").style.display='none';
		document.getElementById("authURL").style.display='none';
		document.getElementById("pinBox").style.display='none';
		document.cookie="twitter=yes";
		document.getElementById("testTweet").innerHTML ="Authorization completed!<br><br>" 
									 + "<button onclick=\"testTweet();\">Publish a test tweet</button>";
	}	
} 	

function testTweet() {
	xmlhttp=new XMLHttpRequest();	
	xmlhttp.onreadystatechange = function(){handleResponse3(xmlhttp);};
	xmlhttp.open('POST','http://nirmalbharat.mybluemix.net/rest/tweet',true);
	xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	xmlhttp.send("username="+getUsername()+"&"+"tweet="+"I want a clean India!");
	document.getElementById("testTweet").innerHTML = "Tweeting ...";
}

function handleResponse3(xmlhttp) {
	if( xmlhttp.readyState == 4 && xmlhttp.status == 200 ) {
		if(xmlhttp.responseText == "PASS") {
			document.getElementById("testTweet").innerHTML = "Test passed!";
			document.getElementById("nextButton").innerHTML = "<br><br><button onclick=\"location.href='checkin.html'\">Continue</button>";			
		}
		
		else {
			alert(xmlhttp.responseText);
			document.getElementById("testTweet").innerHTML = "Test failed :(";
			document.getElementById("nextButton").innerHTML = "<br><br><button onclick=\"location.href='checkin.html'\">Continue</button>";
		}
	}	
}

//3rd page
function uploadPic() {
	if(!validateCheckIn()) 
		return;
	var fd = new FormData();
    fd.append("file", document.getElementById('file').files[0]);
    fd.append("username",getUsername());
    fd.append("size",document.getElementById("file").files[0].size);
    fd.append("score",document.getElementById("score").value);
    fd.append("comment",document.getElementById("comment").value);
    fd.append("tweet", document.getElementById("tno").checked?"No":"Yes");
	xmlhttp=new XMLHttpRequest();	
	xmlhttp.onreadystatechange = function(){handleResponse4(xmlhttp);};
	xmlhttp.open('POST','http://nirmalbharat.mybluemix.net/rest/cleaninfo',true);	
	xmlhttp.send(fd);
	document.getElementById("pic").innerHTML = "<h2>Updating NirmalBharat</h2><br><br>" + 
	                                           "Please wait ... <br><br>" + 
	                                           "This may take a few seconds.";
		                                     
}

function handleResponse4(xmlhttp) {
	if( xmlhttp.readyState == 4 && xmlhttp.status == 200 ) {
		if(xmlhttp.responseText == "PASS") {
			document.getElementById("pic").innerHTML = "<h2>Uploaded!</h2> <br><br>Thank you for this information!." + 
			"<br><br><button onclick=\"location.href='checkin.html'\">Check in another location!</button>";
		}		
		else {
			alert(xmlhttp.responseText);
			var curr = document.getElementById("pic").innerHTML;
			document.getElementById("nextButton").innerHTML = curr + "<br><br>Upload failed. Please try again." + 
			"<br><br><button onclick=\"location.href='checkin.html'\">Check in again</button>";
		}
	}	
}

function countChars() {
	var str = document.getElementById("comment").value;	
	document.getElementById("remaining").innerHTML = 140-str.length;
}

function validateCheckIn() {
	if(document.getElementById('file').files[0] == undefined) {
		alert("You need to choose a picture to check-in");
		return false;
	}
	var str = document.getElementById("comment").value;
	if(str.length > 140 ) {
		alert("Your comment has passed the 140 character limit");
		return false;
	}
	
	var score = document.getElementById("score").value;
	if(score == "0") {
		alert("Please rate this location.");
		return false;
	}	
	return true;
}