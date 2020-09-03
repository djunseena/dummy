package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.UOMDTO;
import com.fsm.models.UOM;
import com.io.iona.springboot.controllers.HibernateCRUDController;


@RestController
@RequestMapping("/uom")
public class UOMController extends HibernateCRUDController<UOM, UOMDTO>{
	
}
