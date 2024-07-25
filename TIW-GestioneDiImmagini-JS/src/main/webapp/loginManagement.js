/**
 * Login management
 */
(function() { // avoid variables ending up in the global scope

	document.getElementById("loginbutton").addEventListener('click', (e) => {
		console.log("Login");
		var form = e.target.closest("form");
		if (form.checkValidity()) {
			makeCall("POST", 'CheckLogin', e.target.closest("form"),
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						switch (x.status) {
							case 200:
								sessionStorage.setItem('username', message);
								window.location.href = "Home.html";
								break;
							case 400: // bad request
								document.getElementById("errormessage").textContent = message;
								break;
							case 401: // unauthorized
								document.getElementById("errormessage").textContent = message;
								break;
							case 500: // server error
								document.getElementById("errormessage").textContent = message;
								break;
						}
					}
				}
			);
		} else {
			form.reportValidity();
		}
	});


	document.getElementById("signupbutton").addEventListener('click', (e) => {
		console.log("Sign up");
		var form = e.target.closest("form");
		this.email = document.querySelector('input[name=signupEmail]').value;
		console.log(email);
		this.validateEmail = email.toLowerCase()
			.match(
				/^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
			);
		this.repeatPwd = document.querySelector('input[name=ripPassword]').value;
		this.pwd = document.querySelector('input[name=signupPassword]').value;
		console.log(this.validateEmail && repeatPwd === pwd && pwd.length > 5);
		if (this.validateEmail && repeatPwd === pwd && pwd.length > 5) {
			if (form.checkValidity()) {
				makeCall("POST", 'CheckRegistration', e.target.closest("form"),
					function(x) {
						if (x.readyState == XMLHttpRequest.DONE) {
							var message = x.responseText;
							switch (x.status) {
								case 200:
									document.getElementById("signupmessage").textContent = "User created, now you can log-in";
									break;
								case 400: // bad request
									document.getElementById("signupmessage").textContent = message;
									break;
								case 401: // unauthorized
									document.getElementById("signupmessage").textContent = message;
									break;
								case 500: // server error
									document.getElementById("signupmessage").textContent = message;
									break;
							}
						}
					}
				);
			}
		} else {
			document.getElementById("signupmessage").textContent = "Form required value are not correct";


		}
	});


})();