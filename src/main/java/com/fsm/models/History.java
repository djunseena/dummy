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

import com.io.iona.core.data.annotations.WithModelID;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@WithModelID
@Entity
@Table(name = "history", schema = "public")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class History implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_history_id_seq")
	@SequenceGenerator(name = "generator_history_id_seq", sequenceName = "history_history_id_seq", schema = "public", allocationSize = 1)
	@Column(name = "history_id", unique = true, nullable = false)
	private long historyId;

	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn( name = "order_id", nullable = false) 
	private Dispatch orderId;

	@Column(name = "reason")
	private String reason;

	@Column(name = "dispatch_status")
	private String dispatchStatus;

	@Column(name = "dispatch_action")
	private int dispatchAction;
	
	@Column(name = "created_by")
	@CreatedBy
	private long createdBy;	
	
	@Column(name = "created_on", columnDefinition = "DATE")
	@CreatedDate
	private Timestamp createdOn;	
	
}
