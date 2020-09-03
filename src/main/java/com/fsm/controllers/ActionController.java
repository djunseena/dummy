package com.fsm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fsm.models.Action;
import com.fsm.repositories.ActionRepository;
import com.io.iona.core.data.interfaces.models.IDataUtility;
import com.io.iona.core.enums.OperationMode;
import com.io.iona.springboot.actionflows.custom.CustomBeforeUpdate;
import com.io.iona.springboot.controllers.HibernateCRUDController;
import com.io.iona.springboot.sources.HibernateDataSource;
import com.fsm.dtos.ActionDTO;

@RestController
@RequestMapping("/action")
public class ActionController extends HibernateCRUDController<Action, ActionDTO>
		implements CustomBeforeUpdate<Action, ActionDTO>{

	@Autowired
	ActionRepository actionRepository;

	@Override
	public void beforeUpdate(IDataUtility arg0, HibernateDataSource<Action, ActionDTO> dataSource, OperationMode arg2)
			throws Exception {
		// TODO Auto-generated method stub
		Action action = dataSource.getDataModel();
		Long id = action.getActionId();
		Action listAction = actionRepository.findById(id).orElse(null);
		action.setCreatedOn(listAction.getCreatedOn());
		action.setCreatedBy(listAction.getCreatedBy());
		dataSource.setDataModel(action);
	}
}
