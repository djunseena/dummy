package com.fsm.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Entity
@Table(name = "users", schema = "public")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Users implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_user_id_seq")
	@SequenceGenerator(name = "generator_user_id_seq", sequenceName = "users_user_id_seq", schema = "public", allocationSize = 1)
	@Column(name = "user_id", unique = true, nullable = false)
	private long userId;

	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "primary_area_id", nullable = false)
	private City primaryAreaId;
	
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "secondary_area_id", nullable = false)
	private City secondaryAreaId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id", nullable = false)
	private Role roleId;
	
	@Column(name = "user_name")
	private String userName;
	
	@Column(name = "user_password")
	private String userPassword;
	
	@Column(name = "user_address")
	private String userAddress;
	
	@Column(name = "user_address_detail")
	private String userAddressDetail;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "mobile_phone")
	private String mobilePhone;
	
	@Column(name = "user_email")
	private String userEmail;
	
	@Column(name = "user_identity")
	private int userIdentity;

	@Column(name = "user_identity_no")
	private String userIdentityNo;
	
	@Column(name = "user_gender")
	private int userGender;

	@Column(name = "user_image")
	private String userImage;
	
	@Column(name = "created_by")
	@CreatedBy
	private long createdBy;

	@Column(name = "created_on", columnDefinition = "DATE")
	@CreatedDate
	private Timestamp createdOn;

	@Column(name = "last_modified_by")
	@LastModifiedBy
	private long lastModifiedBy;

	@Column(name = "last_modified_on", columnDefinition = "DATE")
	@LastModifiedDate
	private Timestamp lastModifiedOn;
	
	@Column(name = "is_deleted")
	private boolean isDeleted;
	
	@Column(name = "user_latitude")
	private BigDecimal userLatitude;
	
	@Column(name = "user_longatitude")
	private BigDecimal userLongatitude;
	
	@Column(name = "user_full_name")
	private String userFullName;

}
