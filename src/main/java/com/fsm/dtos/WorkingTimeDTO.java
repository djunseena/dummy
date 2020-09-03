package com.fsm.dtos;

import java.sql.Time;
import java.sql.Timestamp;

import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Data
@NoArgsConstructor
public class WorkingTimeDTO {

	private long wTimeId;

	private String wTimeName;

	private Time wTimeStart;

	private Time wTimeEnd;

	private String wTimeDesc;

	private long createdBy;

	private Timestamp createdOn;

	private long lastModifiedBy;

	private Timestamp lastModifiedOn;

	private boolean isDeleted;
}
