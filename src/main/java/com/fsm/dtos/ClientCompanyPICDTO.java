package com.fsm.dtos;

import java.sql.Timestamp;

import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Data
@NoArgsConstructor
public class ClientCompanyPICDTO {

	private long picId;

	private ClientCompanyBranchDTO branchId;

	private ClientCompanyDTO companyId;

	private String picName;

	private String picEmail;

	private String picPhone;

	private String picDesc;

	private int picType;

	private long createdBy;

	private Timestamp createdOn;

	private long lastModifiedBy;

	private Timestamp lastModifiedOn;

	private boolean isDeleted;
}
