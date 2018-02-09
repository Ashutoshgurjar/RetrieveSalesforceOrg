package org.kohsuke.github;
import java.io.File;
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
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.metadata.MetadataConnection;

public class MetaDataUtility {
	
	//It will contain the failed package file name
			public static Set<String> failedPackageFile = new HashSet<String>();
			
	//This set will hold the package file name send by the thread
		public static Set<String> profilePackageFile = new HashSet<String>();
		
		public static Set<String> permissionSetPackageFile = new HashSet<String>();
		
		public static Set<String> otherSetPackageFile = new HashSet<String>();
		
		//This map contain the package file nname with all the component in that package file
		public static Map<String, List<String>> mapMetaDataWithRespectivePackageFileName;
	
	public static void threadMaker() {
		
		
		/*for(String packageFileName : setPackageFileName) {
			if(packageFileName.contains("Profile"))
				profilePackageFile.add(packageFileName);
			else if(packageFileName.contains("permission"))
				permissionSetPackageFile.add(packageFileName);
			else
				otherSetPackageFile.add(packageFileName);
		}*/
		
		ThreadGroup tgPermission = new ThreadGroup("permissionSetThread");
		///ThreadGroup tgProfile = new ThreadGroup("profile Thread");
		//ThreadGroup tgOther = new ThreadGroup("other metadata Thread");
		
		System.out.println("Constant.setAllPackageFileName.size()---------"+Utility.setAllPackageFileName.size());
		for(String packageFileName : Utility.setAllPackageFileName) {
			
			/*if(packageFileName) {
				for(String metdat: Constant.setPermissionOfMetadta) {
					
				}
			}*/
			
			//System.out.println("packageFileName--------------"+packageFileName);
			Thread thread1 = new Thread(tgPermission,new OrgBackupController(tgPermission, packageFileName+".xml", OrgBackupController.metadataConnection));
			thread1.start();
			
			
			//System.out.println("Thread.activeCount()"+Thread.activeCount());
			System.out.println("Thread.activeCount()@@@@@@@@@@all process running"+tgPermission.activeCount());
						
		}
		
		//thread1.activeCount();
		if(tgPermission.activeCount() > 0) {
			Integer count = tgPermission.activeCount();
			while(tgPermission.activeCount() != 0) {
				
				if(count != tgPermission.activeCount()) {
					System.out.println("Thread.activeCount()@@@@@@@@@@all process complete"+tgPermission.activeCount());
					count = tgPermission.activeCount();
				}
					
			}
			System.out.println("Thread.activeCount()@@@@@@@@@@########all process complete"+tgPermission.activeCount());
			//Here we are filling the failed package file name so we can again iterate on them
			Utility.setAllPackageFileName = failedPackageFile;
			threadMaker();
				
		}
		
		/*
		Thread thread1 = new Thread(new OrgBackupController(profilePackageFile, OrgBackupController.metadataConnection));
		thread1.start();
		
		Thread thread2 = new Thread(new OrgBackupController(permissionSetPackageFile, OrgBackupController.metadataConnection));
		thread2.start();
		*/
	
			 //Thread thread1 = new Thread(new OrgBackupController("package1.xml", OrgBackupController.metadataConnection));

		        //Thread thread2 = new Thread(new OrgBackupController("package3.xml", OrgBackupController.metadataConnection));

		       // thread1.start();

		     //   thread2.start();
		
		 
	}
	public static Map<String, List<String>> mapMetaDataWithRespectivePackage;
	//Hold the Application path application wide
	public static String appPath;
	
	//This set will hold all the package file name
	public static Set<String> setPackageFileName;
	
	
	
	public void retrieveZip(EnterpriseConnection connection, String releaseName, List<String> packagesPath){
		
		//QueryResult  queryResults = connection.query("select Id from SFDC_Release__c where Name = \'"+RELEASE+"\'");
  		//String relaeseId = ""+queryResults.getRecords()[0].getId();
	}
	
	public void readMetadata(MetadataConnection conn){
		
		
	}
}
