package com.fsm.controllers;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.utility.HashUtil.SHA_256;
import com.fsm.dtos.UsersDTO;
import com.fsm.models.Users;
import com.io.iona.core.data.interfaces.models.IDataUtility;
import com.io.iona.core.enums.OperationMode;
import com.io.iona.springboot.actionflows.custom.CustomBeforeInsert;
import com.io.iona.springboot.controllers.HibernateCRUDController;
import com.io.iona.springboot.sources.HibernateDataSource;



@RestController
@RequestMapping("/users")
public class UsersController extends HibernateCRUDController<Users, UsersDTO> implements CustomBeforeInsert<Users, UsersDTO>{

	@Override
	public void beforeInsert(IDataUtility dataUtility, HibernateDataSource<Users, UsersDTO> dataSource, OperationMode arg2)
			throws Exception {
		// TODO Auto-generated method stub
		Users user = dataSource.getDataModel();
		user.setUserPassword(SHA_256.digestAsHex(user.getUserPassword()));
		dataSource.setDataModel(user);
	}
}
