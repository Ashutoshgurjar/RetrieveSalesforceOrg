package org.kohsuke.github;
import java.util.Date;

/** 
* Description   :   Wrapper class to hold the component properties with it. Like Apex class - Last Modified Date, LastModifiedBy, API Name. 
* 					These details return as Array of File Properties when ListMetadata command run for particual component type.
*
* Created By    :   Rajeev Jain(Simplyforce)
*
* Created Date  :   01/05/2018
*
* Version       :   V1.0 Created
**/   
public class MetadataWrapper {
	
	//Wrapper Properties
	public String createdById;
	public String createdByName;
	public Date createdDate;
	public String fileName;
	public String fullName;
	public String id;
	public String lastModifiedById;
	public String lastModifiedByName;
	public Date lastModifiedDate;
	public String namespacePrefix;
	public String type;
	
	//Class Constructor
	public MetadataWrapper(String createdById, String createdByName, Date createdDate, String fileName, String fullName, String id,
			String lastModifiedById, String lastModifiedByName, Date lastModifiedDate, String namespacePrefix, String type){
		
		this.createdById = createdById;
		this.createdByName = createdByName;
		this.createdDate = createdDate;
		this.fileName = fileName;
		this.fullName = fullName;
		this.id = id;
		this.lastModifiedById = lastModifiedById;
		this.lastModifiedById = lastModifiedById;
		this.lastModifiedDate = lastModifiedDate;
		this.namespacePrefix = namespacePrefix;
		this.type = type;
	}
}
