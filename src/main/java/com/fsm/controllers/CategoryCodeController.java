package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.CategoryCodeDTO;
import com.fsm.models.CategoryCode;
import com.io.iona.springboot.controllers.HibernateCRUDController;


@RestController
@RequestMapping("/category_code")
public class CategoryCodeController extends HibernateCRUDController<CategoryCode, CategoryCodeDTO>{
	
}
