package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.CityDTO;
import com.fsm.models.City;
import com.io.iona.springboot.controllers.HibernateCRUDController;



@RestController
@RequestMapping("/city")
public class CityController extends HibernateCRUDController<City, CityDTO>{
	
	
}
