package com.fsm.repositories.controllers;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.ActionDTO;
import com.fsm.models.Action;
import com.fsm.models.DispatchReport;
import com.fsm.repositories.ActionRepository;
import com.fsm.repositories.DispatchReportRepository;
import com.fsm.repositories.FailureRepository;

import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidExpiresRangeException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.XmlParserException;

@RestController
@RequestMapping("api")
public class ActionRepositoryController {

	@Autowired
	ActionRepository actionRepository;

	@Autowired
	ActionService actionService;

	@Autowired
	DispatchReportRepository dispatchReportRepository;

	@Autowired
	FailureRepository failureRepository;
	
	ModelMapper modelMapper = new ModelMapper();

	public ActionDTO convertActionToDTO(Action action) {
		ActionDTO actionDto = modelMapper.map(action, ActionDTO.class);
		return actionDto;
	}
	
	private Action convertToEntity(ActionDTO actionDTO) {
		Action action = modelMapper.map(actionDTO, Action.class);
		return action;
	}

	@CrossOrigin(allowCredentials = "true")
	@GetMapping("action")
	public HashMap<String, Object> getAction(@RequestParam(value = "pageNo") Integer pageNo,
			@RequestParam(value = "pageSize") Integer pageSize, @RequestParam(value = "failureId") Long failureId,
			@RequestParam(value = "filter", defaultValue = "") String filter) {

		HashMap<String, Object> mapResult = new HashMap<String, Object>();
		ArrayList<ActionDTO> listAction = new ArrayList<ActionDTO>();

		for (Action action : actionService.getActionService(pageNo, pageSize, failureId, filter)) {
			ActionDTO actionDto = convertActionToDTO(action);
			listAction.add(actionDto);
		}

		int total = actionService.getTotalActionService(pageNo, 50, failureId, filter);

		String message;
		if (listAction.isEmpty()) {
			message = "Data is Empty";
		} else {
			message = "Show Data By Filter";
		}

		mapResult.put("Message", message);
		mapResult.put("Total", total);
		mapResult.put("Data", listAction);
		return mapResult;
	}

	@Service
	public class ActionService {

//		Code For get data Action Report
		public List<Action> getActionService(Integer pageNo, Integer pageSize, Long failureId, String filter) {
			Pageable paging = PageRequest.of(pageNo, pageSize);

			Slice<Action> pagedResult = actionRepository.getActionByFailureId(failureId, paging, filter);

			List<Action> listAction = pagedResult.getContent();

			return listAction;
		}

//		Code For get total data Action Report
		public int getTotalActionService(Integer pageNo, Integer pageSize, Long failureId, String filter) {
			Pageable paging = PageRequest.of(pageNo, pageSize);

			Slice<Action> pagedResult = actionRepository.getActionByFailureId(failureId, paging, filter);

			int totalAction = pagedResult.getNumberOfElements();

			return totalAction;
		}
	}

