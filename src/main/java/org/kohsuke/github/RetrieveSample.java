package org.kohsuke.github;
import java.io.*;
import java.util.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sforce.soap.metadata.AsyncResult;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.metadata.RetrieveMessage;
import com.sforce.soap.metadata.RetrieveRequest;
import com.sforce.soap.metadata.RetrieveResult;
import com.sforce.soap.metadata.RetrieveStatus;
import com.sforce.soap.enterprise.LoginResult;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.soap.metadata.PackageTypeMembers;
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
public class RetrieveSample {

    // Binding for the metadata WSDL used for making metadata API calls
    private MetadataConnection metadataConnection;
    public static HttpServletRequest requestS;
    
    static BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));

    // one second in milliseconds
    private static final long ONE_SECOND = 1000;
    // maximum number of attempts to retrieve the results
    private static final int MAX_NUM_POLL_REQUESTS = 50; 

    // manifest file that controls which components get retrieved
    private static final String MANIFEST_FILE = "package.xml";  

    private static final double API_VERSION = 39.0; 
    
    static EnterpriseConnection connection;
    
    static String appPath = "";
    
    public static void main(String[] args) throws Exception {
    	appPath = args[0];
        final String USERNAME = args[2];
        // This is only a sample. Hard coding passwords in source files is a bad practice.
        final String PASSWORD = args[3]; 
        final String ENVIRONMENT = args[4];
        final String RELEASE = args[5];
        String URL = "https://test.salesforce.com/services/Soap/c/39.0";
        System.out.println("--------------------------------");
        System.out.println(USERNAME);
        if(ENVIRONMENT == "production"){
        	URL = "https://login.salesforce.com/services/Soap/c/39.0";
        }
        	        
        RetrieveSample sample = new RetrieveSample(USERNAME, PASSWORD, URL);
        sample.retrieveZip(args[2], args[3], args[5]);
    }
    
    public RetrieveSample(String username, String password, String loginUrl) 
            throws ConnectionException {
        createMetadataConnection(username, password, loginUrl);
    }

    
    private void retrieveZip(String userName, String Password, String Release) throws RemoteException, Exception
    {
    	String USERNAME = userName;
  		String PASSWORD = Password;
  		String RELEASE = Release;
  		System.out.println("RELEASE :  "+RELEASE);
  		try{
        	//Main ma = new Main();  
			 ConnectorConfig config = new ConnectorConfig();
			 config.setUsername(USERNAME);
			 config.setPassword(PASSWORD);
			 connection = Connector.newConnection(config);
  		}catch (ConnectionException e1) {
    	    e1.printStackTrace();
 	   }
  		QueryResult  queryResults = connection.query("select Id from SFDC_Release__c where Name = \'"+RELEASE+"\'");
  		String relaeseId = ""+queryResults.getRecords()[0].getId();
  		System.out.println("relaeseId----- "+queryResults.getRecords()[0].getId());
  		
        RetrieveRequest retrieveRequest = new RetrieveRequest();
        // The version in package.xml overrides the version in RetrieveRequest
        retrieveRequest.setApiVersion(API_VERSION);
        setUnpackaged(retrieveRequest, relaeseId);

        // Start the retrieve operation
        AsyncResult asyncResult = metadataConnection.retrieve(retrieveRequest);
        String asyncResultId = asyncResult.getId();
        
        // Wait for the retrieve to complete
        int poll = 0;
        long waitTimeMilliSecs = ONE_SECOND;
        RetrieveResult result = null;
        do {
            Thread.sleep(waitTimeMilliSecs);
            // Double the wait time for the next iteration
            waitTimeMilliSecs *= 2;
            if (poll++ > MAX_NUM_POLL_REQUESTS) {
                throw new Exception("Request timed out.  If this is a large set " +
                "of metadata components, check that the time allowed " +
                "by MAX_NUM_POLL_REQUESTS is sufficient.");
            }
            result = metadataConnection.checkRetrieveStatus(
                    asyncResultId, true);
            System.out.println("Retrieve Status: " + result.getStatus());
            HttpSession session = requestS.getSession();
            String s = ""+result.getStatus();
            session.setAttribute("deploymentStatus",s);
            System.out.println(session.getAttribute("deploymentStatus"));
            
        } while (!result.isDone());

        if (result.getStatus() == RetrieveStatus.Failed) {
            throw new Exception(result.getErrorStatusCode() + " msg: " +
                    result.getErrorMessage());
        } else if (result.getStatus() == RetrieveStatus.Succeeded) {      
            // Print out any warning messages
            StringBuilder buf = new StringBuilder();
            if (result.getMessages() != null) {
                for (RetrieveMessage rm : result.getMessages()) {
                    buf.append(rm.getFileName() + " - " + rm.getProblem());
                }
            }
            if (buf.length() > 0) {
                System.out.println("Retrieve warnings:\n" + buf);
            }
          
            // Write the zip to the file system
            System.out.println("Writing results to zip file");
            /*ByteArrayInputStream bais = new ByteArrayInputStream(result.getZipFile());
            File resultsFile = new File(appPath + "//retrieveResults.zip");
            FileOutputStream os = new FileOutputStream(resultsFile);
            try {
                ReadableByteChannel src = Channels.newChannel(bais);
                FileChannel dest = os.getChannel();
                copy(src, dest);
                
                System.out.println("Results written to " + resultsFile.getAbsolutePath());
            } finally {
                os.close();
            }*/
            
			 try{
			      //Byte Array
				  Attachment attachment = new Attachment();
				  attachment.setBody(result.getZipFile());
				  attachment.setName("RetrieveResults.zip");
				  attachment.setParentId(relaeseId);
				  attachment.setContentType("application/zip");
				  Attachment[] records = new Attachment[1];
				  records[0] = attachment;
				  
				  // create the records in Salesforce.com
				  SaveResult[] saveResults = connection.create(records);
				  
				  // check the returned results for any errors
				  for (int i=0; i< saveResults.length; i++) {
				    if (saveResults[i].isSuccess()) {
				      System.out.println(i+". Successfully created record - Id: " + saveResults[i].getId());
				    } else {
				      Error[] errors = saveResults[i].getErrors();
				      for (int j=0; j< errors.length; j++) {
				        System.out.println("ERROR creating record: " + errors[j].getMessage());
				          }
				        }    
				      }
			  }catch(Exception excp){excp.printStackTrace();}
            
        }
    }
    
    /**
     * Helper method to copy from a readable channel to a writable channel,
     * using an in-memory buffer.
     */
    private void copy(ReadableByteChannel src, WritableByteChannel dest)
        throws IOException
    {
        // Use an in-memory byte buffer
        ByteBuffer buffer = ByteBuffer.allocate(8092);
        while (src.read(buffer) != -1) {
            buffer.flip();
            while(buffer.hasRemaining()) {
                dest.write(buffer);
            }
            buffer.clear();
        }
    }
    
    private void setUnpackaged(RetrieveRequest request, String releaseId) throws Exception
    {
    	System.out.println(connection);
    	
    	QueryResult  queryResults = connection.query("select Id, Body, Name from Attachment where ParentId = \'"+releaseId+"\'"
	      		+ "AND Name = \'package.xml'");
    	System.out.println("Check Result Size ::: " + queryResults.getSize());
    	Attachment attachPackage = (Attachment)queryResults.getRecords()[0];
    	
    	System.out.println("Check Path ::: " + appPath);
    	System.out.println("Check Path ::: " + System.getProperty("user.dir"));
    	File unpackedManifest = new File(appPath + "//package.xml");
    	FileOutputStream os = new FileOutputStream(unpackedManifest);
    	ByteArrayInputStream bais1 = new ByteArrayInputStream(attachPackage.getBody());
    	
    	try {
            ReadableByteChannel src = Channels.newChannel(bais1);
            FileChannel dest = os.getChannel();
            copy(src, dest);
            
            
        } finally {
            os.close();
        }
    	
        // Edit the path, if necessary, if your package.xml file is located elsewhere
        //File unpackedManifest = new File(MANIFEST_FILE);
        System.out.println("Manifest file: " + unpackedManifest.getAbsolutePath());
        
        if (!unpackedManifest.exists() || !unpackedManifest.isFile())
            throw new Exception("Should provide a valid retrieve manifest " +
                    "for unpackaged content. " +
                    "Looking for " + unpackedManifest.getAbsolutePath());

        // Note that we populate the _package object by parsing a manifest file here.
        // You could populate the _package based on any source for your
        // particular application.
        com.sforce.soap.metadata.Package p = parsePackage(unpackedManifest);
        request.setUnpackaged(p);
    }

    private com.sforce.soap.metadata.Package parsePackage(File file) throws Exception {
        try {
            InputStream is = new FileInputStream(file);
            List<PackageTypeMembers> pd = new ArrayList<PackageTypeMembers>();
            DocumentBuilder db =
                DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Element d = db.parse(is).getDocumentElement();
            for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
                if (c instanceof Element) {
                    Element ce = (Element)c;
                    //
                    NodeList namee = ce.getElementsByTagName("name");
                    if (namee.getLength() == 0) {
                        // not
                        continue;
                    }
                    String name = namee.item(0).getTextContent();
                    NodeList m = ce.getElementsByTagName("members");
                    List<String> members = new ArrayList<String>();
                    for (int i = 0; i < m.getLength(); i++) {
                        Node mm = m.item(i);
                        members.add(mm.getTextContent());
                    }
                    PackageTypeMembers pdi = new PackageTypeMembers();
                    pdi.setName(name);
                    pdi.setMembers(members.toArray(new String[members.size()]));
                    pd.add(pdi);
                }
            }
            com.sforce.soap.metadata.Package r = new com.sforce.soap.metadata.Package();
            r.setTypes(pd.toArray(new PackageTypeMembers[pd.size()]));
            r.setVersion(API_VERSION + "");
            return r;
        } catch (ParserConfigurationException pce) {
            throw new Exception("Cannot create XML parser", pce);
        } catch (IOException ioe) {
            throw new Exception(ioe);
        } catch (SAXException se) {
            throw new Exception(se);
        }
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
    
    //The sample client application retrieves the user's login credentials.
    // Helper function for retrieving user input from the console
    String getUserInput(String prompt) {
        System.out.print(prompt);
        try {
            return rdr.readLine();
        }
        catch (IOException ex) {
            return null;
        }
    }

}
