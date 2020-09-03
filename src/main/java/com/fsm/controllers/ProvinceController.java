package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.ProvinceDTO;
import com.fsm.models.Province;
import com.io.iona.springboot.controllers.HibernateCRUDController;


@RestController
@RequestMapping("/province")
public class ProvinceController extends HibernateCRUDController<Province, ProvinceDTO>{
	
}
