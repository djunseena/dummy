package com.fsm.dtos;

import java.sql.Timestamp;

import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Data
@NoArgsConstructor
public class SLADTO {

	private long slaId;

	private WorkingTimeDTO wTimeId;

	private SLATypeDTO slaTypeId;

	private int slaResponseTime;

	private int slaResolutionTime;

	private boolean isIncludeWeekend;

	private long createdBy;

	private Timestamp createdOn;

	private long lastModifiedBy;

	private Timestamp lastModifiedOn;

	private boolean isDeleted;
	
	private ClientCompanyBranchDTO branchId;
}
