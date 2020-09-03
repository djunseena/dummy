package com.fsm.dtos;

import java.sql.Timestamp;

import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Data
@NoArgsConstructor
public class HistoryDTO {

	private long historyId;

	private DispatchDTO orderId;

	private String reason;

	private String dispatchStatus;

	private int dispatchAction;

	private long createdBy;

	private Timestamp createdOn;
}
