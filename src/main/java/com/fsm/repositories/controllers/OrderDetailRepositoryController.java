package com.fsm.repositories.controllers;

import java.util.ArrayList;
import java.util.HashMap;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.DispatchDTO;
import com.fsm.models.Code;
import com.fsm.models.Dispatch;
import com.fsm.repositories.CodeRepository;
import com.fsm.repositories.DispatchRepository;

@RestController
@RequestMapping("api")
public class OrderDetailRepositoryController {

	@Autowired
	private DispatchRepository dispatchRepository;
	@Autowired
	private CodeRepository codeRepository;

	ModelMapper modelMapper = new ModelMapper();

//	Convert Entity to DTO
	private DispatchDTO convertToDTO(Dispatch dispatch) {
		DispatchDTO dispathDto = modelMapper.map(dispatch, DispatchDTO.class);
		return dispathDto;
	}

//	Get All data
	@GetMapping("/orderdetail")
	@ResponseBody
	public HashMap<String, Object> getAllData() {

		HashMap<String, Object> mapResult = new HashMap<String, Object>();
		ArrayList<DispatchDTO> listDistpatchDto = new ArrayList<DispatchDTO>();

		for (Dispatch dis : dispatchRepository.findAll()) {
			DispatchDTO dispatchDto = convertToDTO(dis);
			listDistpatchDto.add(dispatchDto);
		}

		String message;
		if (listDistpatchDto.isEmpty()) {
			message = "Data is Empty";
		} else {
			message = "Show Data By Filter";
		}

		mapResult.put("Message", message);
		mapResult.put("Total", listDistpatchDto.size());
		mapResult.put("Data", listDistpatchDto);

		return mapResult;
	}

//	Get All data Order Detail dengan filter
	@GetMapping("/dispatch/list")
	@ResponseBody
	public HashMap<String, Object> getAllData(@RequestParam(value = "dispatchStatus") String dispatchStatus,
			@RequestParam

			(value = "userId") long userId) {

		HashMap<String, Object> mapResult = new HashMap<String, Object>();
		ArrayList<DispatchDTO> listDistpatchDto = new ArrayList<DispatchDTO>();

		for (Dispatch dis : dispatchRepository.getDispatchByFilter(dispatchStatus, userId)) {
			DispatchDTO dispatchDto = convertToDTO(dis);
			listDistpatchDto.add(dispatchDto);
		}

		String message;
		if (listDistpatchDto.isEmpty()) {
			message = "Data is Empty";
		} else {
			message = "Show Data By Filter";
		}

		mapResult.put("Message", message);
		mapResult.put("Total", listDistpatchDto.size());
		mapResult.put("Data", listDistpatchDto);

		return mapResult;
	}

//	Get Data By Id
	@GetMapping("/dispatch/detail/{orderId}")
	public HashMap<String, Object> getDataById(@PathVariable(value = "orderId") long orderId) {
		HashMap<String, Object> mapDispatch = new HashMap<String, Object>();
		HashMap<String, Object> mapTemporary = new HashMap<String, Object>();

		Dispatch dispatch = dispatchRepository.findById(orderId).get();

		DispatchDTO dispatchDto = convertToDTO(dispatch);

		mapTemporary.put("orderId", dispatchDto.getOrderId());
		mapTemporary.put("titleTicket", dispatchDto.getTicketId().getTicketTitle());
		mapTemporary.put("dispatchDate", dispatchDto.getDispatchDate());
		mapTemporary.put("dispatchTime", dispatchDto.getDispatchTime());
		mapTemporary.put("companyName", dispatchDto.getTicketId().getBranchId().getCompanyId().getCompanyName());
		mapTemporary.put("branchName", dispatchDto.getTicketId().getBranchId().getBranchName());
		mapTemporary.put("ticketCode", dispatchDto.getTicketId().getTicketCode());
		mapTemporary.put("picName", dispatchDto.getTicketId().getPicId().getPicName());
		mapTemporary.put("picPhone", dispatchDto.getTicketId().getPicId().getPicPhone());
		mapTemporary.put("branchAddress", dispatchDto.getTicketId().getBranchId().getBranchAddress());
		mapTemporary.put("lat", dispatchDto.getTicketId().getBranchId().getBranchLatitude());
		mapTemporary.put("long", dispatchDto.getTicketId().getBranchId().getBranchLongitude());
		mapTemporary.put("dispatchDesc", dispatchDto.getDispatchDesc());
		mapTemporary.put("jobCategoryId", dispatchDto.getTicketId().getJobId().getJobCategoryId());
		mapTemporary.put("StatusSLA", dispatchDto.getTicketId().getPriorityId());
		mapTemporary.put("fileName", dispatchDto.getTicketId().getFileName());

		Code code = codeRepository.findById(dispatchDto.getTicketId().getPriorityId()).get();
		mapTemporary.put("status", code.getCodeName());

		String message;
		if (dispatch == null) {
			message = "Data is empty";
		} else {
			message = "Show data by Id";
		}

		mapDispatch.put("Message", message);
		mapDispatch.put("Data", mapTemporary);

		return mapDispatch;
	}

}
