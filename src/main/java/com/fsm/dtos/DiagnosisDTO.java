package com.fsm.dtos;
import java.sql.Timestamp;

import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Data
@NoArgsConstructor
public class DiagnosisDTO {

	private long diagnosisId;
	
	private JobCategoryDTO jobCategoryId;
	
	private String diagnosisDesc;
	
	private long createdBy;
	
	private Timestamp createdOn;
	
	private long lastModifiedBy;
	
	private Timestamp lastModifiedOn;
	
	private boolean isDeleted;
}
