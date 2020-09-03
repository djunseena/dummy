package com.fsm.repositories.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.ReasonDTO;
import com.fsm.models.Reason;
import com.fsm.repositories.ReasonRepository;

@RestController
@RequestMapping("api")
public class ReasonRepositoryController {

	@Autowired
	private ReasonRepository reasonRepository;
	@Autowired
	private CancelService cancelService;
	@Autowired
	private HoldService holdService;

	ModelMapper modelMapper = new ModelMapper();

//	Convert Reason Entity To DTO
	public ReasonDTO convertReasonToDTO(Reason reason) {
		ReasonDTO reasonDto = modelMapper.map(reason, ReasonDTO.class);
		return reasonDto;
	}

//	Code untuk Get All Reason
	@GetMapping("/reason/all")
	public List<Reason> getReason() {
		return reasonRepository.findAll();
	}

//	Code untuk LOV reason hold (code :40) (start)
	@GetMapping("reason/hold/detail")
	public HashMap<String, Object> getHoldReasonDetail(@RequestParam(value = "pageNo") Integer pageNo,
			@RequestParam(value = "pageSize") Integer pageSize, @RequestParam(value = "reasonDesc") String reasonDesc) {

		HashMap<String, Object> mapResult = new HashMap<String, Object>();
		ArrayList<ReasonDTO> listReasonDto = new ArrayList<ReasonDTO>();

		for (Reason reason : holdService.getHoldReasonService(pageNo, pageSize, reasonDesc)) {
			ReasonDTO reasonDto = convertReasonToDTO(reason);
			listReasonDto.add(reasonDto);
		}
		int total = holdService.getTotalHoldReasonService(pageNo, 50, reasonDesc);

		mapResult.put("Message", "Cancel Reason All Data");
		mapResult.put("Total", total);
		mapResult.put("Data", listReasonDto);
		return mapResult;
	}

	@Service
	public class HoldService {

//		Code For get data hold reason
		public List<Reason> getHoldReasonService(Integer pageNo, Integer pageSize, String reasonDesc) {
			Pageable paging = PageRequest.of(pageNo, pageSize);

			Slice<Reason> pagedResult = reasonRepository.getHoldReason(reasonDesc, paging);

			List<Reason> listReason = pagedResult.getContent();

			return listReason;
		}

//		Code For get total data hold reason
		public int getTotalHoldReasonService(Integer pageNo, Integer pageSize, String reasonDesc) {
			Pageable paging = PageRequest.of(pageNo, pageSize);

			Slice<Reason> pagedResult = reasonRepository.getHoldReason(reasonDesc, paging);

			int totalReason = pagedResult.getNumberOfElements();

			return totalReason;
		}
	}
//	Code untuk LOV reason hold (code :40) (finish)	

//	Code untuk LOV reason cancel (code :41) (start)
	@GetMapping("reason/cancel/detail")
	public HashMap<String, Object> getCancelReasonDetail(@RequestParam(value = "pageNo") Integer pageNo,
			@RequestParam(value = "pageSize") Integer pageSize, @RequestParam(value = "reasonDesc") String reasonDesc) {

		HashMap<String, Object> mapResult = new HashMap<String, Object>();
		ArrayList<ReasonDTO> listReasonDto = new ArrayList<ReasonDTO>();

		for (Reason reason : cancelService.getCancelReasonService(pageNo, pageSize, reasonDesc)) {
			ReasonDTO reasonDto = convertReasonToDTO(reason);
			listReasonDto.add(reasonDto);
		}

		int total = cancelService.getTotalCancelReasonService(pageNo, 50, reasonDesc);
		mapResult.put("Message", "Cancel Reason All Data");
		mapResult.put("Total", total);
		mapResult.put("Data", listReasonDto);
		return mapResult;
	}

	@Service
	public class CancelService {

//		Code For get data cancel reason
		public List<Reason> getCancelReasonService(Integer pageNo, Integer pageSize, String reasonDesc) {
			Pageable paging = PageRequest.of(pageNo, pageSize);

			Slice<Reason> pagedResult = reasonRepository.getCancelReason(reasonDesc, paging);

			List<Reason> listReason = pagedResult.getContent();

			return listReason;
		}

//		Code For get total data cancel reason
		public int getTotalCancelReasonService(Integer pageNo, Integer pageSize, String reasonDesc) {
			Pageable paging = PageRequest.of(pageNo, pageSize);

			Slice<Reason> pagedResult = reasonRepository.getCancelReason(reasonDesc, paging);

			int totalReason = pagedResult.getNumberOfElements();

			return totalReason;
		}
	}
//	Code untuk LOV reason cancel (code :41) (finish)

}
