package com.fsm.dtos;

import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Data
@NoArgsConstructor
public class JobUserWorkerDTO {

	private long jobUserWorkerId;
	
	private JobDTO jobId;
	
	private UsersDTO userId;
	
	private boolean isDeleted;
}
