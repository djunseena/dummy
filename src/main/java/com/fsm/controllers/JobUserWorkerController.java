package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.JobUserWorkerDTO;
import com.fsm.models.JobUserWorker;
import com.io.iona.springboot.controllers.HibernateCRUDController;


@RestController
@RequestMapping("/job_user_worker")
public class JobUserWorkerController extends HibernateCRUDController<JobUserWorker, JobUserWorkerDTO>{
	
}
