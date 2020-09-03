package com.fsm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.UserGroupDTO;
import com.fsm.models.UserGroup;
import com.io.iona.springboot.controllers.HibernateCRUDController;


@RestController
@RequestMapping("/user_group")
public class UserGroupController extends HibernateCRUDController<UserGroup, UserGroupDTO>{
	
}
