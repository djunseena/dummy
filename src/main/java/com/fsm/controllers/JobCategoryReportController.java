package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.JobCategoryReportDTO;
import com.fsm.models.JobCategoryReport;
import com.io.iona.springboot.controllers.HibernateCRUDController;


@RestController
@RequestMapping("/job_category_report")
public class JobCategoryReportController extends HibernateCRUDController<JobCategoryReport, JobCategoryReportDTO>{
	
}
