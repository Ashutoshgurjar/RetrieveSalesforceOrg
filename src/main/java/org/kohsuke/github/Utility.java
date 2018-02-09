package org.kohsuke.github;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Utility {
    
    public static String ApexPage = "pageAccesses";
    public static String CustomApplication = "applicationVisibilities";
    public static String CustomObject = "fieldPermissions";
    public static String CustomTab = "tabVisibilities";
    public static String RecordType = "recordTypeVisibilities";
    public static String ApexClass = "classAccesses";
    
    public static String ppackagName = "";
    
    
    //This set will hold all the package file name
    public static Set<String> setPackageFileName;
    
    public static String ConnectedApp = null;
    public static String AppMenu = null;
    public static String Layout = null;
    static String[] words = {"ReportType", "Role", "SamlSSoConfig", "SharingRules", "SharingSet", "SiteDotCom",
        "Skill", "StaticResource", "Workflow", "Settings","ApexComponent", "ApexTrigger", "AppMenu", "ApprovalProcess", "AssignmentRules", "AutoResponseRules",
        "Certificate", "Community", "CustomLabels", "CustomPageLink","CustomPermission", "CustomSite","DelegateGroup",
        "FlexiPage","Flow","Group","HomePageComponent","HomePageLayout","InstalledPackage","LetterHead","LiveChatAgentConfig","LiveChatButton",
        "LiveChatDeployment","Portal","Queue","QuickAction","RemoteSiteSetting","ReportType","EmailTemplate"/*,"Profile","PermissionSet"*/};
            public static Set<String> setAllComponenet = new HashSet<String>(Arrays.asList(words));
            
            public static Set<String> setAllPackageFileName = new HashSet<String>();
    
    
    static String[] permissionOfMetadtaType = {"ApexPage", "ConnectedApp", "CustomApplication", "CustomObject", "CustomTab"
        ,"Layout", "RecordType","AppMenu","ApexClass"};
           // public static Set<String> setPermissionOfMetadta= new HashSet<String>(Arrays.asList(permissionOfMetadtaType));
            
            static String[] profilePermissionOfMetadtaType = {"ApexPage", "ConnectedApp", "CustomApplication", "CustomObject", "CustomTab"
                    ,"Layout", "RecordType","AppMenu","ApexClass"};
                      //  public static Set<String> setProfilePermissionOfMetadta= new HashSet<String>(Arrays.asList(profilePermissionOfMetadtaType));
                        
                        public static Map<String, List<String>> mapMetaDataWithRespectivePackage = new HashMap<String, List<String>>();
                       // mapMetaDataWithRespectivePackage.put("Profile",Arrays.asList(profilePermissionOfMetadtaType));
    
    public static List<String> prepareXmlFiles(Map<String, List<MetadataWrapper>> mapMetadataComponents) throws Exception {
        
        setPackageFileName = new HashSet<String>();
        mapMetaDataWithRespectivePackage.put("Profile",Arrays.asList(profilePermissionOfMetadtaType));
        mapMetaDataWithRespectivePackage.put("PermissionSet",Arrays.asList(permissionOfMetadtaType));
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        
        if(mapMetadataComponents != null && mapMetadataComponents.size() > 0 ){ 
            
            for(String ComponentName : setAllComponenet) {
                
                try {
                    //System.out.println("setAllComponenet -------"+setAllComponenet);
                    //List to hold the Package File Path which will be used in Metadata Retrieval command
                    List<String> packageFilesPath = new ArrayList<String>();
                    //Check APP Path
                    System.out.println("Check App Path ::: " + MetaDataUtility.appPath);
                    
                    if(ComponentName != null && ComponentName != "" ){
                        
                        
                        
                        if(ComponentName == "PermissionSet" || ComponentName == "Profile") {
                            profilePermissionSetPackageMaker(ComponentName,mapMetadataComponents);
                        }
                        
                        else if(ComponentName == "StaticResource") {
                        	staticResourcePackageMaker(ComponentName,mapMetadataComponents);
                        }
                        
                        else {
                        	
                        	//This set contain all package file name
                        	setAllPackageFileName.add(ComponentName);
                        	
                            System.out.println("packageName######"+ComponentName);
                            // root elements
                            Document doc = docBuilder.newDocument();
                            Element rootElement = doc.createElement("Package");
                            doc.appendChild(rootElement);
                            
                            Attr attr = doc.createAttribute("xmlns");
                            attr.setValue("http://soap.sforce.com/2006/04/metadata");
                            rootElement.setAttributeNode(attr);
                            
                            System.out.println("Package Name ::: " + ComponentName);
                            
                            
                            Element types = doc.createElement("types");
                            
                            System.out.println("Metadata Type ::: " + ComponentName);
                            
                            //Loop over Sub details of the Metadata Type(Get List of Metadata type e.g. ApexClass)
                            for(MetadataWrapper metaData : mapMetadataComponents.get(ComponentName)){
                                
                                //System.out.println("Metadata Name ::: " + metaData.fullName);
                                
                                Element members = doc.createElement("members");
                                members.appendChild(doc.createTextNode(metaData.fullName));
                                types.appendChild(members);
                                //System.out.println("members #######-----------"+members);
                                //System.out.println("metaData.fullName #######-----------"+metaData.fullName);
                            }
                            
                            //Add type of Metadata Type
                            Element name = doc.createElement("name");
                            name.appendChild(doc.createTextNode(ComponentName));
                            types.appendChild(name);
                            rootElement.appendChild(types);
                            
                            
                            Element version = doc.createElement("version");
                            version.appendChild(doc.createTextNode("41.0"));
                            rootElement.appendChild(version);
                            
                            // write the content into xml file
                            TransformerFactory transformerFactory = TransformerFactory.newInstance();
                            Transformer transformer = transformerFactory.newTransformer();
                            DOMSource source = new DOMSource(doc);
                            
                            
                            
                            StreamResult result = new StreamResult(new File(MetaDataUtility.appPath + ComponentName+".xml"));
                            
                            //Add package file  name into the set
                            setPackageFileName.add(ComponentName);
                            
                            //StreamResult result = new StreamResult(System.out);
                            
                            //Indent the output
                            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                            
                            transformer.transform(source, result);
                            System.out.println("File saved!");
                        }
                        
                    }
                }
                
                catch(Exception e) {
                    
                }
            }
        }
        
        System.out.println("ppackagName######@@@@@@@@@@@@@@@@@@@@@@"+ppackagName);
        
        return null;
        
    }
    
    public static void profilePermissionSetPackageMaker(String metaDataName, Map<String, List<MetadataWrapper>>mapMetadataComponents) throws Exception{

    	setPackageFileName = new HashSet<String>();
		
		//List to hold the Package File Path which will be used in Metadata Retrieval command
		List<String> packageFilesPath = new ArrayList<String>();
		
		//Check APP Path
		//System.out.println("Check App Path ::: " + MetaDataUtility.appPath);
		
		if(mapMetadataComponents != null && mapMetadataComponents.size() > 0 ){
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			try {
				docBuilder = docFactory.newDocumentBuilder();
				
				
					
					//Get List of the component for each package.xml and create the xml documents respectively
					for(String metaDataType : mapMetaDataWithRespectivePackage.get(metaDataName)) {	
						
						
						if(metaDataType == "CustomObject" /*|| metaDataType == "CustomObject"/*mapMetadataComponents.get(metaDataType).size() > 2000*/)  {
							System.out.println("hi i am about to break custom object");
							breakMetadata(metaDataName, metaDataType, mapMetadataComponents);
						}
						
						//This part will excecute when metadata record are more than 2000
						else {
							System.out.println("hi i am not to break custom object");
						for(Integer i=0 ; i<8 ; i++) {
							
							Integer j = null;
							Integer k = null;
							
							Document doc = docBuilder.newDocument();
							Element rootElement = doc.createElement("Package");
							doc.appendChild(rootElement);
							
							Attr attr = doc.createAttribute("xmlns");
							attr.setValue("http://soap.sforce.com/2006/04/metadata");
							rootElement.setAttributeNode(attr);
							
						if(mapMetadataComponents.containsKey(metaDataType) && mapMetadataComponents.get(metaDataType) != null && mapMetadataComponents.get(metaDataType).size() > 0){
							
							
							
							System.out.println("Metadata Type ::: " + metaDataType);
							//Integer permissionFileSize = mapMetadataComponents.get(metaDataType).size() / 2;
							
							Integer temp = mapMetadataComponents.get(metaDataName).size()/8;
							
							k = i * temp;
							j = (i+1) * temp;
							
							if(i==7)
								j = mapMetadataComponents.get(metaDataName).size();
														
								Element types = doc.createElement("types");
								
								System.out.println("Metadata Type ::: " + metaDataType);
																							
								List<MetadataWrapper> metaData = mapMetadataComponents.get(metaDataName);
								for(Integer l = k; l<j; l++) {
									
									Element members = doc.createElement("members");
									members.appendChild(doc.createTextNode(metaData.get(l).fullName));
									types.appendChild(members);
								}
								
								//Add type of Metadata Type
								Element name = doc.createElement("name");
								name.appendChild(doc.createTextNode(metaDataName));
								types.appendChild(name);
								rootElement.appendChild(types);
								
								
								Element types1 = doc.createElement("types");
																							
								//Loop over Sub details of the Metadata Type(Get List of Metadata type e.g. ApexClass)
								for(MetadataWrapper metaData1 : mapMetadataComponents.get(metaDataType)){
									
									//System.out.println("Metadata Name ::: " + metaData.fullName);
									
									Element members = doc.createElement("members");
									members.appendChild(doc.createTextNode(metaData1.fullName));
									types1.appendChild(members);
									
								}
								
								//Add type of Metadata Type
								Element name1 = doc.createElement("name");
								name1.appendChild(doc.createTextNode(metaDataType));
								types1.appendChild(name1);
								rootElement.appendChild(types1);
								
							
						
						
						Element version = doc.createElement("version");
						version.appendChild(doc.createTextNode("41.0"));
						rootElement.appendChild(version);
						
						// write the content into xml file
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						DOMSource source = new DOMSource(doc);
						
						StreamResult result = new StreamResult(new File(MetaDataUtility.appPath + i+metaDataType+metaDataName+".xml"));
						
						setPackageFileName.add(i+metaDataType+metaDataName);
						
						//This set contain all package file name
                    	setAllPackageFileName.add(i+metaDataType+metaDataName);
						//mapMetaDataWithRespectivePackageFileName.put("permissionPackage"+i+metaDataName, mapMetaDataWithRespectivePackage.get(metaDataName));
						
						
						//StreamResult result = new StreamResult(System.out);
						
						//Indent the output
						transformer.setOutputProperty(OutputKeys.INDENT, "yes");
						
						transformer.transform(source, result);
						System.out.println("File saved!");
					}
						}
					}
					
				}
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return;
    }
    
    public static void breakMetadata(String metaDataName,String metaDataType, Map<String, List<MetadataWrapper>>mapMetadataComponents) throws  Exception{
    	Integer jPackage = null;
    	Integer kPackage = null;
    	//Check APP Path
    			//System.out.println("Check App Path ::: " + MetaDataUtility.appPath);
    	for(Integer iPackage=0; iPackage<4; iPackage++) {
    		
        			//metaDataName = profile/permissionSet
        	
    		Integer temp1 = mapMetadataComponents.get(metaDataName).size()/4;
			
    		kPackage = iPackage * temp1;
			jPackage = (iPackage+1) * temp1;
			
			if(iPackage==3)
				jPackage = mapMetadataComponents.get(metaDataName).size();
			
        			if(mapMetadataComponents != null && mapMetadataComponents.size() > 0 ){
        				
        				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        				DocumentBuilder docBuilder;
        				try {
        					docBuilder = docFactory.newDocumentBuilder();
        					
        					for(Integer i=0 ; i<8 ; i++) {
        								
        								Integer j = null;
        								Integer k = null;
        								
        								Document doc = docBuilder.newDocument();
        								Element rootElement = doc.createElement("Package");
        								doc.appendChild(rootElement);
        								
        								Attr attr = doc.createAttribute("xmlns");
        								attr.setValue("http://soap.sforce.com/2006/04/metadata");
        								rootElement.setAttributeNode(attr);
        								
        							if(mapMetadataComponents.containsKey(metaDataType) && mapMetadataComponents.get(metaDataType) != null && mapMetadataComponents.get(metaDataType).size() > 0){
        								
        								
        								
        								System.out.println("Metadata Type ::: " + metaDataType);
        								//Integer permissionFileSize = mapMetadataComponents.get(metaDataType).size() / 2;
        								
        								Integer temp = mapMetadataComponents.get(metaDataName).size()/8;
        								
        								k = i * temp;
        								j = (i+1) * temp;
        								
        								if(i==7)
        									j = mapMetadataComponents.get(metaDataName).size();
        								
        								
        									Element types = doc.createElement("types");
        									        																								
        									List<MetadataWrapper> metaData = mapMetadataComponents.get(metaDataName);
        									for(Integer l = k; l<j; l++) {
        										
        										Element members = doc.createElement("members");
        										members.appendChild(doc.createTextNode(metaData.get(l).fullName));
        										types.appendChild(members);
        									}
        									
        									//Add type of Metadata Type
        									Element name = doc.createElement("name");
        									name.appendChild(doc.createTextNode(metaDataName));
        									types.appendChild(name);
        									rootElement.appendChild(types);
        									
        									
        									Element types1 = doc.createElement("types");
        									
        									
        									
        									
        									
        									Element types2 = doc.createElement("types");
        									
        									System.out.println("Metadata Type ::: " + metaDataType);
        																								
        									List<MetadataWrapper> metaDataOfObj = mapMetadataComponents.get(metaDataType);
        									for(Integer l = kPackage; l<jPackage; l++) {
        										
        										Element members = doc.createElement("members");
        										members.appendChild(doc.createTextNode(metaDataOfObj.get(l).fullName));
        										types2.appendChild(members);
        									}
        									
        									//Add type of Metadata Type
        									Element name2 = doc.createElement("name");
        									name2.appendChild(doc.createTextNode(metaDataType));
        									types2.appendChild(name2);
        									rootElement.appendChild(types2);
        							
        							
        							Element version = doc.createElement("version");
        							version.appendChild(doc.createTextNode("41.0"));
        							rootElement.appendChild(version);
        							
        							// write the content into xml file
        							TransformerFactory transformerFactory = TransformerFactory.newInstance();
        							Transformer transformer = transformerFactory.newTransformer();
        							DOMSource source = new DOMSource(doc);
        							
        							StreamResult result = new StreamResult(new File(MetaDataUtility.appPath + i+iPackage+metaDataType+metaDataName+".xml"));
        							
        							setPackageFileName.add(i+iPackage+metaDataType+metaDataName);
        							
        							//This set contain all package file name
        	                    	setAllPackageFileName.add(i+""+iPackage+metaDataType+metaDataName);
        							//mapMetaDataWithRespectivePackageFileName.put("permissionPackage"+i+metaDataName, mapMetaDataWithRespectivePackage.get(metaDataName));
        							
        	                    	ppackagName += i+""+iPackage+metaDataType+metaDataName;
        							//StreamResult result = new StreamResult(System.out);
        							
        							//Indent the output
        							transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        							
        							transformer.transform(source, result);
        							System.out.println("File saved!");
        						}
        							}
        						
        						
        					
        				} catch (ParserConfigurationException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
        				
        			}
    	}
    	
    }
    
    
    // deleting all the file in unpackage
    public static void fileDeleter() {
		/*String str = "abc/bcd/egf/ghi";
		System.out.println("last / index"+str.lastIndexOf("/"));
		System.out.println("substring "+str.substring(str.lastIndexOf("/")+1));*/
		String zipFilePath = MetaDataUtility.appPath+"\\unpackaged" /*"C:\\Users\\sf\\eclipse-workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp1\\wtpwebapps\\ZipRetrieveAutomation\\unpackaged"*/;
		File fle = new File(zipFilePath);
		
		
		
		File directory = new File(zipFilePath);

    	//make sure directory exists
    	if(!directory.exists()){

           //System.out.println("Directory does not exist.");
           return;

        }else{

           try{

               delete(directory);

           }catch(IOException e){
               e.printStackTrace();
               System.exit(0);
           }
        }

    	System.out.println("Done");
		
	}
    
    public static void staticResourcePackageMaker(String metaDataName, Map<String, List<MetadataWrapper>>mapMetadataComponents) throws Exception {
    	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        
        Integer j = null;
        Integer k = null;
        
        for(Integer i = 0; i<4; i++) {
        	
        	Integer temp = mapMetadataComponents.get(metaDataName).size()/4;
			
			k = i * temp;
			j = (i+1) * temp;
			
			if(i==3)
				j = mapMetadataComponents.get(metaDataName).size();
			
        	//This set contain all package file name
        	setAllPackageFileName.add(i+metaDataName);
        	
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Package");
            doc.appendChild(rootElement);
            
            Attr attr = doc.createAttribute("xmlns");
            attr.setValue("http://soap.sforce.com/2006/04/metadata");
            rootElement.setAttributeNode(attr);
            
            System.out.println("Package Name ::: " + metaDataName);
            
            
            Element types = doc.createElement("types");
            
            System.out.println("Metadata Type ::: " + metaDataName);
            
            List<MetadataWrapper> metaDataOfObj = mapMetadataComponents.get(metaDataName);
			for(Integer l = k; l<j; l++) {
				
				Element members = doc.createElement("members");
				members.appendChild(doc.createTextNode(metaDataOfObj.get(l).fullName));
				types.appendChild(members);
			}
            
            //Add type of Metadata Type
            Element name = doc.createElement("name");
            name.appendChild(doc.createTextNode(metaDataName));
            types.appendChild(name);
            rootElement.appendChild(types);
            
            
            Element version = doc.createElement("version");
            version.appendChild(doc.createTextNode("41.0"));
            rootElement.appendChild(version);
            
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            
            
            
            StreamResult result = new StreamResult(new File(MetaDataUtility.appPath + i+metaDataName+".xml"));
            
            //Add package file  name into the set
            setPackageFileName.add(i+metaDataName);
            
            //StreamResult result = new StreamResult(System.out);
            
            //Indent the output
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            
            transformer.transform(source, result);
            System.out.println("File saved!");
        }
      
    }

    public static void delete(File file)
    	throws IOException{

    	if(file.isDirectory()){

    		//directory is empty, then delete it
    		if(file.list().length==0){

    		   file.delete();
    		   //System.out.println("Directory is deleted : "+ file.getAbsolutePath());

    		}else{

    		   //list all the directory contents
        	   String files[] = file.list();

        	   for (String temp : files) {
        	      //construct the file structure
        	      File fileDelete = new File(file, temp);

        	      //recursive delete
        	     delete(fileDelete);
        	   }

        	   //check the directory again, if empty then delete it
        	   if(file.list().length==0){
           	     file.delete();
        	     //System.out.println("Directory is deleted : "+ file.getAbsolutePath());
        	   }
    		}

    	}else{
    		//if file, then delete it
    		file.delete();
    		//System.out.println("File is deleted : " + file.getAbsolutePath());
    	}
    }
    
    
}
