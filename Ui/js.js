function getUser() {
	var cookie = document.cookie;
	var xmlhttp = new XMLHttpRequest();
	var url = "http://10.151.60.46:8204/hello-service/user/";
	xmlhttp.open("GET", url, true);
	xhttp.setRequestHeader("Cookie",cookie);
	//sending the http-request
	xmlhttp.send();

	xmlhttp.onreadystatechange = function () {
		if (this.readyState == 4 && this.status == 200) {
			var myarr = JSON.parse(this.responseText);

		}
	}
}

document.getElementById("userprofile").onClick=UserProfile();

function UserProfile(){
	var url = "http://10.151.60.46:8204/hello-service/user";
	xmlhttp.open("POST", url, true);
	xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	//sending the http-request
	var cookie = document.cookie;
	xmlhttp.send("Cookie",cookie);

	xmlhttp.onreadystatechange = function () {
		if (this.readyState == 4 && this.status == 200) {
			document.cookie = "token="+this.responseText;
		} else{
			alert("Invalid Credentials");
		}
	}
}

function login(){
	var username = document.getElementById("username");
	var password = document.getElementById("password");
	var xmlhttp = new XMLHttpRequest();
	var url = "http://10.151.60.46:8204/registration-service/login";
	xmlhttp.open("POST", url, true);
	xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	//sending the http-request
	xmlhttp.send("userId="+username"&password="+password);

	xmlhttp.onreadystatechange = function () {
		if (this.readyState == 4 && this.status == 200) {
			localStorage.setItem("Token", this.responseText);
		} else{
			alert("Invalid Credentials");
		}
	}	
}

function logout(){
	document.cookie = "token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
	window.location="http://localhost:8080/index.html";
}