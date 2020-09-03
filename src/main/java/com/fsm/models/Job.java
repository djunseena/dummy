package com.fsm.models;

import java.io.Serializable;
import java.math.BigInteger;
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
@Table(name = "job", schema = "public")
@Data
@NoArgsConstructor
public class Job implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_job_id_seq")
	@SequenceGenerator(name = "generator_job_id_seq", sequenceName = "job_job_id_seq", schema = "public", allocationSize = 1)
	@Column(name = "job_id", unique = true, nullable = false)
	private long jobId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "uom_id", nullable = false)
	private UOM uomId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "job_category_id", nullable = false)
	private JobCategory jobCategoryId;

	@Column(name = "job_name")
	private String jobName;

	@Column(name = "job_tag")
	private String jobTag;

	@Column(name = "job_desc")
	private String jobDesc;

	@Column(name = "transport_fee")
	private BigInteger transportFee;

	@Column(name = "incl_transport")
	private boolean inclTransport;

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
