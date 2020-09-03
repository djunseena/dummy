package com.fsm.dtos;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Data
@NoArgsConstructor
public class DispatchDTO {

	private long orderId;

	private TroubleTicketDTO ticketId;	
	
	private UsersDTO userId;	
	
	private Date dispatchDate;

	private Time dispatchTime;

	private String dispatchDesc;

	private Timestamp startJob;

	private Timestamp endJob;

	private long createdBy;	

	private Timestamp createdOn;

	private long lastModifiedBy;

	private Timestamp lastModifiedOn;
}
