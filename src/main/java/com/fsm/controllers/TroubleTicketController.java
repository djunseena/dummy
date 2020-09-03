package com.fsm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.TroubleTicketDTO;
import com.fsm.models.TroubleTicket;
import com.fsm.repositories.TroubleTicketRepository;
import com.io.iona.core.data.interfaces.models.IDataUtility;
import com.io.iona.core.enums.OperationMode;
import com.io.iona.springboot.actionflows.custom.CustomBeforeUpdate;
import com.io.iona.springboot.controllers.HibernateCRUDController;
import com.io.iona.springboot.sources.HibernateDataSource;

@RestController
@RequestMapping("/trouble_ticket")
public class TroubleTicketController extends HibernateCRUDController<TroubleTicket, TroubleTicketDTO>
		implements CustomBeforeUpdate<TroubleTicket, TroubleTicketDTO> {

	@Autowired
	TroubleTicketRepository troubleTicketRepository;

	@Override
	public void beforeUpdate(IDataUtility arg0, HibernateDataSource<TroubleTicket, TroubleTicketDTO> dataSource,
			OperationMode arg2) throws Exception {
		TroubleTicket troubleTicket = dataSource.getDataModel();
		Long id = troubleTicket.getTicketId();
		TroubleTicket listTroubleTicket = troubleTicketRepository.findById(id).orElse(null);
		troubleTicket.setCreatedOn(listTroubleTicket.getCreatedOn());
		troubleTicket.setCreatedBy(listTroubleTicket.getCreatedBy());
		dataSource.setDataModel(troubleTicket);
	}
}