{ // avoid variables ending up in the global scope

	// page components
	let userAlbums, otherAlbums, createAlbum,
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
			var row, titlecell, creatorcell, datecell, linkcell, anchor;
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
					albumDetails.show(e.target.getAttribute("albumid")); // the list must know the details container
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
	
	


	function OtherAlbums(_alert, _listcontainer, _listcontainerbody) {
		this.alert = _alert;
		this.listcontainer = _listcontainer;
		this.listcontainerbody = _listcontainerbody;

		this.reset = function() {
			this.listcontainer.style.visibility = "hidden";
		}

		this.show = function(next) {
			var self = this;
			makeCall("GET", "GetOtherAlbums", null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var albumsToShow = JSON.parse(req.responseText);
							if (albumsToShow.length == 0) {
								self.alert.textContent = "No album has been created yet!";
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
			var row, titlecell, creatorcell, datecell, linkcell, anchor;
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
				anchor.setAttribute('otheralbumid', album.id); // set a custom HTML attribute
				anchor.addEventListener("click", (e) => {
					// dependency via module parameter
					albumDetails.show(e.target.getAttribute("otheralbumid")); // the list must know the details container
				}, false);
				anchor.href = "#";
				row.appendChild(linkcell);
				self.listcontainerbody.appendChild(row);
			});
			this.listcontainer.style.visibility = "visible";
		}
	}


	function CreateAlbum(_alert, _form, _listcontainer, _listcontainerbody, _albumtitle){
		this.alert = _alert;
		this.form = _form;
		this.listcontainer = _listcontainer;
		this.listcontainerbody = _listcontainerbody;
		this.albumtitle = _albumtitle;
		
		this.reset = function() {
			this.listcontainer.style.visibility = "hidden";
		}
		
		this.show = function(){
			var self = this;
			makeCall("GET", "GetUserImages", null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var userImages = JSON.parse(req.responseText);
							if (userImages.length == 0) {
								self.alert.textContent = "You haven't uploaded any image yet!";
								return;
							}
							self.update(userImages); // self visible by closure

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
		}
		
		this.update = function(userImages) {
			var row, checkbox, imagetitle, datecell, input, linkcell, anchor;
			this.listcontainerbody.innerHTML = ""; // empty the table body
			// build updated list
			var self = this;
			userImages.forEach(function(image) { // self visible here, not this
				row = document.createElement("tr");
				
				checkbox = document.createElement("td");
				input = document.createElement("INPUT");
				input.setAttribute("type", "checkbox")
				input.setAttribute("value",image.id)
				input.setAttribute("name","id")
				checkbox.appendChild(input);
				row.appendChild(checkbox);
				
				imagetitle = document.createElement("td");
				imagetitle.textContent = image.title;
				row.appendChild(imagetitle);

				self.listcontainerbody.appendChild(row);
			});
			this.listcontainerbody.style.visibility = "visible";
		}
		
		
		this.registerEvents = function(orchestrator) {
				var params=[]
				//Get all the checkbox checked which each one from the form has the idImage as value 
  			 	this.form.querySelector("input[type='submit']").addEventListener('click', (e) => {
  					let values = Array.from(document.querySelectorAll('input[type=checkbox]:checked'))
    				.map(item => item.value);
	    			for(let i=0;i<values.length;i++){
						params[i]="id="+parseInt(values[i]);
					}
					var str;
					for(let n=0;n<params.length;n++){
						if(n==0){
							str=params[n]+"&";
						}
						else{
							str+=params[n]+"&";
						}
					}
	        		var formToSend = e.target.closest("form");
			       if (formToSend.checkValidity()) {
			          var self = this;
		 			  makeCall("POST", "CreateAlbum?title=" + self.albumtitle.value + "&" + str, formToSend,function(req) {
			              if (req.readyState == XMLHttpRequest.DONE) {
			                var message = req.responseText; //error messagge 
			              if (req.status == 200) {
			                   createAlbum.show()
			                   userAlbums.show();
			              }else if (req.status == 403) {
		                       window.location.href = req.getResponseHeader("Location");
		                       window.sessionStorage.removeItem('username');
		                  }else {
							  console.error("Error response:", message);
			                   self.alert.textContent = message;
			                   self.show();
				                }
				              }
				            }
				          );
				  }else{
				     this.alert.textContent = "Required parameters for form missing";
				}
		     	 }); 
		     }   
		
		
	}


	function ShowAlbum(_alert, _listcontainer, _listcontainerbody, _next, _prev){
		this.alert = _alert;
		this.listcontainer = _listcontainer;
		this.listcontainerbody = _listcontainerbody;
		this.next=_next;
		this.prev=_prev;

		this.reset = function() {
			this.listcontainer.style.visibility = "hidden";
			this.next.style.visibility = "hidden";
			this.prev.style.visibility = "hidden";
		}
		
		var imagesAlbum;
	        this.show = function(albumid) {
		        var self = this;
		        makeCall("GET", "SelectAlbum?albumId=" + albumid, null, function(req) {
		         	 if (req.readyState == 4) {
		           		 var message = req.responseText;
			             if (req.status == 200) {
			                 imagesAlbum = JSON.parse(req.responseText);
				              if (imagesAlbum.length == 0) {
				                self.alert.textContent = "No images for this album!";
				                return;
				              }
				          self.alert.textContent = "";
			              self.update(imagesAlbum,0);//Pass the index 0, initialize the table
			            
			             }else if (req.status == 403) {
			                  window.location.href = req.getResponseHeader("Location");
			                  window.sessionStorage.removeItem('username');
		                 }else {
			            	  self.alert.textContent = message;
			            	  self.listcontainer.style.visibility = "hidden";
			          	 }
		            }
		        }
		      	);
	       };
		    
		   var imagesToShow=[];
		   var currentIndex
		   
		   //Update the table contenet the images of the album selected  
		   this.update = function(imagesAlbum, index) {
		   		var row, destcell, imagecell, titlecell, imagerow, titlerow, anchor;
		   		this.listcontainerbody.innerHTML = ""; // empty the table body
			    var self = this;
			    var indexMax=index+5
			    imagesToShow=Array.from(imagesAlbum);
			    row = document.createElement("tr");
			    for(var h;index<imagesToShow.length && index<indexMax ;index++ ){ 
				        destcell = document.createElement("td");
				        
				        imagerow = document.createElement("tr");
				        destcell.appendChild(imagerow);
				        
				        imagecell = document.createElement("img");
				        imagecell.src= "GetImage/" + imagesToShow[index].path;
				        imagecell.setAttribute("idImage",imagesToShow[index].id);
				        imagecell.setAttribute("height", "75");
					    
					    
				        imagecell.addEventListener('mouseenter', function(e) {
							const modal = document.querySelector(destcell.dataset.modalTarget);
							showFullImage.show(e.target.getAttribute("idImage"), modal);
							//openModal(modal);
					    });
					    
					    imagerow.appendChild(imagecell);
					    destcell.appendChild(imagerow);
				        
				        titlerow = document.createElement("tr");
				        titlecell = document.createElement("td");
				        titlecell.textContent = imagesToShow[index].title;
				        titlerow.appendChild(titlecell);
				        
				        destcell.appendChild(titlerow);
				        
				        destcell.setAttribute("data-modal-target", "#modal");
				        
				        row.appendChild(destcell);
			      		 
			      		self.listcontainerbody.appendChild(row);
			 	 }
			     currentIndex=index
			     this.listcontainer.style.visibility = "visible";
			     if(indexMax<imagesToShow.length){
			        this.next.style.visibility = "visible";
			     }else{
					this.next.style.visibility = "hidden";
				 }
					
			     if(currentIndex>5){
			        this.prev.style.visibility = "visible";
			     }else{
				 	this.prev.style.visibility = "hidden";
				}
			}
	      //Add an event listenr to next and prev button
	        this.registerEvents = function(orchestrator) {
		   		this.next.addEventListener('click', (e) => { 
				this.update(imagesAlbum,currentIndex)
		    	});
		    	//Check if the last index is divisble per 5, if not bring the index to the closeset multiple of 5 greater then currentIndex
				this.prev.addEventListener('click', (e) => {	
					if(currentIndex%5!=0){
						while(currentIndex%5!=0){
							  currentIndex++;
						}
					 }
					currentIndex=currentIndex-10
					this.update(imagesAlbum,currentIndex)
					});
				}
			
	}



	function ShowFullImage(_modalImageTitle, _modalImageToShow, _modalImageDescription, _modalComments, _modalCommentsBody, _modalNoComments, _commentForm, _commentMessage){
		this.modal;
		this.modalImageTitle = _modalImageTitle;
		this.modalImageToShow = _modalImageToShow;
		this.modalImageDescription = _modalImageDescription
		this.modalComments = _modalComments;
		this.modalCommentsBody = _modalCommentsBody;
		this.modalNoComments = _modalNoComments;
		this.commentForm = _commentForm;
		this.commentMessage = _commentMessage;
		this.imageId;
		
		this.reset = function() {
			this.modalComments.style.visibility = "hidden";
			this.modalNoComments.style.visibility = "hidden";
		}
		
		
		this.show = function(idImage, modalToOpen) {
	     	this.modal = modalToOpen;
	     	this.imageId = idImage;
	     	var self = this;
	      	makeCall("GET","SelectImage?imageId="+ idImage, null,function(req) {
	          if (req.readyState == 4) {
	          	var message = req.responseText;
	          	if (req.status == 200) {
	               currentImageShow = JSON.parse(req.responseText);
	               makeCall("GET","ShowComments?imageId="+ idImage, null,function(req) {
				   		if (req.readyState == 4) {
				            	var message = req.responseText;
				            if (req.status == 200) {
				             	 var comments = JSON.parse(req.responseText);
				            if(comments.length==0){
								 showFullImage.noComments(currentImageShow);
							}else{
				             	 showFullImage.update(currentImageShow,comments);
				             	 //openModal(modalToOpen);
				                }
				            }else if (req.status == 403) {
			                  	window.location.href = req.getResponseHeader("Location");
			                 	window.sessionStorage.removeItem('username');
			                }else {
				            	self.alert.textContent = message;
				          	}
				        }
				    });   
	           }else if (req.status == 403) {
                  window.location.href = req.getResponseHeader("Location");
                  window.sessionStorage.removeItem('username');
               }else {
	              //self.alert.textContent = message;
	          	}
	          }
	        });
	      }
			
		this.noComments = function(currentImageToShow){
			this.modalNoComments.style.visibility = "visible";
			this.modalComments.style.visibility = "hidden";
			
			this.modalImageTitle.textContent = currentImageToShow.title;
			this.modalImageDescription.textContent = currentImageToShow.description;
			this.modalImageToShow.src="GetImage/"+currentImageShow.path
			openModal(modal);
		}
		
		
		this.update = function(currentImageToShow, comments){
			this.modalComments.style.visibility = "visible";
			this.modalNoComments.style.visibility = "hidden";
			
			this.modalImageTitle.textContent = currentImageToShow.title;
			this.modalImageDescription.textContent = currentImageToShow.description;
			this.modalImageToShow.src="GetImage/"+currentImageShow.path
			
			this.modalComments.innerHTML = "";
			this.modalCommentsBody.innerHTML = "";
			
			self = this;
			
			comments.forEach(function(comment) { // self visible here, not this
		        row = document.createElement("tr");
		        destcell = document.createElement("td");
		        destcell.textContent = comment.user;
		        row.appendChild(destcell);
		        datecell = document.createElement("td");
		        datecell.textContent = comment.text;
		        row.appendChild(datecell);
		        self.modalComments.appendChild(row);
	     	});
	     	
	     	openModal(modal);
		}
	      
	      
	    this.registerEvents = function(orchestrator){
			self = this;
			this.commentForm.querySelector("input[type='submit']").addEventListener('click', (e) => {
				var formToSend = e.target.closest("form");
				var text= document.getElementById("textArea");
				
				if (formToSend.checkValidity() && text.value.length!=0 && text.value.length<180) {
		          
	 			  makeCall("POST", "AddComment?idImage=" + this.imageId +"&text=" + text.value , formToSend,function(req) {
		              if (req.readyState == XMLHttpRequest.DONE) {
		                var message = req.responseText; //error messagge 
		              if (req.status == 200) {
		                  self.show(self.imageId, self.modal);
		              }else if (req.status == 403) {
	                       window.location.href = req.getResponseHeader("Location");
	                       window.sessionStorage.removeItem('username');
	                  }else {
		                   self.show(self.imageId, self.modal);
			                }
			              }
			            }
			          );
			        }else{
						if(text.value.length==0)
							this.commentMessage.textContent="You cannot add a blank comment";
						else
							this.commentMessage.textContent="The comment size must be under 180";
					}
			});
		}
	      
		
		
		
		
		
		
	}


	function UploadImage(_alert, _uploadForm, _imageFile, _imageTitle, _imageDescription){
		this.alert = _alert;
		this.uploadForm = _uploadForm;
		this.file = _imageFile;
		this.title = _imageTitle;
		this.description = _imageDescription;
		
		this.reset = function() {
		}
		
		this.registerEvents = function(orchestrator) {
  			this.uploadForm.querySelector("input[type='submit']").addEventListener('click', (e) => {
				var formToSend = e.target.closest("form");
			       if (formToSend.checkValidity()) {
			          var self = this;
		 			  makeCall("POST", "UploadImage", formToSend,function(req) {
			              if (req.readyState == XMLHttpRequest.DONE) {
			                var message = req.responseText; //error messagge 
			              if (req.status == 200) {
			                   createAlbum.show();
			              }else if (req.status == 403) {
		                       window.location.href = req.getResponseHeader("Location");
		                       window.sessionStorage.removeItem('username');
		                  }else {
							  console.error("Error response:", message);
			                   self.alert.textContent = message;
			                   self.show();
				                }
				              }
				            }
				          );
				  }else{
				     this.alert.textContent = "Required parameters for form missing";
				}
		     	 }); 
		     }
	}
	
	
	
	function openModal(modal) {
  		if (modal == null) return
  		modal.classList.add('active')
  		overlay.classList.add('active')
	}
	
	
	function closeModal(modal) {
  		if (modal == null) return
  		modal.classList.remove('active')
  		overlay.classList.remove('active')
	}



	function PageOrchestrator() {
		var alertContainer = document.getElementById("id_alert");

		this.start = function() {
			personalMessage = new PersonalMessage(
				sessionStorage.getItem('username'),
				document.getElementById("id_username"));
			
			personalMessage.show();
			
			userAlbums = new UserAlbums(
			document.getElementById("id_emptyUserAlbumAlert"),
			document.getElementById("id_userAlbumContainer"),
			document.getElementById("id_userAlbumContainerBody"));
			
		otherAlbums = new OtherAlbums(
			document.getElementById("id_emptyOtherAlbumAlert"),
			document.getElementById("id_otherAlbumContainer"),
			document.getElementById("id_otherAlbumContainerBody"));

		createAlbum = new CreateAlbum(
			document.getElementById("createalbummsg"),
			document.getElementById("id_createAlbumForm"),
			document.getElementById("id_createAlbumContainer"),
			document.getElementById("id_createAlbumContainerBody"),
			document.getElementById("id_albumTitle"));
			
		albumDetails = new ShowAlbum(
			document.getElementById("id_emptyAlbumAlert"),
			document.getElementById("id_albumContainer"),
			document.getElementById("id_albumContainerBody"),
			document.getElementById("next"),
			document.getElementById("prev"));
			
		sendImage = new UploadImage(
			document.getElementById("uploadimgmsg"),
			document.getElementById("id_uploadImageForm"),
			document.getElementById("id_imageFile"),
			document.getElementById("id_imageTitle"),
			document.getElementById("id_imageDescription"));
			
		showFullImage = new ShowFullImage(
			document.getElementById("modalImageTitle"),
			document.getElementById("modalImageToShow"),
			document.getElementById("modalImageDescription"),
			document.getElementById("modalComments"),
			document.getElementById("modalCommentsBody"),
			document.getElementById("modalNoComments"),
			document.getElementById("id_createComment"),
			document.getElementById("id_createcommentmsg"));
			

		createAlbum.registerEvents(this);
		albumDetails.registerEvents(this);
		sendImage.registerEvents(this);
		showFullImage.registerEvents(this);


			
		document.querySelector("a[href='Logout']").addEventListener('click', () => {
	        window.sessionStorage.removeItem('username');
	      })
	      
	    
	    
	      
	    const closeModalButton = document.querySelector('[data-close-button]');
	    closeModalButton.addEventListener('click', () => {
    	const modalClose = closeModalButton.closest('.modal')
    	closeModal(modalClose);
  		})
  		
  		const overlay = document.getElementById('overlay')
  		overlay.addEventListener('click', () => {
  			const modals = document.querySelectorAll('.modal.active')
  			modals.forEach(modal => {
    			closeModal(modal)
  			})
		})
		
		
		}


		this.refresh = function(currentAlbum) { // currentAlbum initially null at start
			alertContainer.textContent = "";        // not null after creation of status change
			userAlbums.reset();
			userAlbums.show(function() {
			userAlbums.autoclick(currentAlbum);
			}); // closure preserves visibility of this
			otherAlbums.reset();
			otherAlbums.show();
			albumDetails.reset();
			createAlbum.reset();
			createAlbum.show();
			sendImage.reset();
			showFullImage.reset();
			
		};
	}


};