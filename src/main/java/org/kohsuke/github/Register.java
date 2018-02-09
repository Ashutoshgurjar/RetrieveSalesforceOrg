package org.kohsuke.github;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.*;  
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;  
import com.sforce.soap.enterprise.Connector;
import com.sforce.soap.enterprise.DeleteResult;
import com.sforce.soap.enterprise.*;
import com.sforce.soap.enterprise.Error;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.SaveResult;
import com.sforce.soap.enterprise.sobject.Account;
import com.sforce.soap.enterprise.sobject.Contact;
import com.sforce.soap.enterprise.sobject.Attachment;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;  
import com.sforce.soap.metadata.RetrieveResult;
import com.sforce.soap.enterprise.sobject.*;

//@WebServlet (value = "/Register")
public class Register extends HttpServlet {
	
	static EnterpriseConnection connection;
	public static String USERNAME;
	public static String PASSWORD;
	public static String ENVIRONMENT;
	public static String RELEASE;
	public void doGet(HttpServletRequest request, HttpServletResponse response)  
            throws ServletException, IOException {  
		
		System.out.println("request:::::" + request.getQueryString());
		USERNAME = request.getParameter("userName");
		PASSWORD = request.getParameter("password");
		ENVIRONMENT = request.getParameter("environment");
		RELEASE = request.getParameter("release");
		System.out.println("UserName  "+ USERNAME);
		System.out.println("userPass  "+ PASSWORD);
		System.out.println("Environment" + ENVIRONMENT);
		System.out.println("RELEASE  "+ RELEASE);
		try{
			System.out.println("Check Dynamic Path ::: " + getServletContext().getRealPath("/"));
	    	  String arr[] = new String[6];
	    	  arr[0] = getServletContext().getRealPath("/");
	    	  arr[1] = "Test1";
	    	  arr[2] = USERNAME; 
	    	  arr[3] = PASSWORD;
	    	  arr[4] = ENVIRONMENT;
	    	  arr[5] = RELEASE;
	    	  //RetrieveSample.requestS = request;
	    	 // RetrieveSample.main(arr);
	    	   
	    	  MetaDataUtility.appPath = getServletContext().getRealPath("/");
	    	  
	    	  //DescribeMetadataUtility xyz = new DescribeMetadataUtility("rajeev.jain@simplyforce.com.automation", "Raj.jain@2026", "https://test.salesforce.com/services/Soap/c/39.0");
	    	  OrgBackupController abc = new OrgBackupController("rajeev.jain@simplyforce.com.automation", "Raj.jain@2026", "sandbox");
		      
	      }catch(Exception excp){excp.printStackTrace();}
		
		String text = "some text";
		response.setContentType("text/plain");  // Set content type of the response so that jQuery knows what it can expect.
	    response.setCharacterEncoding("UTF-8"); // You want world domination, huh?
	    response.getWriter().write(text); 
	   
	}
		
	public void doPost(HttpServletRequest request, HttpServletResponse response)  
	            throws ServletException, IOException {  
	  
		response.setContentType("text/html");  
		PrintWriter out = response.getWriter();  
		response.setIntHeader("Refersh", 5);        
		String n=request.getParameter("userName");  
		String p=request.getParameter("userPass");  
		String e=request.getParameter("userEmail");  
		String c=request.getParameter("userCountry");  
	    System.out.println("Test UserName ::: " + n);
	    
	    String USERNAME = "rajeev.jain@simplyforce.com.Test";
		String PASSWORD = "simplyforce2023";
		
	  
	      try{
	    	  System.out.println("Check Dynamic Path ::: " + getServletContext().getRealPath("/"));
	    	  String arr[] = new String[2];
	    	  arr[0] = getServletContext().getRealPath("/");
	    	  arr[1] = "Test1";
	    	  RetrieveSample.requestS = request;
	    	  RetrieveSample.main(arr);
		      
	      }catch(Exception excp){excp.printStackTrace();}
	   
	}
	
	
	// queries and displays the 5 newest contacts
	  private static void queryRelease() {
	     
	    System.out.println("Querying for the Attachment Record.");
	     
	    try {
	       
	      // query for the 5 newest contacts      
	      QueryResult  queryResults = connection.query("select Id, Body, Name from Attachment where ParentId = \'a0WC000000cdZTD\'"
	      		+ "AND Name = \'1package1.xml'");
	      System.out.println("Check Result Size ::: " + queryResults.getSize());
	      if (queryResults.getSize() > 0) {
	        for (int i=0;i<queryResults.getRecords().length;i++) {
	          // cast the SObject to a strongly-typed Contact
	          Attachment attach = (Attachment)queryResults.getRecords()[i];
	          //Blob MyBlob = EncodingUtil.base64Decode(attach.getBody());
	          //String myFile = MyBlob.toString(); 
	          /*System.out.println("Id: " + attach.getId() + " - Name: "+attach.getName()+" "+
	        		  attach.getBody().toString());*/
	        }
	      }
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	    }    
	    
	  }
}  