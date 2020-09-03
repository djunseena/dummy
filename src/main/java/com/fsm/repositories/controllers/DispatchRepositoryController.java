package com.fsm.repositories.controllers;

import java.util.ArrayList;
import java.util.HashMap;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.DispatchDTO;
import com.fsm.models.Dispatch;
import com.fsm.models.History;
import com.fsm.models.TroubleTicket;
import com.fsm.repositories.CodeRepository;
import com.fsm.repositories.DispatchRepository;
import com.fsm.repositories.HistoryRepository;
import com.fsm.repositories.JobCategoryReportRepository;
import com.fsm.repositories.TroubleTicketRepository;

@RestController
@RequestMapping("Dispatch")
public class DispatchRepositoryController {

	ModelMapper modelMapper = new ModelMapper();

	@Autowired
	DispatchRepository dispatchRepository;

	@Autowired
	HistoryRepository historyRepository;

	@Autowired
	CodeRepository codeRepository;

	@Autowired
	JobCategoryReportRepository jobCategoryReportRepository;

	@Autowired
	TroubleTicketRepository troubleTicketRepository;

//	Convert Entity to DTO
	private DispatchDTO convertToDTO(Dispatch dispatch) {
		DispatchDTO dispatchDTO = modelMapper.map(dispatch, DispatchDTO.class);
		return dispatchDTO;
	}

	// Create Dispatch
	@PostMapping("/create")
	public HashMap<String, Object> createDispatch(@Valid @RequestBody Dispatch dispatch) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		dispatch.getCreatedBy();
		dispatch.getDispatchDate();
		dispatch.getDispatchTime();
		dispatch.getLastModifiedBy();
		dispatch.getTicketId();
		dispatch.getUserId();

		TroubleTicket ticketId = dispatch.getTicketId();
		String dispatchDesc = troubleTicketRepository.findById(ticketId.getTicketId()).orElse(null)
				.getTicketDescription().trim();

		dispatchRepository.saveDispatch(dispatch.getCreatedBy(), dispatch.getDispatchDate(), dispatchDesc,
				dispatch.getDispatchTime(), dispatch.getLastModifiedBy(), dispatch.getTicketId().getTicketId(),
				dispatch.getUserId().getUserId());
		historyRepository.saveHistoryDispatch();
		showHashMap.put("Message", "Dispatch Berhasil Dibuat");
		showHashMap.put("Status", HttpStatus.OK);
		showHashMap.put("Data", dispatch);

