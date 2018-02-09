
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="javax.servlet.http.*"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta charset="UTF-8">
<title>Log In</title>
<link href='http://fonts.googleapis.com/css?family=Titillium+Web:400,300,600' rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/normalize/5.0.0/normalize.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.0/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<link rel="stylesheet" href="css/style.css">
<script>
        $(document).on("click", "#somebutton", function() {  // When HTML DOM "click" event is invoked on element with ID "somebutton", execute the following function...
            alert('Starting');
        	$.get("Register", function(responseText) {    // Execute Ajax GET request on URL of "someservlet" and execute the following function with Ajax response JSON...
                alert(responseText);
            });
        });
</script>
</head>
<body>
	
<%

 	String deploymentStatus1 = "";
	deploymentStatus1 = ""+session.getAttribute("deploymentStatus");
	Integer a = 0;%>
	<%=deploymentStatus1%>
	<% if(deploymentStatus1 != null && !deploymentStatus1.equals("Succeeded") ){
		do{
			deploymentStatus1 = ""+session.getAttribute("deploymentStatus");
			System.out.println("Do loop-- "+ a);
			a++;
	%>
			<%= deploymentStatus1 %> <%
		}while(a <= 10);
	}	 
	 %>
	 <div class="form">
           
      <div class="tab-content">
        
        <div id="signup">   
          
        	<img class = "center" src="image\person.png" style="width:200px;height:200px;">
			<form action="servlet/Register" method="post">  
  				<div class="field-wrap">
            		<label>
              			User Name<span class="req">*</span>
            		</label>
            		<input type="email" required autocomplete="off" name="userName"/>
         		</div>
          
          		<div class="field-wrap">
            		<label>
              			Password<span class="req">*</span>
            		</label>
            		<input type="password" required autocomplete="off" name="userPass"/>
          		</div>
          		
          		
          		<input type="submit" class="btn btn-info" value="Log In"/>
  				
			</form>
		</div>
	<div>   
    	<h1></h1>
    </div>
	</div><!-- tab-content -->
</div> <!-- /form -->
	<script src='http://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script>
	<script src="js/index.js"></script>  
	
	
</body>
</html>