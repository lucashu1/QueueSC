<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<script>
	var socket;
	socket = new WebSocket("ws://localhost:8080/QueueSC/ws");
	var createGuestUser = {
		type : "guestLogInRequest",
		email : "",
		firstName : "",
		lastName : "",
		qCode : ""
	};
	function onSubmit() {

		createGuestUser.email = document.getElementById("email").value;
		createGuestUser.firstName = document.getElementById("firstName").value;
		createGuestUser.lastName = document.getElementById("lastName").value;
		createGuestUser.qCode = document.getElementById("queueCode").value;
		socket.send(JSON.stringify(createGuestUser));

	}
	function connectToServer() {

		socket.onmessage = function(event) {
			var message = JSON.parse(event.data);
			alert(message.responseStatus);
			if (message.responseStatus == "emailTaken") {
				document.getElementById("emailError").innerHTML = message.responseStatus;
			}
			if (message.responseStatus == "qCodeInvalid") {
				document.getElementById("Invalid Queue Code").innerHTML = message.responseResponse;
			}
			if (message.responseStatus == "success") {
				alert("success");
				window.location = "JoinQueue.html?qCode="
						+ createGuestUser.qCode;
			}

		}

	}
</script>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Continue as guest</title>
	<!-- begin boostrap import -->
	<link rel="stylesheet"
		href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"
		integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm"
		crossorigin="anonymous">
	<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"
		integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"
		crossorigin="anonymous"></script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"
		integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
		crossorigin="anonymous"></script>
	<script
		src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"
		integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
		crossorigin="anonymous"></script>
	<!-- end boostrap import -->

	<link rel="stylesheet" href="GeneralStyle.css">
</head>

<body onload="connectToServer()">
	<div id="navbar" class="topnav">
		<h1>QueueSC</h1>
	</div>
	
	<div class="container vertical-center input-form-wrapper">
		<form name="guestUserForm" class="general-form-style" onsubmit="onSubmit(); return false;">
			
			<div class="form-group">
				<label for="email">Email address</label>
				<input type="email" class="form-control" name="email" id="email" placeholder="Enter email" required>
				<div id=emailError></div> 
			</div>
			
			<div class="form-group">
				<label for="firstName">First Name</label> 
				<input type="text" class="form-control" name="firstName" id="firstName" required>
			</div>
			
			<div class="form-group">
				<label for="lastName">Last Name</label> 
				<input type="text" class="form-control" name="lastName" id="lastName" required>
			</div>
			
			<div class="form-group">
				<label for="queueCode">Queue Code</label> 
				<input type="text" class="form-control" name="queueCode" id="queueCode" required>
				<div id=queueCodeError></div>
			</div>
			
			
			<button type="submit" value="Submit" id="submitButton" class="btn btn-primary">Continue</button>
		</form>
	</div>

</body>
</html>