package com.fsm.models;

import java.io.Serializable;
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

import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Entity
@Table(name = "client_contract_file", schema = "public")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ClientContractFile implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_client_contract_file_id_seq")
	@SequenceGenerator(name = "generator_client_contract_file_id_seq", sequenceName = "client_contract_file_contract_id_seq", schema = "public", allocationSize = 1)
	@Column(name = "contract_id", unique = true, nullable = false)
	private long contractId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id", nullable = false)
	private ClientCompany companyId;

	@Column(name = "contract_file_name")
	private String contractFileName;

	@Column(name = "contract_file_path")
	private String contractFilePath;

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

}
