{ // avoid variables ending up in the global scope

	// page components
	let missionDetails, missionsList, wizard,
		pageOrchestrator = new PageOrchestrator(); // main controller

	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "index.html";
		} else {
			pageOrchestrator.start(); // initialize the components
			pageOrchestrator.refresh();
		} // display initial content
	}, false);


	// Constructors of view components

	function PersonalMessage(_username, messagecontainer) {
		this.username = _username;
		this.show = function() {
			messagecontainer.textContent = this.username;
		}
	}


	function UserAlbums(_alert, _listcontainer, _listcontainerbody) {
		this.alert = _alert;
		this.listcontainer = _listcontainer;
		this.listcontainerbody = _listcontainerbody;

		this.reset = function() {
			this.listcontainer.style.visibility = "hidden";
		}

		this.show = function(next) {
			var self = this;
			makeCall("GET", "GetUserAlbums", null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var albumsToShow = JSON.parse(req.responseText);
							if (albumsToShow.length == 0) {
								self.alert.textContent = "You haven't created any album yet!";
								return;
							}
							self.update(albumsToShow); // self visible by closure
							if (next) next(); // show the default element of the list if present

						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							window.sessionStorage.removeItem('username');
						}
						else {
							self.alert.textContent = message;
						}
					}
				}
			);
		};

		this.update = function(userAlbums) {
			var elem, i, row, titlecell, creatorcell, datecell, linkcell, anchor;
			this.listcontainerbody.innerHTML = ""; // empty the table body
			// build updated list
			var self = this;
			userAlbums.forEach(function(album) { // self visible here, not this
				row = document.createElement("tr");
				titlecell = document.createElement("td");
				titlecell.textContent = album.title;
				row.appendChild(titlecell);

				creatorcell = document.createElement("td");
				creatorcell.textContent = album.creator;
				row.appendChild(creatorcell);

				datecell = document.createElement("td");
				datecell.textContent = album.creationDate;
				row.appendChild(datecell);

				linkcell = document.createElement("td");
				anchor = document.createElement("a");
				linkcell.appendChild(anchor);
				linkText = document.createTextNode("Show");
				anchor.appendChild(linkText);
				//anchor.missionid = mission.id; // make list item clickable
				anchor.setAttribute('albumid', album.id); // set a custom HTML attribute
				anchor.addEventListener("click", (e) => {
					// dependency via module parameter
					missionDetails.show(e.target.getAttribute("albumid")); // the list must know the details container
				}, false);
				anchor.href = "#";
				row.appendChild(linkcell);
				self.listcontainerbody.appendChild(row);
			});
			this.listcontainer.style.visibility = "visible";
		}
		
		this.autoclick = function(albumId) {
	      var e = new Event("click");
	      var selector = "a[albumid='" + albumId + "']";
	      var anchorToClick =  // the first mission or the mission with id = missionId
	        (albumId) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];
	      if (anchorToClick) anchorToClick.dispatchEvent(e);
	    }





	}










	function PageOrchestrator() {
		var alertContainer = document.getElementById("id_alert");

		this.start = function() {
			personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
				document.getElementById("id_username"));
			personalMessage.show();
		}

		userAlbums = new UserAlbums(
			alertContainer,
			document.getElementById("id_userAlbumContainer"),
			document.getElementById("id_userAlbumContainerBody"));

		this.refresh = function(currentMission) { // currentMission initially null at start
			alertContainer.textContent = "";        // not null after creation of status change
			userAlbums.reset();
			userAlbums.show(function() {
			missionsList.autoclick(currentMission);
			}); // closure preserves visibility of this
		};
	}




};