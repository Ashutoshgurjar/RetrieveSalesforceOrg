<%@ page language="java" contentType="text/html; charset=ISO-8859-1"  pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<meta charset="UTF-8">
		<title>Back Up</title>
		<link href='http://fonts.googleapis.com/css?family=Titillium+Web:400,300,600' rel='stylesheet' type='text/css'>
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/normalize/5.0.0/normalize.min.css">
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.0/jquery.min.js"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
		
        <script src="http://code.jquery.com/jquery-latest.min.js"></script>
        <!--  script type="text/javascript" src="../Scripts/jquery.blockUI.js"></script>-->
        <script>
        var blockUI = "";
        $(document).on("click", "#somebutton", function() {
        	// When HTML DOM "click" event is invoked on element with ID "somebutton", execute the following function...
        	var blockUIStatus = true;
        	var blockUI = document.createElement("div");
		    blockUI.setAttribute("id", "blocker");
		   // blockUI.innerHTML = '<div><img src="WEB-INF/image/loading.gif"></div><div><p style="text-align:center; font-size:160%; ">Processing...</p></div>'
		    document.body.appendChild(blockUI);
        	
            var userName = document.getElementById('userName');
        	var password = document.getElementById('password');
        	var environment = document.getElementById('environment');
        	var release = document.getElementById('release');
        	
        	setTimeout(function(){ 
        		if(blockUIStatus == true){
        			var t = document.createElement("div");
            		t.setAttribute("id", "message");
        		    t.innerHTML = '<div><p style="text-align:center; font-size:100%; color: #4B0082; font-size: 16px; font-family: "Book Antiqua", Georgia, Serif; "><b>Backup is taking time, file will get attached with related release record once it will get process completely</b></p></div>'
        		    document.body.appendChild(t);
        		    blockUIStatus = false;
            		blockUI.setAttribute("id", "");
            		blockUI.innerHTML = "";
        		}
        		}, 30000);
        	
        	$.get("Register?userName="+userName.value + "&password=" + password.value + "&environment=" + environment.value +"&release=" + release.value , function(responseText) {    // Execute Ajax GET request on URL of "someservlet" and execute the following function with Ajax response JSON...
        		blockUIStatus = false;
        		blockUI.setAttribute("id", "");
        		blockUI.innerHTML = "";
        		var t = document.createElement("div");
        		t.setAttribute("id", "message");
    		    t.innerHTML = '<div><p style="text-align:center; font-size:100%; color: #4B0082; font-size: 16px; font-family: "Book Antiqua", Georgia, Serif;"><b>Your request has been processed successfully. Back-Up file has been attached with release record.</b></p></div>'
    		    document.body.appendChild(t);
                
        		blockUIStatus = false;
            });
        	document.getElementById('userName').value = "";
        	document.getElementById('password').value = "";
        	document.getElementById('release').value = "";
        	document.getElementById('environment').value = "production";
        	return false;
        });
        </script>
    </head>
    <body>
    	<div class="form">
      <div class="tab-content">
        
        <div id="signup">   
          <img class = "center" style="width:45%;height:45%;">
        	<!-- img class = "center" src="/image/image.png" style="width:45%;height:45%;"-->
			<form style="text-align:center;">  
  				<div class="field-wrap">
            		<input type="email" required id = "userName" placeholder ="Target Org User Name" autocomplete="off" name="userName" value=""/>
         		</div>
          
          		<div class="field-wrap">
            		<input type="password" id = "password" placeholder = "Target Org Password" required autocomplete="off" name="userPass" value=""/>
          		</div>
          		
          		
          		<div class="field-wrap">
            		<input type="text"required id = "release" placeholder="Release Name" autocomplete="off" name="release" value="" />
         		</div>
          		
          		<div class="field-wrap">
          			<select name="environment" id = "environment" class="environment" required>
					  <option value="production">Production/Developer</option>
					  <option value="sandbox">Sandbox</option>
					</select>         		
          		</div>
          		         
          		<input id="somebutton" class="btn btn-info" value="Start Back-Up" type="button" style="width:90%"/>
  				
			</form>
		</div>
	<div>   
    	<h1></h1>
    </div>
	</div><!-- tab-content -->
</div> <!-- /form -->
	<script src='http://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script>
	<!--  script src="/WEB-INF/js/index.js"></script>  -->
	
    </body>
</html>