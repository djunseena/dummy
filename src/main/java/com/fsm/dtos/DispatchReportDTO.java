package com.fsm.dtos;

import java.sql.Timestamp;

import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Data
@NoArgsConstructor
public class DispatchReportDTO {
	
	private long dispatchReportId;

	private DispatchDTO orderId;

	private String dispatchReportDiagnostic;

	private String dispatchReportReportedFailure;
	
	private String dispatchReportAction;	
	
	private String dispatchReportNote;
	
	private int dispatchReportRating;
	
	private long createdBy;

	private Timestamp createdOn;

	private long lastModifiedBy;
	
	private Timestamp lastModifiedOn;

}
