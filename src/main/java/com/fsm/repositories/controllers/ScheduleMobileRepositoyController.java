package com.fsm.repositories.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.DispatchDTO;
import com.fsm.models.Code;
import com.fsm.models.Dispatch;
import com.fsm.repositories.CodeRepository;
import com.fsm.repositories.DispatchRepository;

@RestController
@RequestMapping("api")
public class ScheduleMobileRepositoyController {

	@Autowired
	private DispatchRepository dispatchRepository;

	@Autowired
	private CodeRepository codeRepository;

	@Autowired
	private ScheduleService scheduleService;

	ModelMapper modelMapper = new ModelMapper();

//	Convert Entity model Dispatch to DTO
	private DispatchDTO convertDispatchToDTO(Dispatch dispatch) {
		DispatchDTO dispatchDto = modelMapper.map(dispatch, DispatchDTO.class);
		return dispatchDto;
	}

//	Code for Looking Schedule for Mobile (start)
	@GetMapping("/schedule/mobile/{userId}")
	public HashMap<String, Object> getScheduleDispatch(@PathVariable(value = "userId") long userId,
			@RequestParam(value = "pageNo") Integer pageNo, @RequestParam(value = "pageSize") Integer pageSize,
			@RequestParam(value = "date") @DateTimeFormat(pattern = "dd/MM/yyyy") Date date) {

		String message = "";
		HashMap<String, Object> mapResult = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> listDispatchDto = new ArrayList<HashMap<String, Object>>();

		for (Dispatch dispatch : scheduleService.getScheduleService(pageNo, pageSize, date)) {
			if (dispatch.getUserId().getUserId() == userId) {
				DispatchDTO dispatchDto = convertDispatchToDTO(dispatch);
				HashMap<String, Object> mapDispatchTemp = getFilterDispatch(dispatchDto);
				listDispatchDto.add(mapDispatchTemp);
			}
		}

		int total = scheduleService.getTotalScheduleService(pageNo, 100, date);
		if (listDispatchDto.isEmpty()) {
			message = "No Schedule";
		} else {
			message = "Today schedule";
		}

		mapResult.put("message", message);
		mapResult.put("total", total);
		mapResult.put("data", listDispatchDto);

		return mapResult;
	}

	@Service
	public class ScheduleService {

//		Code for get data schedule
		public List<Dispatch> getScheduleService(Integer pageNo, Integer pageSize,
				@DateTimeFormat(pattern = "dd/MM/yyyy") Date date) {
			Pageable paging = PageRequest.of(pageNo, pageSize);

			Slice<Dispatch> pagedResult = dispatchRepository.getQuerySchedule(date, paging);

			List<Dispatch> listSchedule = pagedResult.getContent();
			return listSchedule;
		}

//		Code for get total data schedule
		public int getTotalScheduleService(Integer pageNo, Integer pageSize,
				@DateTimeFormat(pattern = "dd/MM/yyyy") Date date) {
			Pageable paging = PageRequest.of(pageNo, pageSize);

			Slice<Dispatch> pagedResult = dispatchRepository.getQuerySchedule(date, paging);

			int total = pagedResult.getNumberOfElements();
			return total;
		}

	}
//	Code for Looking Schedule for Mobile (finish)

//	Code for filter result from dispatch
	public HashMap<String, Object> getFilterDispatch(DispatchDTO dispatchDto) {
		HashMap<String, Object> mapResult = new HashMap<String, Object>();

		mapResult.put("userId", dispatchDto.getUserId().getUserId());
		mapResult.put("orderId", dispatchDto.getOrderId());
		mapResult.put("ticketTitle", dispatchDto.getTicketId().getTicketTitle());
		mapResult.put("companyName", dispatchDto.getTicketId().getBranchId().getCompanyId().getCompanyName());
		mapResult.put("dispatchDate", dispatchDto.getDispatchDate());
		mapResult.put("dispatchTime", dispatchDto.getDispatchTime());
		mapResult.put("dispatchStatus", "Confirmed");

		Code code = codeRepository.findById(dispatchDto.getTicketId().getPriorityId()).get();
		mapResult.put("status", code.getCodeName());

		return mapResult;
	}

}
