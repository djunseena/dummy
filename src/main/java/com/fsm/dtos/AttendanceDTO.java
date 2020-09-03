package com.fsm.dtos;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Data
@NoArgsConstructor
public class AttendanceDTO {

	private long attendanceId;
	
	private UsersDTO userId;
	
	private Timestamp checkIn;
	
	private Timestamp checkOut;
	
	private BigDecimal checkInLat;
	
	private BigDecimal checkInLong;
	
	private BigDecimal checkOutLat;
	
	private BigDecimal checkOutLong;
	
	private long createadBy;
	
	private Timestamp createdOn;
	
	private long lastModifiedBy;
	
	private Timestamp lastModifiedOn;
	
	private boolean isDeleted;
	
}
