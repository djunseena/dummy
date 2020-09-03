package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.RoleDTO;
import com.fsm.models.Role;
import com.io.iona.springboot.controllers.HibernateCRUDController;


@RestController
@RequestMapping("/role")
public class RoleController extends HibernateCRUDController<Role, RoleDTO>{
	
}
