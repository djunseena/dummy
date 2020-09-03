package com.fsm.models;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
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

import com.io.iona.core.data.annotations.WithModelID;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Entity
@Table(name = "trouble_ticket", schema = "public")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TroubleTicket implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_trouble_ticket_id_seq")
	@SequenceGenerator(name = "generator_trouble_ticket_id_seq", sequenceName = "trouble_ticket_ticket_id_seq", schema = "public", allocationSize = 1)
	@Column(name = "ticket_id", unique = true, nullable = false)
	private long ticketId;

	@Column(name = "ticket_status_id")
	private long ticketStatusId;

	@Column(name = "category_id")
	private long categoryId;

	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "branch_id", nullable = false)
	private ClientCompanyBranch branchId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sla_id", nullable = false)
	private SLA slaId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "job_id", nullable = false)
	private Job jobId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pic_id", nullable = false)
	private ClientCompanyPIC picId;

	@Column(name = "ticket_title")
	private String ticketTitle;

	@Column(name = "ticket_date")
	private Date ticketDate;

	@Column(name = "ticket_time")
	private Time ticketTime;

	@Column(name = "ticket_description")
	private String ticketDescription;

	@Column(name = "ticket_due_date" , columnDefinition = "DATE")
	private Timestamp ticketDueDate;

	@Column(name = "ticket_duration_time")
	private BigInteger ticketDurationTime;

	@Column(name = "ticket_code")
	private String ticketCode;

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
	
	@Column(name = "file_name")
	private String fileName;
	
	@Column(name = "file_path")
	private String filePath;
	
	@Column(name = "priority_id")
	private long priorityId;
}
