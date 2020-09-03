package com.fsm.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportDTO {
	
	private int total;
	
	private int open;
	
	private int inprogress;
	
	private int hold;
	
	private int finish_reported;
	
	private int urgent;
	
	private int high;
	
	private int medium;
	
	private int low;
	
	private int request;
	
	private int task;
	
	private int incident;
}
