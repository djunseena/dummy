package com.fsm.dtos;

import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Data
@NoArgsConstructor
public class DispatchImageReportDTO {

	private long imageReportId;

	private String imageReportName;

	private String imageReportPath;
	
	private DispatchReportDTO dispatchReportId;
	
	private int imageReportType;
}
