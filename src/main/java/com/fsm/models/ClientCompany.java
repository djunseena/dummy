package com.fsm.models;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.io.iona.core.data.annotations.WithModelID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Entity
@Table(name = "client_company", schema = "public")
@Data
@NoArgsConstructor

public class ClientCompany implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_company_id_seq")
	@SequenceGenerator(name = "generator_company_id_seq", sequenceName = "client_company_company_id_seq", schema = "public", allocationSize = 1)
	@Column(name = "company_id", unique = true, nullable = false)
	private long companyId;

	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id", nullable = false)
	private City cityId;

	@Column(name = "company_name")
	private String companyName;

	@Column(name = "company_email")
	private String companyEmail;

	@Column(name = "company_password")
	private String companyPassword;

	@Column(name = "company_address1")
	private String companyAddress1;

	@Column(name = "company_address2")
	private String companyAddress2;

	@Column(name = "company_zip_code")
	private String companyZipCode;

	@Column(name = "company_phone")
	private String companyPhone;

	@Column(name = "created_by")
	private long createdBy;

	@Column(name = "created_on", columnDefinition = "DATE")
	private Timestamp createdOn;

	@Column(name = "last_modified_by")
	private long lastModifiedBy;

	@Column(name = "last_modified_on", columnDefinition = "DATE")
	private Timestamp lastModifiedOn;

	@Column(name = "is_deleted")
	private boolean isDeleted;
}