	@PutMapping("action/deleteAction")
	public HashMap<String, Object> deleteAction(@RequestParam Long actionId, @RequestBody Action body) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		try {
			Action action = actionRepository.findById(actionId).orElse(null);
			DispatchReport checkReportAction = dispatchReportRepository.checkingData(action.getActionDesc());
			if(checkReportAction == null) {
			action.setLastModifiedOn(dateNow);
			action.setDeleted(true);
			action.setLastModifiedBy(body.getLastModifiedBy());
			actionRepository.save(action);
				message = "Action Berhasil Dihapus";
				result.put("Status", HttpStatus.OK);
			}
			else {
				message = "Action Gagal Dihapus, Karena Data Masih Digunakan";
				result.put("Status", HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			message = e.getMessage() ;
		}
		result.put("Message", message);
		return result;
	}
	
	@PostMapping("action/create")
	public HashMap<String, Object> createDataDiagnosis(@Valid @RequestBody ActionDTO body){
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		ModelMapper modelMapper = new ModelMapper();
		Action actionEntity = modelMapper.map(body, Action.class);
		Boolean isFind = checkDataAlreadyExist(body);
			if(isFind == false) {
				actionRepository.save(actionEntity);
				body.setActionId(actionEntity.getActionId());
				
				result.put("Status", HttpStatus.OK);
				result.put("Message", "Action Berhasil Dibuat");
				result.put("Data", body);
			}else {
				Long failureId = body.getFailureId().getFailureId();
				String failureDesc = failureRepository.findById(failureId).orElse(null).getFailureDesc();
				result.put("Status", HttpStatus.BAD_REQUEST);
				result.put("Message", "Action Gagal Dibuat, Nama Action : '"+ body.getActionDesc() +"' dengan "
				+ "Failure : '"+ failureDesc +"' Sudah Terdaftar");
			}
		return result;
	}
	
	@PutMapping("action/update/{id}")
	public HashMap<String, Object> updateData (@PathVariable(value = "id") Long actionId,@Valid @RequestBody ActionDTO actionDTO){
		HashMap<String, Object> result = new HashMap<String, Object>();
		Action tempAction = actionRepository.findById(actionId).orElse(null);
		ArrayList<Action> checkData = actionRepository.checkDuplicateData(actionDTO.getActionDesc(), actionDTO.getFailureId().getFailureId(), actionId);
			if(checkData.isEmpty()) {
				actionDTO.setActionId(tempAction.getActionId());
				actionDTO.setCreatedBy(tempAction.getCreatedBy());
				actionDTO.setCreatedOn(tempAction.getCreatedOn());
				if(actionDTO.getActionDesc() == null) {
					actionDTO.setActionDesc(tempAction.getActionDesc());
				}
				tempAction = convertToEntity(actionDTO);
				actionRepository.save(tempAction);
				
				result.put("Status", HttpStatus.OK);
				result.put("Message", "Action Berhasil Diubah");
				result.put("Data", tempAction);
			}else {
				Long failureId = actionDTO.getFailureId().getFailureId();
				String failureDesc = failureRepository.findById(failureId).orElse(null).getFailureDesc();
				result.put("Status", HttpStatus.BAD_REQUEST);
				result.put("Message", "Action Gagal Diubah, Nama Action : '"+ actionDTO.getActionDesc() +"' dengan "
				+ "Failure : '"+ failureDesc +"' Sudah Terdaftar");
			}
		return result;
	}
	
	@GetMapping("action/all")
	public HashMap<String, Object> getAllAction(@RequestParam String search, Pageable pageable)throws InvalidKeyException, InvalidEndpointException, InvalidPortException, ErrorResponseException,
	IllegalArgumentException, InsufficientDataException, InternalException, InvalidBucketNameException,
	InvalidResponseException, NoSuchAlgorithmException, XmlParserException, InvalidExpiresRangeException,
	IOException {
		HashMap<String, Object> result = new HashMap<String, Object>();
		List<ActionDTO> listAction = new ArrayList<ActionDTO>();
		for(Action tempAction : actionRepository.getDataAction(search, pageable)) {
			ActionDTO actionDTO = convertActionToDTO(tempAction);
			if(actionDTO.isDeleted() == false) {
				listAction.add(actionDTO);
			}
		}
		ArrayList<Action> sizeTotal = (ArrayList<Action>) actionRepository.findAllAction();
		String message ;
		if(listAction.isEmpty()) {
			message = "Data Kosong";
		}else {
			message = "Data Action";
		}
		result.put("Status", HttpStatus.OK);
		result.put("Message", message);
		result.put("Total", sizeTotal.size());
		result.put("Data", listAction);
		return result;
	}
	private Boolean checkDataAlreadyExist (@Valid @RequestBody ActionDTO body) {
		Boolean isFind = false ;
		ArrayList<Action> checking = actionRepository.checkDataAlreadyExist(body.getActionDesc(), body.getFailureId().getFailureId());
			if(checking.isEmpty()) {
				isFind = false ;
			}else {
				isFind = true;
			}
		return isFind;
	}

}
