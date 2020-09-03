package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.WorkingTimeDTO;
import com.fsm.models.WorkingTime;
import com.io.iona.springboot.controllers.HibernateCRUDController;


@RestController
@RequestMapping("/working_time")
public class WorkingTimeController extends HibernateCRUDController<WorkingTime, WorkingTimeDTO>{
	
}
