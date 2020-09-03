package com.fsm.dtos;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Data
@NoArgsConstructor
public class TroubleTicketDTO {

	private long ticketId;

	private long ticketStatusId;

	private long categoryId;

	private ClientCompanyBranchDTO branchId;

	private SLADTO slaId;

	private JobDTO jobId;

	private ClientCompanyPICDTO picId;

	private String ticketTitle;

	private Date ticketDate;

	private Time ticketTime;

	private String ticketDescription;
	
	private Timestamp ticketDueDate;

	private BigInteger ticketDurationTime;

	private String ticketCode;

	private long createdBy;

	private Timestamp createdOn;

	private long lastModifiedBy;

	private Timestamp lastModifiedOn;

	private boolean isDeleted;
	
	private String fileName;

	private String filePath;
	
	private long priorityId;
}
