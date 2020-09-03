package com.fsm.repositories.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import com.fsm.dtos.HistoryDTO;
import com.fsm.models.DispatchReport;
import com.fsm.models.History;
import com.fsm.repositories.CodeRepository;
import com.fsm.repositories.DispatchReportRepository;
import com.fsm.repositories.HistoryRepository;

@RestController
@RequestMapping("History")
public class HistoryRepositoryController {

	@Autowired
	HistoryRepository historyRepository;

	@Autowired
	CodeRepository codeRepository;

	@Autowired
	DispatchReportRepository dispatchReportRepository;

	ModelMapper modelMapper = new ModelMapper();

	// Convert Entity to DTO
	private HistoryDTO convertToDTO(History history) {
		HistoryDTO historyDTO = modelMapper.map(history, HistoryDTO.class);
		return historyDTO;
	}

	@GetMapping("getAllFinishedOrder")
	public Map<String, Object> getAllFinishedOrder(@RequestParam String search, Pageable pageable) {
		Map<String, Object> result = new HashMap<>();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		ArrayList<History> listHistoryEntity = (ArrayList<History>) historyRepository.getAllFinishedOrder(search,
				pageable);

		for (History historyItem : listHistoryEntity) {
			if (historyItem.getDispatchStatus().equalsIgnoreCase("Reported")
					|| historyItem.getDispatchStatus().equalsIgnoreCase("Finish")) {
				HashMap<String, Object> data = new HashMap<String, Object>();

				HistoryDTO historyDTO = modelMapper.map(historyItem, HistoryDTO.class);

				Long OrderId = historyDTO.getOrderId().getOrderId();
				data.put("historyId", historyDTO.getHistoryId());
				data.put("orderId", historyDTO.getOrderId().getOrderId());
				data.put("ticketCode", historyDTO.getOrderId().getTicketId().getTicketCode());
				data.put("ticketTitle", historyDTO.getOrderId().getTicketId().getTicketTitle());
				data.put("companyName",
						historyDTO.getOrderId().getTicketId().getBranchId().getCompanyId().getCompanyName());
				data.put("reason", historyDTO.getReason());
				data.put("dispatchStatus", historyDTO.getDispatchStatus());
				;
				DispatchReport dispatchReport = dispatchReportRepository.findByOrderId(OrderId);
				if (dispatchReport != null) {
					data.put("dispatchReportRating", dispatchReport.getDispatchReportRating());
				} else {
					data.put("dispatchReportRating", 0);
				}

				listData.add(data);
			}
		}
		int totalListFinishOrder = historyRepository.getTotalFinishedOrder(search);
		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("TotalData", totalListFinishOrder);
		result.put("Data", listData);

		return result;
	}

	@GetMapping("getAllFinishedOrderFilter")
	public Map<String, Object> getAllFinishedOrderFilter(@RequestParam String search, @RequestParam int filter,
			Pageable pageable) {
		Map<String, Object> result = new HashMap<>();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		ArrayList<History> listHistoryEntity = (ArrayList<History>) historyRepository.getAllFinishedOrderFilter(search,
				filter, pageable);

		for (History historyItem : listHistoryEntity) {
			if (historyItem.getDispatchStatus().equalsIgnoreCase("Reported")) {
				HashMap<String, Object> data = new HashMap<String, Object>();

				HistoryDTO historyDTO = modelMapper.map(historyItem, HistoryDTO.class);

				Long OrderId = historyDTO.getOrderId().getOrderId();
				data.put("historyId", historyDTO.getHistoryId());
				data.put("orderId", historyDTO.getOrderId().getOrderId());
				data.put("ticketCode", historyDTO.getOrderId().getTicketId().getTicketCode());
				data.put("ticketTitle", historyDTO.getOrderId().getTicketId().getTicketTitle());
				data.put("companyName",
						historyDTO.getOrderId().getTicketId().getBranchId().getCompanyId().getCompanyName());
				data.put("reason", historyDTO.getReason());
				data.put("dispatchReportRating",
						dispatchReportRepository.findByOrderId(OrderId).getDispatchReportRating());
				data.put("dispatchStatus", historyDTO.getDispatchStatus());
				;

				listData.add(data);
			}
		}
		int totalListFinishOrder = historyRepository.getTotalFinishedOrderFilter(search, filter);
		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("TotalData", totalListFinishOrder);
		result.put("Data", listData);

		return result;
	}

