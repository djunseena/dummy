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
@Table(name = "sla", schema = "public")
@Data
@NoArgsConstructor
public class SLA implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_sla_id_seq")
	@SequenceGenerator(name = "generator_sla_id_seq", sequenceName = "sla_sla_id_seq", schema = "public", allocationSize = 1)
	@Column(name = "sla_id", unique = true, nullable = false)
	private long slaId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sla_type_id", nullable = false)
	private SLAType slaTypeId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wtime_id", nullable = false)
	private WorkingTime wTimeId;

	@Column(name = "sla_response_time")
	private int slaResponseTime;

	@Column(name = "sla_resolution_time")
	private int slaResolutionTime;

	@Column(name = "is_include_weekend")
	private boolean isIncludeWeekend;

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "branch_id", nullable = false)
	private ClientCompanyBranch branchId;
}
