package org.kohsuke.github;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.metadata.AsyncResult;
import com.sforce.soap.metadata.DescribeMetadataObject;
import com.sforce.soap.metadata.DescribeMetadataResult;
import com.sforce.soap.metadata.FileProperties;
import com.sforce.soap.metadata.ListMetadataQuery;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.metadata.PackageTypeMembers;
import com.sforce.soap.metadata.RetrieveMessage;
import com.sforce.soap.metadata.RetrieveRequest;
import com.sforce.soap.metadata.RetrieveResult;
import com.sforce.soap.metadata.RetrieveStatus;
import com.sforce.ws.ConnectionException;

/** 
* Description   :   Controller class to handle all Org backup feature related requests and pass them to the Service classes and various other utility classes and subclasses
*
* Created By    :   Rajeev Jain(Simplyforce)
*
* Created Date  :   01/05/2018
*
* Version       :   V1.0 Created
**/   
public class OrgBackupController extends Thread {
	
	public String packagFilleNameFromThread;
	
	public ThreadGroup tgPermission;
	
	//Hold Metadata Connection Details with it
	public static MetadataConnection metadataConnection;
	
	public EnterpriseConnection connection;
	
	//This variable is used to for getting information that when overide and when append
	public static Integer profileAppendVar = 0;
	public static Integer permissionSetAppendVar = 0;
	
	//Constants
	final static Double API_VERSION = 39.0;
	
	final static String DEFAULT_LOGIN_URL = "https://test.salesforce.com/services/Soap/c/";
	
	public void finalize(){
		//System.out.println("object is garbage collected");
		}  
	
	public static Set<String> setAllFileNameWithPath = new HashSet<String>();
	//Set to hold the Apex Component which must be retrieved through Permissions(Profile/Permission Set)
	Set<String> profileBasedComp = new HashSet<String>();
	Set<String> permissionSetBasedComp = new HashSet<String>();
	
	//Basic Permission Components Set
	Set<String> basicSet = new HashSet<String>();
	
	
	
	//Folder Based Components(All Parent Level Component Like Report, Document, Dashboard and EmailTemplate)
	Set<String> folderBasedComp = new HashSet<String>();
	
	//Folder Name based on Component Types
	Set<String> folderNames = new HashSet<String>();
	
	//This set will hold the package file name send by the thread
	public Set<String> profilePermissionPackageFile;
	
	
	
	//Map to Hold the Describe Metadata Details corrosponding to each metadata type(Full Org Metadata with Parent Level Component, like CustomObject)
	//Custom Object Implicitly retrieve it's child component subsequently
	Map<String, DescribeMetadataObject> mapDescribeMetada = new HashMap<String, DescribeMetadataObject>();
	
	//Set to Hold all other Child based Metadata which automatically retrieved through their Parent Name only
	Set<String> setDescribeDataChildLevel = new HashSet<String>();
	
	
	//Prepare the Map to Hold Components Corrosponding to each metadata
	Map<String, List<MetadataWrapper>> mapMetadataComponents = new HashMap<String, List<MetadataWrapper>>();
	
	//Map to hold the mapping for Item
	Map<String, String> mapFolderMapping = new HashMap<String, String>();
	
	//Map to hold Metadata Type corrosponding to Package.xml file name with Serial number
	//This map will play a big role to chuck the metadata Retrieval for various metadata components
	//Like Package1.xml => ApexClass, ApexPage, ApexTrigger etc.
	Map<String, Set<String>> mapMetaDataWithRespectivePackage = new HashMap<String, Set<String>>();
	
	//List to Hold the Package.xml paths
	List<String> packageFilePaths;
	
	public void run()

    {

		/*if(profilePermissionPackageFile!= null && profilePermissionPackageFile.size() > 0) {
			
			for(String packageName : profilePermissionPackageFile) {
				System.out.println("###################################packageName"+packageName);
				retrieveZipFiles(packageName);
			}
		}*/
		//else {
			//System.out.println("###################################packagFilleNameFromThread"+packagFilleNameFromThread);
			retrieveZipFiles(packagFilleNameFromThread);
		//}

    }
	
	
	public OrgBackupController(ThreadGroup tgPermission, String newPackageFileName, MetadataConnection metadataConnection) {
		
	    this.tgPermission = tgPermission;
		//System.out.println("In constructor #########"+newPackageFileName);
		packagFilleNameFromThread = newPackageFileName;
	}
	
