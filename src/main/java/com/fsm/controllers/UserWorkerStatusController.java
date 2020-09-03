package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.UserWorkerStatusDTO;
import com.fsm.models.UserWorkerStatus;
import com.io.iona.springboot.controllers.HibernateCRUDController;


@RestController
@RequestMapping("/user_worker_status")
public class UserWorkerStatusController extends HibernateCRUDController<UserWorkerStatus, UserWorkerStatusDTO>{
	
}
