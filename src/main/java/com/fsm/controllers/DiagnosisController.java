package com.fsm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fsm.models.Diagnosis;
import com.fsm.repositories.DiagnosisRepository;
import com.io.iona.core.data.interfaces.models.IDataUtility;
import com.io.iona.core.enums.OperationMode;
import com.io.iona.springboot.actionflows.custom.CustomBeforeUpdate;
import com.io.iona.springboot.controllers.HibernateCRUDController;
import com.io.iona.springboot.sources.HibernateDataSource;
import com.fsm.dtos.DiagnosisDTO;

@RestController
@RequestMapping("/diagnosis")
public class DiagnosisController extends HibernateCRUDController<Diagnosis, DiagnosisDTO>
			implements CustomBeforeUpdate<Diagnosis, DiagnosisDTO>{
	
	@Autowired
	DiagnosisRepository diagnosisRepository;

	@Override
	public void beforeUpdate(IDataUtility arg0, HibernateDataSource<Diagnosis, DiagnosisDTO> dataSource, OperationMode arg2)
			throws Exception {
		// TODO Auto-generated method stub
		Diagnosis diagnosis = dataSource.getDataModel();
		Long id = diagnosis.getDiagnosisId();
		Diagnosis listDiagnosis = diagnosisRepository.findById(id).orElse(null);
		diagnosis.setCreatedOn(listDiagnosis.getCreatedOn());
		diagnosis.setCreatedBy(listDiagnosis.getCreatedBy());
		dataSource.setDataModel(diagnosis);
	}
	
	
}