	public OrgBackupController(Set<String> newPackageFileName, MetadataConnection metadataConnection) {
		this.metadataConnection = metadataConnection;
		profilePermissionPackageFile = new HashSet<String>();
		
		profilePermissionPackageFile.addAll(newPackageFileName);
		
	}
	
	//Class Constructor
	public OrgBackupController(String username, String password, String Env){
		
		System.out.println("Org Backup Controller called ::: ");
		
		String loginUrl = DEFAULT_LOGIN_URL;
		
		//Check for the Env
		if(Env == "production")
			loginUrl += API_VERSION;
		else
			loginUrl = DEFAULT_LOGIN_URL + API_VERSION;
		
		try{
			
			SFLoginUtility login = new SFLoginUtility(username, password, loginUrl);
			
			//Getting Metadata Conncection with Proper Login
			this.metadataConnection = login.metadataConnection;
			
			//Create connection to Salesforce for API requests
			this.connection = login.connection;
			
		}catch(ConnectionException ex){
			ex.printStackTrace();
		}
		
		basicSet.add("ApexClass");
		basicSet.add("ApexPage");
		basicSet.add("CustomObject");
		basicSet.add("Layout");
		basicSet.add("CustomTab");
		basicSet.add("CustomApplication");
		basicSet.add("CustomPermission");
		basicSet.add("AppMenu");
		
		//Populate Permission Based Set With It's Associated Components
		profileBasedComp.addAll(basicSet);
		profileBasedComp.add("Profile");
		
		permissionSetBasedComp.addAll(basicSet);
		permissionSetBasedComp.add("PermissionSet");
		
		folderBasedComp.add("Report");
		folderBasedComp.add("Document");
		folderBasedComp.add("Dashboard");
		folderBasedComp.add("EmailTemplate");
		
		folderNames.add("ReportFolder");
		folderNames.add("DocumentFolder");
		folderNames.add("EmailFolder");
		folderNames.add("DashboardFolder");
		
		mapFolderMapping.put("Report", "ReportFolder");
		mapFolderMapping.put("Document", "DocumentFolder");
		mapFolderMapping.put("Dashboard", "DashboardFolder");
		mapFolderMapping.put("EmailTemplate", "EmailFolder");
		
		describeMetadata();
		listMetadata();
		
		
		//This method will clear the unpackaged folder
		Utility.fileDeleter();
		//Prepare Package Files annd save them
		preparePackageFiles();
		//multiplePackageFileHandler();
		//retrieveZipFiles();
	}
	
	 public void describeMetadata() {
		  try {
			  
		    double apiVersion = 39.0;
		    
		    // Assuming that the SOAP binding has already been established.
		    DescribeMetadataResult res = metadataConnection.describeMetadata(apiVersion);
		    
		    if (res != null && res.getMetadataObjects().length > 0) {
		      for (DescribeMetadataObject obj : res.getMetadataObjects()) {
		    	  //System.out.println("obj.getChildXmlNames()"+obj.getChildXmlNames());
		    	  if(obj.getChildXmlNames().length > 1 ){
		    		  for(String str : obj.getChildXmlNames()){
		    			  setDescribeDataChildLevel.add(str);
		    		  }
		    	  }
		    	  if(!mapDescribeMetada.containsKey(obj.getXmlName())){
		    		  //System.out.println("obj@@@@@"+obj.getXmlName());
		    		  if(!folderBasedComp.contains(obj.getXmlName()))
		    			  mapDescribeMetada.put(obj.getXmlName(), obj);
		    	  }
		      }
		    } else {
		      
		    }
		    
		  } catch (ConnectionException ce) {
		    ce.printStackTrace();
		  }
	 }
	 
	 public void listMetadata() {
	 	
		 try {
			 
			 System.out.println("List Metadata Called ::: ");
			 
			 Integer processingCounter = 1;
					 
			 //Loop over various maps to List out the Components corrosponding to each metadata Type(Like classes corrosponding to ApexClass Metadata Types etc.)
			 for(String str : mapDescribeMetada.keySet()){
				 
				 listMetadataProcessing(str, null);
				 System.out.println("item processed counter " + processingCounter);
				 processingCounter++;
			 }
			 
			 //System.out.println("Child Metadata Processing has started ::: ");
			 
			 //Get list of child metadata
			 for(String str : setDescribeDataChildLevel){
				
				 System.out.println("item processed counter " + processingCounter + "Child Metadat Name ::: " + str);
				 
				 listMetadataProcessing(str, null);
				 
				 processingCounter++;
			 }
			 			 
		 }catch(ConnectionException ex){
			 ex.printStackTrace(); 
		 }
	 }
	 
