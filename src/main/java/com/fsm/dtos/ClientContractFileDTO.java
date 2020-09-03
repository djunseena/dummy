package com.fsm.dtos;

import java.sql.Timestamp;

import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Data
@NoArgsConstructor
public class ClientContractFileDTO {
	
	private long contractId;	
	
	private ClientCompanyDTO companyId;	
	
	private String contractFileName;
	
	private String contractFilePath;
	
	private long createdBy;	
	
	private Timestamp createdOn;	
	
	private long lastModifiedBy;	
	
	private Timestamp lastModifiedOn;
	
	private boolean isDeleted;
}
