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
@Table(name = "job_category_report", schema = "public")
@Data
@NoArgsConstructor
public class JobCategoryReport implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_job_category_report_job_category_report_id_seq")
	@SequenceGenerator(name = "generator_job_category_report_job_category_report_id_seq", sequenceName = "job_category_report_job_category_report_id_seq", schema = "public", allocationSize = 1)
	@Column(name = "job_category_report_id", unique = true, nullable = false)
	private long jobCategoryReportId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "job_category_id", nullable = false)
	private JobCategory jobCategoryId;
	
	@Column(name = "report_id")
	private long reportId;

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
