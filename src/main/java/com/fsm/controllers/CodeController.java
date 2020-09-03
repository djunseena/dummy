package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.CodeDTO;
import com.fsm.models.Code;
import com.io.iona.springboot.controllers.HibernateCRUDController;


@RestController
@RequestMapping("/code")
public class CodeController extends HibernateCRUDController<Code, CodeDTO>{
	
}
