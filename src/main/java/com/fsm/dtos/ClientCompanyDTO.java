package com.fsm.dtos;

import java.sql.Timestamp;

import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Data
@NoArgsConstructor
public class ClientCompanyDTO {

	private long companyId;

	private CityDTO cityId;

	private String companyName;

	private String companyEmail;

	private String companyPassword;

	private String companyAddress1;

	private String companyAddress2;

	private String companyZipCode;

	private String companyPhone;

	private long createdBy;

	private Timestamp createdOn;

	private long lastModifiedBy;

	private Timestamp lastModifiedOn;

	private boolean isDeleted;
}
