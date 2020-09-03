package com.fsm.models;

import java.io.Serializable;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dispatch_image_report", schema = "public")
@Data
@NoArgsConstructor
public class DispatchImageReport implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "generator_dispatch_image_report_id_seq")
	@SequenceGenerator(name="generator_dispatch_image_report_id_seq", sequenceName="dispatch_image_report_image_report_id_seq", schema = "public", allocationSize = 1)
	@Column(name = "image_report_id", unique = true, nullable = false)
	private long imageReportId;
	
	@Column(name = "image_report_name")
	private String imageReportName;
	
	@Column(name = "image_report_path")
	private String imageReportPath;	
	
	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn( name = "dispatch_report_id", nullable = false)
	private DispatchReport dispatchReportId;
	
	@Column(name = "image_report_type")
	private int imageReportType;	
	
}
