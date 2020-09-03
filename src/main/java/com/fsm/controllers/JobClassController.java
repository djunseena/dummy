package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.JobClassDTO;
import com.fsm.models.JobClass;
import com.io.iona.springboot.controllers.HibernateCRUDController;


@RestController
@RequestMapping("/job_class")
public class JobClassController extends HibernateCRUDController<JobClass, JobClassDTO>{
	
}
