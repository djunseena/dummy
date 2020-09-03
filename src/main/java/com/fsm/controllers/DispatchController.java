package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.DispatchDTO;
import com.fsm.models.Dispatch;
import com.io.iona.springboot.controllers.HibernateCRUDController;


@RestController
@RequestMapping("/dispatch")
public class DispatchController extends HibernateCRUDController<Dispatch, DispatchDTO>{
	
}