	@GetMapping("getAllCanceledOrder")
	public Map<String, Object> getAllCanceledOrderSearch(Pageable pageable, @RequestParam String search) {
		Map<String, Object> result = new HashMap<>();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		ArrayList<History> listHistoryEntity = (ArrayList<History>) historyRepository.getAllCanceledOrder(search,
				pageable);

		for (History historyItem : listHistoryEntity) {
			if (historyItem.getDispatchStatus().equalsIgnoreCase("Canceled")) {
				HashMap<String, Object> data = new HashMap<String, Object>();

				HistoryDTO historyDTO = modelMapper.map(historyItem, HistoryDTO.class);

				data.put("historyId", historyDTO.getHistoryId());
				data.put("orderId", historyDTO.getOrderId().getOrderId());
				data.put("ticketCode", historyDTO.getOrderId().getTicketId().getTicketCode());
				data.put("ticketTitle", historyDTO.getOrderId().getTicketId().getTicketTitle());
				data.put("companyName",
						historyDTO.getOrderId().getTicketId().getBranchId().getCompanyId().getCompanyName());
				data.put("reason", historyDTO.getReason());
				data.put("dispatchStatus", historyDTO.getDispatchStatus());
				;

				listData.add(data);
			}
		}

		int totalListFinishOrder = historyRepository.getTotalCanceledOrder(search);
		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("TotalData", totalListFinishOrder);
		result.put("Data", listData);

		return result;
	}

	public History convertToEntity(HistoryDTO historyDto) {
		return modelMapper.map(historyDto, History.class);
	}

	// API Create History
	@PostMapping("/history/create")
	public HashMap<String, Object> createHistory(@Valid @RequestBody ArrayList<HistoryDTO> historyDto) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		@Valid
		ArrayList<HistoryDTO> listHistories = historyDto;
		String message;

		for (HistoryDTO h : listHistories) {
			History history = convertToEntity(h);
			historyRepository.save(history);
		}

		if (listHistories == null) {
			message = "History Gagal Dibuat";
		} else {
			message = "History Berhasil Dibuat";
		}

		showHashMap.put("Message", message);
		showHashMap.put("Total Insert", listHistories.size());
		showHashMap.put("Data", listHistories);

		return showHashMap;
	}

	@GetMapping("/listHistoryDispatch")
	public HashMap<String, Object> showListWorkerForDispatch(@RequestParam Long orderId, Pageable pageable)
			throws ParseException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		ArrayList<History> listHistoryEntity = (ArrayList<History>) historyRepository.getHistoryDispatch(orderId,
				pageable);

		for (History item : listHistoryEntity) {
			HashMap<String, Object> data = new HashMap<>();
			HistoryDTO historyDTO = modelMapper.map(item, HistoryDTO.class);
			int dispatchAction = historyDTO.getDispatchAction();
			data.put("historyId", historyDTO.getHistoryId());
			data.put("dispatchStatus", historyDTO.getDispatchStatus());
			data.put("dispatchAction", codeRepository.findById((long) dispatchAction).orElse(null).getCodeName());
			data.put("Date", historyDTO.getCreatedOn());
			listData.add(data);
		}

		ArrayList<HashMap<String, Object>> list = new ArrayList<>();
		HashMap<String, Object> data = new HashMap<>();
		data.put("ticketCode", historyRepository.findByOrderIdFix(orderId).getOrderId().getTicketId().getTicketCode());
		data.put("history", listData);
		list.add(data);

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", list);

		return result;

	}

	@GetMapping("/detailHoldReason/{id}")
	public HashMap<String, Object> getDetailHoldReason(@PathVariable(value = "id") long id) {
		HashMap<String, Object> mapFinishOrder = new HashMap<String, Object>();
		HashMap<String, Object> mapTemporary = new HashMap<String, Object>();

		History history = historyRepository.getHoldReasonDispatch(id);

		HistoryDTO historyDTO = convertToDTO(history);

		mapTemporary.put("orderId", historyDTO.getOrderId().getOrderId());
		mapTemporary.put("ticketCode", historyDTO.getOrderId().getTicketId().getTicketCode());
		mapTemporary.put("reason", historyDTO.getReason());

		String message;
		if (history == null) {
			message = "Data Kosong";
		} else {
			message = "Menampilkan Alasan Hold";
		}

		mapFinishOrder.put("Message", message);
		mapFinishOrder.put("Data", mapTemporary);

		return mapFinishOrder;
	}
}