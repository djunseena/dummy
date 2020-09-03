package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.SLADTO;
import com.fsm.models.SLA;
import com.io.iona.springboot.controllers.HibernateCRUDController;


@RestController
@RequestMapping("/sla")
public class SLAController extends HibernateCRUDController<SLA, SLADTO>{
	
}
