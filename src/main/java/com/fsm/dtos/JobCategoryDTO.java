package com.fsm.dtos;

import java.sql.Timestamp;

import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Data
@NoArgsConstructor
public class JobCategoryDTO {

	private long jobCategoryId;

	private JobClassDTO jobClassId;

	private String jobCategoryName;

	private String jobCategoryTag;

	private String jobCategoryDesc;

	private long createdBy;

	private Timestamp createdOn;

	private long lastModifiedBy;

	private Timestamp lastModifiedOn;

	private boolean isDeleted;
}
