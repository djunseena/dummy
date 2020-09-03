package com.fsm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fsm.models.Failure;
import com.fsm.repositories.FailureRepository;
import com.io.iona.core.data.interfaces.models.IDataUtility;
import com.io.iona.core.enums.OperationMode;
import com.io.iona.springboot.actionflows.custom.CustomBeforeUpdate;
import com.io.iona.springboot.controllers.HibernateCRUDController;
import com.io.iona.springboot.sources.HibernateDataSource;
import com.fsm.dtos.FailureDTO;

@RestController
@RequestMapping("/failure")
public class FailureController extends HibernateCRUDController<Failure, FailureDTO>
		implements CustomBeforeUpdate<Failure, FailureDTO>{
	
	@Autowired
	FailureRepository failureRepository;

	@Override
	public void beforeUpdate(IDataUtility arg0, HibernateDataSource<Failure, FailureDTO> dataSource, OperationMode arg2)
			throws Exception {
		// TODO Auto-generated method stub
		Failure failure = dataSource.getDataModel();
		Long id = failure.getFailureId();
		Failure listFailure = failureRepository.findById(id).orElse(null);
		failure.setCreatedBy(listFailure.getCreatedBy());
		failure.setCreatedOn(listFailure.getCreatedOn());
		dataSource.setDataModel(failure);
	}
	
	

}
