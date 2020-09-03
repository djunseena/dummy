package com.fsm.models;

import java.io.Serializable;
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
@Table(name = "dispatch", schema = "public")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Dispatch implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_dispatch_id_seq")
	@SequenceGenerator(name = "generator_dispatch_id_seq", sequenceName = "dispatch_order_id_seq", schema = "public", allocationSize = 1)
	@Column(name = "order_id", unique = true, nullable = false)
	private long orderId;

	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn( name = "ticket_id", nullable = false)
	private TroubleTicket ticketId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Users userId;

	@Column(name = "dispatch_date")
	private Date dispatchDate;

	@Column(name = "dispatch_time")
	private Time dispatchTime;

	@Column(name = "dispatch_desc")
	private String dispatchDesc;

	@Column(name = "start_job")
	private Timestamp startJob;

	@Column(name = "end_job")
	private Timestamp endJob;

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
	
}