		return showHashMap;
	}

	@GetMapping("getDataSchedule")
	public HashMap<String, Object> getDataSchedule() {
		HashMap<String, Object> result = new HashMap<String, Object>();
		ArrayList<Dispatch> listDispatch = (ArrayList<Dispatch>) dispatchRepository.findAll();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		for (Dispatch item : listDispatch) {
			HashMap<String, Object> mapping = new HashMap<String, Object>();
			mapping.put("orderid", item.getOrderId());
			mapping.put("dispatchDate", item.getDispatchDate());
			mapping.put("dispatchTime", item.getDispatchTime());
			mapping.put("customer", item.getTicketId().getBranchId().getCompanyId().getCompanyName());
			History history = historyRepository.getStatusByOrderId(item.getOrderId());
			mapping.put("status", history.getDispatchStatus());
			listData.add(mapping);
		}
		result.put("Status", HttpStatus.OK);
		result.put("Message", "Data Schedule");
		result.put("Data", listData);
		return result;
	}

	@GetMapping("getDetailDataSchedule")
	public HashMap<String, Object> showListDataDetailOnScheduleById(@RequestParam Long orderid) {
		DispatchDTO dispatchDTO = new DispatchDTO();
		HashMap<String, Object> result = new HashMap<String, Object>();
		HashMap<String, Object> temp = new HashMap<String, Object>();
		Dispatch dispatch = dispatchRepository.findById(orderid).orElse(null);
		History history = historyRepository.getStatusByOrderId(orderid);

		dispatchDTO = modelMapper.map(dispatch, DispatchDTO.class);

		temp.put("oderId", dispatchDTO.getOrderId());
		temp.put("detailTicket", dispatchDTO.getTicketId().getTicketCode());
		temp.put("company_name", dispatchDTO.getTicketId().getBranchId().getCompanyId().getCompanyName());
		temp.put("branch_address", dispatchDTO.getTicketId().getBranchId().getBranchAddress());
		temp.put("city", dispatchDTO.getTicketId().getBranchId().getCityId().getCityName());
		temp.put("taskType", codeRepository.findById(Long.valueOf(dispatchDTO.getTicketId().getCategoryId()))
				.orElse(null).getCodeName());
		temp.put("taskTitle", dispatchDTO.getTicketId().getJobId().getJobDesc());
		temp.put("status", history.getDispatchStatus());
		temp.put("description", dispatchDTO.getTicketId().getTicketDescription());
		temp.put("branchName", dispatchDTO.getTicketId().getBranchId().getBranchName());
		String message = " ";
		if (dispatch == null) {
			message = "Data Kosong";
		} else {
			message = "Data Schedule";
		}
		result.put("Message", message);
		result.put("Status", HttpStatus.OK);
		result.put("Data", temp);
		return result;
	}

	@GetMapping("/detailDispatch/{id}")
	public HashMap<String, Object> getDetailDispatchById(@PathVariable(value = "id") long id) {
		HashMap<String, Object> mapDispatch = new HashMap<String, Object>();
		HashMap<String, Object> mapTemporary = new HashMap<String, Object>();

		Dispatch dispatch = dispatchRepository.findById(id).orElse(null);

		DispatchDTO dispatchDto = convertToDTO(dispatch);
		Long orderId = dispatchDto.getOrderId();
		Long priorityId = dispatchDto.getTicketId().getPriorityId();
		Long categoryId = dispatchDto.getTicketId().getCategoryId();
		Long jobCategoryId = dispatchDto.getTicketId().getJobId().getJobCategoryId().getJobCategoryId();
		Long reportId = jobCategoryReportRepository.findByJobCategoryId(jobCategoryId).getReportId();
		mapTemporary.put("orderId", dispatchDto.getOrderId());
		mapTemporary.put("ticketCode", dispatchDto.getTicketId().getTicketCode());
		mapTemporary.put("ticketTitle", dispatchDto.getTicketId().getTicketTitle());
		mapTemporary.put("companyName", dispatchDto.getTicketId().getBranchId().getCompanyId().getCompanyName());
		mapTemporary.put("branchName", dispatchDto.getTicketId().getBranchId().getBranchName());
		mapTemporary.put("picName", dispatchDto.getTicketId().getPicId().getPicName());
		mapTemporary.put("workerName", dispatchDto.getUserId().getUserFullName());
		mapTemporary.put("status", historyRepository.getStatusByOrderId(orderId).getDispatchStatus());
		mapTemporary.put("slaName",
				dispatchDto.getTicketId().getSlaId().getSlaTypeId().getSlaTypeName() + " / "
						+ dispatchDto.getTicketId().getSlaId().getSlaResponseTime() + " Min (Response Time) / "
						+ dispatchDto.getTicketId().getSlaId().getSlaResolutionTime() + " Hour (Resolution Time)");
		mapTemporary.put("priorityName", codeRepository.findById(priorityId).orElse(null).getCodeName());
		mapTemporary.put("dispatchDate", dispatchDto.getDispatchDate());
		mapTemporary.put("dispatchTime", dispatchDto.getDispatchTime());
		mapTemporary.put("ticketCategory", codeRepository.findById(categoryId).orElse(null).getCodeName());
		mapTemporary.put("ticketDurationTime", dispatchDto.getTicketId().getTicketDurationTime());
		mapTemporary.put("jobCategoryName",
				dispatchDto.getTicketId().getJobId().getJobCategoryId().getJobCategoryName());
		mapTemporary.put("jobClassName",
				dispatchDto.getTicketId().getJobId().getJobCategoryId().getJobClassId().getJobClassName());
		mapTemporary.put("dispatchDesc", dispatchDto.getDispatchDesc());
		mapTemporary.put("reportName", codeRepository.findById(reportId).orElse(null).getCodeName());
		mapTemporary.put("jobName", dispatchDto.getTicketId().getJobId().getJobName());

		String message;
		if (dispatch == null) {
			message = "Data Kosong";
		} else {
			message = "Data Dispatch";
		}

		mapDispatch.put("Status", HttpStatus.OK);
		mapDispatch.put("Message", message);
		mapDispatch.put("Data", mapTemporary);

		return mapDispatch;

	}

}
