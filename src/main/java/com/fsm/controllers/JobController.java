package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.JobDTO;
import com.fsm.models.Job;
import com.io.iona.springboot.controllers.HibernateCRUDController;


@RestController
@RequestMapping("/job")
public class JobController extends HibernateCRUDController<Job, JobDTO>{
	
}
