package com.fsm.models;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.io.iona.core.data.annotations.WithModelID;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Entity
@Table(name = "working_time", schema = "public")
@Data
@NoArgsConstructor
public class WorkingTime implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_working_time_id_seq")
	@SequenceGenerator(name = "generator_working_time_id_seq", sequenceName = "working_time_wtime_id_seq", schema = "public", allocationSize = 1)
	@Column(name = "wtime_id", unique = true, nullable = false)
	private long wTimeId;

	@Column(name = "wtime_name")
	private String wTimeName;

	@Column(name = "wtime_start")
	private Time wTimeStart;

	@Column(name = "wtime_end")
	private Time wTimeEnd;

	@Column(name = "wtime_desc")
	private String wTimeDesc;

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
