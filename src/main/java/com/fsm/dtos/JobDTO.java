package com.fsm.dtos;

import java.math.BigInteger;
import java.sql.Timestamp;

import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Data
@NoArgsConstructor
public class JobDTO {

	private long jobId;

	private UOMDTO uomId;

	private JobCategoryDTO jobCategoryId;

	private String jobName;

	private String jobTag;

	private String jobDesc;

	private BigInteger transportFee;

	private boolean inclTransport;

	private long createdBy;

	private Timestamp createdOn;

	private long lastModifiedBy;

	private Timestamp lastModifiedOn;

	private boolean isDeleted;
}
