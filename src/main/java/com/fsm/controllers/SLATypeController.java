package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.SLATypeDTO;
import com.fsm.models.SLAType;
import com.io.iona.springboot.controllers.HibernateCRUDController;


@RestController
@RequestMapping("/sla_type")
public class SLATypeController extends HibernateCRUDController<SLAType, SLATypeDTO>{
	
}