	 private void listMetadataProcessing(String metaDataName, String folderName) throws ConnectionException{
		 
		//Fill the map with key and initialize the list
		 if(!mapMetadataComponents.containsKey(metaDataName)){
			 mapMetadataComponents.put(metaDataName, new ArrayList<MetadataWrapper>()); 
		 }
		 ListMetadataQuery query = new ListMetadataQuery();
		 query.setType(metaDataName);
		 
		 if(folderName != null)
			query.setFolder(folderName); 
		 
		 FileProperties[] lmr = metadataConnection.listMetadata(new ListMetadataQuery[] {query}, API_VERSION);
		
		 
		 //Loop over file Properties and fill it in the Map
		 if (lmr != null) {
			 
			 System.out.println("File Properties are Processing ::: ");
			 
			 for (FileProperties n : lmr) {
				 
				 //Convert Calendar Date to Readbale format
				 MetadataWrapper wrap = new MetadataWrapper(n.getCreatedById(), n.getCreatedByName(), n.getCreatedDate().getTime(), n.getFileName(), 
						 n.getFullName(), n.getId(), n.getLastModifiedById(), n.getLastModifiedByName(), n.getLastModifiedDate().getTime(), n.getNamespacePrefix(), n.getType());
				 
				 //Add it to the Map
				 mapMetadataComponents.get(metaDataName).add(wrap);
			 }
		 }
		 
	 }
	 
