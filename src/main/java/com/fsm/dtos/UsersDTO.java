package com.fsm.dtos;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Data
@NoArgsConstructor
public class UsersDTO {

	private long userId;
	
	private CityDTO primaryAreaId;
	
	private CityDTO secondaryAreaId;
	
	private RoleDTO roleId;
	
	private String userName;
	
	private String userPassword;
	
	private String userAddress;
	
	private String userAddressDetail;
	
	private String phone;
	
	private String mobilePhone;
	
	private String userEmail;
	
	private int userIdentity;
	
	private String userIdentityNo;
	
	private int userGender;
	
	private String userImage;
	
	private long createdBy;
	
	private Timestamp createdOn;
	
	private long lastModifiedBy;
	
	private Timestamp lastModifiedOn;
	
	private boolean isDeleted;
	
	private BigDecimal userLatitude;
	
	private BigDecimal userLongatitude;
	
	private String userFullName;
}
