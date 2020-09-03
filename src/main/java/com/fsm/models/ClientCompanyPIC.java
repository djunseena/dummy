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

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Entity
@Table(name = "client_company_pic", schema = "public")
@Data
@NoArgsConstructor

public class ClientCompanyPIC implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_pic_id_seq")
	@SequenceGenerator(name = "generator_pic_id_seq", sequenceName = "client_company_pic_pic_id_seq", schema = "public", allocationSize = 1)
	@Column(name = "pic_id", unique = true, nullable = false)
	private long picId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "branch_id", nullable = false)
	private ClientCompanyBranch branchId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id", nullable = false)
	private ClientCompany companyId;

	@Column(name = "pic_name")
	private String picName;

	@Column(name = "pic_email")
	private String picEmail;

	@Column(name = "pic_phone")
	private String picPhone;

	@Column(name = "pic_desc")
	private String picDesc;

	@Column(name = "pic_type")
	private int picType;

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
