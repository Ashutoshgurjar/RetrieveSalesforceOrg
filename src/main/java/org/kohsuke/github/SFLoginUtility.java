package org.kohsuke.github;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.enterprise.Connector;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.LoginResult;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class SFLoginUtility {
	
	//Hold Metadata Connection Details with it
	public MetadataConnection metadataConnection;
	public EnterpriseConnection connection;
	
	//Class Constructor
	public SFLoginUtility(String username, String password, String loginUrl) throws ConnectionException{
		
		createMetadataConnection(username, password, loginUrl);
	}
	 private void createMetadataConnection(final String username,
	            final String password, final String loginUrl)
	            throws ConnectionException {
		 
		 //Set Login Config first to set Login End Point URL
		 final ConnectorConfig loginConfig = new ConnectorConfig();
		 loginConfig.setAuthEndpoint(loginUrl);
		 loginConfig.setManualLogin(true);
		 loginConfig.setServiceEndpoint(loginUrl);
		 
		 //Get Session Id and othe login results
		 LoginResult loginResult = (new EnterpriseConnection(loginConfig)).login(username, password);
		 
		 connection = Connector.newConnection(loginConfig);
		 
		 //Prepare Metadata Config to get Metadata API connection in Salesforce
		 final ConnectorConfig metadataConfig = new ConnectorConfig();
		 metadataConfig.setServiceEndpoint(loginResult.getMetadataServerUrl());
		 metadataConfig.setSessionId(loginResult.getSessionId());
		 
		 //Set Metadata Conncection.
		 this.metadataConnection = new MetadataConnection(metadataConfig);
	 }
}