	 public void preparePackageFiles(){
		 
		 try{
			 //packageFilePaths = MetaDataUtility.preparePackageXML(mapMetadataComponents);
			 packageFilePaths = Utility.prepareXmlFiles(mapMetadataComponents);
			 File file = new File(MetaDataUtility.appPath+File.separator+"unpackaged");
			 if(file.exists())
				 file.delete();
			 MetaDataUtility.threadMaker();
			 
			
			 
		 }catch(Exception e){
			 System.out.println("Error thrown in Creating the package files ::: " + e.getStackTrace());
			 System.out.println("Error thrown in Creating the package files ::: " + e.getMessage());
		 }
	 }
	 
	 
	 public void retrieveZipFiles(String packageFileName){
		 
		 RetrieveRequest retrieveRequest = new RetrieveRequest();
	        // The version in package.xml overrides the version in RetrieveRequest
	        retrieveRequest.setApiVersion(API_VERSION);
	        
	        File unpackedManifest = new File(MetaDataUtility.appPath + packageFileName);
	        System.out.println("packageFileName-----------"+packageFileName);
	        
	     
	        
	        
		 try{
			// Edit the path, if necessary, if your package.xml file is located elsewhere
		        //File unpackedManifest = new File(MANIFEST_FILE);
		       // System.out.println("Manifest file: " + unpackedManifest.getAbsolutePath());
		        
		        if (!unpackedManifest.exists() || !unpackedManifest.isFile())
		            throw new Exception("Should provide a valid retrieve manifest " +
		                    "for unpackaged content. " +
		                    "Looking for " + unpackedManifest.getAbsolutePath());

		        // Note that we populate the _package object by parsing a manifest file here.
		        // You could populate the _package based on any source for your
		        // particular application.
		        //System.out.println("stage1");
		        com.sforce.soap.metadata.Package p = parsePackage(unpackedManifest);
		        //System.out.println("stage2");
		        retrieveRequest.setUnpackaged(p);
		        
		     // Start the retrieve operation
		        AsyncResult asyncResult = metadataConnection.retrieve(retrieveRequest);
		        //System.out.println("stage4");
		        //System.out.println("asyncResult.getMessage()"+asyncResult.getMessage());
		        
		        String asyncResultId = asyncResult.getId();
		        
		        // Wait for the retrieve to complete
		        int poll = 0;
		        long waitTimeMilliSecs = 1000;
		        RetrieveResult result = null;
		        do {
		            Thread.sleep(waitTimeMilliSecs);
		            // Double the wait time for the next iteration
		            waitTimeMilliSecs *= 2;
		            if (poll++ > 50) {
		                throw new Exception("Request timed out.  If this is a large set " +
		                "of metadata components, check that the time allowed " +
		                "by MAX_NUM_POLL_REQUESTS is sufficient.");
		            }
		              
		            result = metadataConnection.checkRetrieveStatus(
		                    asyncResultId, true);
		            //System.out.println("result.getZipFile().length"+result.getZipFile().length);
		            //System.out.println("package Fiel Name" +packageFileName);
		            //System.out.println("Retrieve Status: result.getStatus()" + result.getStatus());
		            //System.out.println("Retrieve Status: result.getErrorMessage()" + result.getErrorMessage());
		            //System.out.println("Retrieve Status: result.getDone()" + result.getDone());
		           // System.out.println("Retrieve Status: result.getSuccess()" + result.getSuccess());
		            //System.out.println("Retrieve Status: result.isSuccess()" + result.isSuccess());
		            //System.out.println("Retrieve Status: result.isDone()" + result.isDone());
		           // System.out.println("Retrieve Status: result.getFileProperties()" + result.getFileProperties());
		            //System.out.println("Retrieve Status: result.getMessages()" + result.getMessages());
		            //HttpSession session = requestS.getSession();
		            String s = ""+result.getStatus();
		            //session.setAttribute("deploymentStatus",s);
		           // System.out.println(session.getAttribute("deploymentStatus"));
		            
		            //System.out.println("The size of retrieved zip file is ---------"+result.getZipFile());
		            
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
		                //System.out.println("Retrieve warnings:\n" + buf);
		            }
		          
		            // Write the zip to the file system
		            //System.out.println("Writing results to zip file");
		            ByteArrayInputStream bais = new ByteArrayInputStream(result.getZipFile());
		            
		            result = null;
		            File resultsFile = new File(MetaDataUtility.appPath + packageFileName+".zip");
		            FileOutputStream os = new FileOutputStream(resultsFile);
		            try {
		                ReadableByteChannel src = Channels.newChannel(bais);
		                FileChannel dest = os.getChannel();
		                copy(src, dest);
		                
		                System.out.println("Results written to " + resultsFile.getAbsolutePath());
		            } finally {
		                os.close();
		            }
		            
		            
		            //unzip(MetaDataUtility.appPath+packageFileName+".zip",MetaDataUtility.appPath);
		            
		            if(profileAppendVar == 1)
		            	profileAppendVar = 0;
		            if(permissionSetAppendVar == 2)
		            	permissionSetAppendVar = 0;
		            
		 }
		 }catch(Exception e){
			
			 MetaDataUtility.failedPackageFile.add(packageFileName);
			 System.out.println("Error thrown in Retrieving zip Files ::: "+packageFileName + e.getMessage()+e.getLocalizedMessage()+e.getCause()+e.getLocalizedMessage()+e.getStackTrace());
			 tgPermission.destroy();
		 }
	 }
	 
	 public static void unzip(String zipFilePath, String destDir) {
		 
		 System.out.println("MetaDataUtility@@@@Thread.activeCount()"+Thread.activeCount());
		 
		 Integer folderFlag = 0;
		 String fileString = "";
	       
	        FileInputStream fis;
	        //buffer for read and write data to file
	        byte[] buffer = new byte[1024];
	        try {
	            
	            fis = new FileInputStream(zipFilePath);
	            ZipInputStream zis1 = new ZipInputStream(fis);
	            ZipEntry ze1 = zis1.getNextEntry();
	            while(ze1 != null){
	                String fileName = ze1.getName();
	                //System.out.println("stage3"+ze1.getName());
	                File newFile = null;
	                
	                
	                
	                File readFile = new File(destDir + File.separator + fileName);
	               // if(setAllFileNameWithPath != null && setAllFileNameWithPath.size() > 0 && setAllFileNameWithPath.contains(fileName)/*(profileAppendVar == 1 && fileName.contains(".profile")) || (permissionSetAppendVar > 0 && fileName.contains(".permissionset"))*/) {
	                if(readFile.exists() && (fileName.contains(".permissionset") || fileName.contains(".profile"))) {	
	                	
	                	// This will reference one line at a time
		                String line = null;
		                String s1 = "";
		                String s2 = "";
		                
		             // FileReader reads text files in the default encoding.
		                FileReader fileReader = 
		                    new FileReader(destDir + File.separator + fileName);
		                // Always wrap FileReader in BufferedReader.
		                BufferedReader bufferedReader = 
		                    new BufferedReader(fileReader);

		                while((line = bufferedReader.readLine()) != null) {
		                	s1 += line+"\n";
		                    //System.out.println(line);
		                }   
		                //System.out.println("s1 @@1"+s1);
		                // Always close files.
		                bufferedReader.close();
		                
		                FileOutputStream fos = new FileOutputStream(destDir + File.separator + fileName);
		               // System.out.println("s1 ##3");
		                int len;
		                while ((len = zis1.read(buffer)) > 0) {
		                	s2 = buffer+"\n";
		                fos.write(buffer, 0, len);
		                }
		                
		                fos.close();
		                
		                //close this ZipEntry
		                zis1.closeEntry();
		              
		                ze1 = zis1.getNextEntry();
		              
		             // FileReader reads text files in the default encoding.
		                FileReader fileReader1 = 
		                    new FileReader(destDir + File.separator + fileName);

		               
		                // Always wrap FileReader in BufferedReader.
		                BufferedReader bufferedReader1 = 
		                    new BufferedReader(fileReader1);

		                while((line = bufferedReader1.readLine()) != null) {
		                	s2 += line+"\n";
		                    
		                }   
		                
		                // Always close files.
		                bufferedReader1.close();
		                
		                if(s1.contains("</Profile>"))
		                	s1 = s1.replace("</Profile>", "");
		                
		                if(s1.contains("</PermissionSet>"))
		                	s1 = s1.replace("</PermissionSet>", "");
		                
		                Integer abc1 = s2.indexOf("/metadata\">");
		    	        //System.out.println("abc1"+abc1);
		    	        String s3 = s2.substring(abc1+11);
		    	        
		    	        BufferedWriter out = new BufferedWriter(new FileWriter(destDir + File.separator + fileName));
		    	        out.write(s1+s3);
		    	        out.close();		                
	                }
	                
	                
	                else {
	                	
	                	//System.out.println("Hi i am in new mode");
	                	newFile = new File(destDir + File.separator + fileName);
		                 
	                	//create directories for sub directories in zip
		                new File(newFile.getParent()).mkdirs();
		                FileOutputStream fos = new FileOutputStream(newFile);
		                int len;
		                while ((len = zis1.read(buffer)) > 0) {
		                fos.write(buffer, 0, len);
		                }
		                fos.close();
		                ze1 = zis1.getNextEntry();	
	                }
	            }
	            //close last ZipEntry
	            zis1.closeEntry();
	            zis1.close();
	            fis.close();
	        } catch (IOException e) {
	        	
	        	System.out.println("Error"+e.getMessage());
	            e.printStackTrace();
	            return;
	        }
	        return;
	        
	    }
	 private static File File(String string) {
		// TODO Auto-generated method stub
		return null;
	}
	 
	 public static void cleaningFromOldRetrieveFile (String componentName, String packageFileContent, String filePath) {
		 
		 try {
				//str = str.substringbetween("<classAccess>", "</classAccess>").replace("");
				
				Integer startingIndex = packageFileContent.indexOf(componentName+">");
				
				Integer varLength  = componentName.length();
				
				Integer endingIndex = packageFileContent.indexOf("/"+componentName+">");
				
		        
				String s3 = null;
				if(startingIndex > 0 && endingIndex > 0) {
					s3 = packageFileContent.substring(startingIndex-1, endingIndex+varLength+2);
			        //System.out.println("s3#########"+s3);
			        packageFileContent = packageFileContent.replace(s3, "");
			        //str.trim();
			        if(packageFileContent != null && packageFileContent.contains("<"+componentName+">")) {

				  		 System.out.println("ready for recursion");
				  		cleaningFromOldRetrieveFile(componentName, packageFileContent, filePath);
				  	 
				  	  }
			        else {
			        	BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
				        out.write(packageFileContent);
				        out.close();
				        return;
			        }
			        
				}
				else {
					
				  		BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
				        out.write(packageFileContent);
				        out.close();
				        return;
				  	  
				}
					
			}
			catch(Exception e) {
				System.out.println("Errror-------"+e.getMessage());
			}
	   	 
	  	return;
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
}
