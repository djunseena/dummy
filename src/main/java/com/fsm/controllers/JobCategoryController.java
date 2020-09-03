package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.JobCategoryDTO;
import com.fsm.models.JobCategory;
import com.io.iona.springboot.controllers.HibernateCRUDController;


@RestController
@RequestMapping("/job_category")
public class JobCategoryController extends HibernateCRUDController<JobCategory, JobCategoryDTO>{
	
}
