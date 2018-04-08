<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<script>
var socket;
socket= new WebSocket("ws://localhost:8080/QueueSC/ws");
function onSubmit()
{
	var createGuestUser= {type: "guestLogIn",email: "", firstName:"", lastName: "", qCode:"" };
	createGuestUser.email=document.getElementById("email").value;
	createGuestUser.firstName=document.getElementById("firstName").value;
	createGuestUser.lastName=document.getElementById("lastName").value;
	createGuestUser.qCode=document.getElementById("queueCode").value;
	socket.send(JSON.stringify(createGuestUser));
	
	
	
	
}
function connectToServer()
{
	
	socket.onopen=alert("connected");
	socket.onmessage=function(event)
	{
		var message=JSON.parse(event.data);
		document.getElementById("done").innerHTML=message.responseText="responseRecieved";
		alert(message.responseStatus);
		if(message.responseStatus=="email taken")
			{
			document.getElementById("emailError").innerHTML=message.responseStatus;
			}
		if(message.responseStatus=="queue code")
			{
			document.getElementById("Invalid Queue Code").innerHTML=message.responseResponse;
			}
		
	}
	 
	
}

</script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Continue as guest</title>
</head>
<body onload="connectToServer()">
<form name="guestUserForm"  onsubmit="onSubmit(); return false;">
<div id=done></div>
<div id=emailError></div>
Email: <br><input type="email" name="email" id="email" required><br>
First Name: <br><input type="text" name="firstName" id="firstName" required><br>
Last Name: <br><input type="text" name="lastName" id="lastName" required><br>
<div id=queueCodeError></div>
Queue Code: <br><input type="text" name= "queueCode" id="queueCode" required><br>
<input type="submit" value="Submit" id="submitButton">
</form>
</body>
</html>