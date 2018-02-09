package org.kohsuke.github;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.LoginResult;
import com.sforce.soap.metadata.DescribeMetadataObject;
import com.sforce.soap.metadata.DescribeMetadataResult;
import com.sforce.soap.metadata.FileProperties;
import com.sforce.soap.metadata.ListMetadataQuery;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class DescribeMetadataUtility {
	
	//Hold Metadata Connection Details with it
	private MetadataConnection metadataConnection;
	
	// one second in milliseconds
    private static final long ONE_SECOND = 1000;
    
    // maximum number of attempts to retrieve the results
    private static final int MAX_NUM_POLL_REQUESTS = 50; 

    // manifest file that controls which components get retrieved
    private static final String MANIFEST_FILE = "package.xml";  

    private static final double API_VERSION = 39.0; 
	
    static String appPath = "";
    
	public static void main(String[] args) throws Exception {
		
		
	}
	public DescribeMetadataUtility(String username, String password, String loginUrl) 
            throws ConnectionException {
        createMetadataConnection(username, password, loginUrl);
        //describeMetadata();
        listMetadata();
    }
	
	 private void createMetadataConnection(final String username,
            final String password, final String loginUrl)
            throws ConnectionException {

        final ConnectorConfig loginConfig = new ConnectorConfig();
        loginConfig.setAuthEndpoint(loginUrl);
        loginConfig.setServiceEndpoint(loginUrl);
        loginConfig.setManualLogin(true);
        LoginResult loginResult = (new EnterpriseConnection(loginConfig)).login(
                username, password);

        final ConnectorConfig metadataConfig = new ConnectorConfig();
        metadataConfig.setServiceEndpoint(loginResult.getMetadataServerUrl());
        metadataConfig.setSessionId(loginResult.getSessionId());
        this.metadataConnection = new MetadataConnection(metadataConfig);
    }
	
	 public void describeMetadata() {
	  try {
		  
	    double apiVersion = 39.0;
	    
	    // Assuming that the SOAP binding has already been established.
	    DescribeMetadataResult res = metadataConnection.describeMetadata(apiVersion);
	    StringBuffer sb = new StringBuffer();
	    Integer counter = 0;
	    
	    if (res != null && res.getMetadataObjects().length > 0) {
	      for (DescribeMetadataObject obj : res.getMetadataObjects()) {
	        sb.append("***************************************************\n");
	        sb.append("XMLName: " + obj.getXmlName() + "\n");
	        sb.append("***************************************************\n");
	        counter++;
	      }
	    } else {
	      sb.append("Failed to obtain metadata types.");
	    }
	    System.out.println(sb.toString());
	    System.out.println("Check counter ::: " + counter);
	  } catch (ConnectionException ce) {
	    ce.printStackTrace();
	  }
	}
	 public void listMetadata() {
		  try {
		    ListMetadataQuery query = new ListMetadataQuery();
		   
		    query.setType("EmailFolder");
		    //query.setFolder("unfiled$public");
		    double asOfVersion = 41.0;
		    // Assuming that the SOAP binding has already been established.
		    FileProperties[] lmr = metadataConnection.listMetadata(
		        new ListMetadataQuery[] {query}, asOfVersion);
		    if (lmr != null) {
		    	Integer counter = 1;
		      for (FileProperties n : lmr) {
		    	  
		    	  Calendar cal = n.getLastModifiedDate();
		    	  
		    	  
		        System.out.println(counter + " ::: Type ::: " + n.getType() + " :: API Name: " + n.getFullName() + " Last Modified By Name ::: " + n.getLastModifiedByName() + " Managebale State :: " + n.getManageableState()
		        + " Last Modified Date :: " + cal.getTime());
		        System.out.println("---------------------- Next Comonent ----------------------- ");
		        counter++;
		      }
		    }            
		  } catch (ConnectionException ce) {
		    ce.printStackTrace();
		  }
		}
}
