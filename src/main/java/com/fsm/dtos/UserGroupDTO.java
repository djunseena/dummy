package com.fsm.dtos;

import java.sql.Timestamp;

import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Data
@NoArgsConstructor
public class UserGroupDTO {
	
	private long userGroupId;
	
	private String userGroupName;
	
	private String userGroupDesc;
	
	private long createdBy;
	
	private Timestamp createdOn;
	
	private long lastModifiedBy;
	
	private Timestamp lastModifiedOn;
	
	private boolean isDeleted;

}
