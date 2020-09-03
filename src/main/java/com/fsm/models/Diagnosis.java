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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Entity
@Table(name = "diagnosis", schema = "public")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Diagnosis implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_diagnosis_diagnosis_id_seq")
	@SequenceGenerator(name = "generator_diagnosis_diagnosis_id_seq", sequenceName = "diagnosis_diagnosis_id_seq", schema = "public", allocationSize = 1)
	@Column(name = "diagnosis_id", unique = true, nullable = false)
	private long diagnosisId;
	
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "job_category_id", nullable = false)
	private JobCategory jobCategoryId;
	
	@Column(name = "diagnosis_desc")
	private String diagnosisDesc;
	
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
