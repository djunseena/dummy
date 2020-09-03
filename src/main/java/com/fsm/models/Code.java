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
@Table(name = "code", schema = "public")
@Data
@NoArgsConstructor
public class Code implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_code_id_seq")
	@SequenceGenerator(name = "generator_code_id_seq", sequenceName = "code_code_id_seq", schema = "public", allocationSize = 1)
	@Column(name = "code_id", unique = true, nullable = false)
	private long codeId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_code_id", nullable = false)
	private CategoryCode categorycodeid;

	@Column(name = "code_name")
	private String codeName;

	@Column(name = "code_desc")
	private String codeDesc;

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
