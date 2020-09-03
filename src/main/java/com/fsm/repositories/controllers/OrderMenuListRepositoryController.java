package com.fsm.repositories.controllers;

import java.util.ArrayList;
import java.util.HashMap;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.models.Dispatch;
import com.fsm.models.History;
import com.fsm.repositories.CodeRepository;
import com.fsm.repositories.DispatchRepository;
import com.fsm.repositories.HistoryRepository;

import lombok.Data;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("order")
public class OrderMenuListRepositoryController {

	@Autowired
	private HistoryRepository historyRepository;

	@Autowired
	private CodeRepository codeRepository;

	@Autowired
	private DispatchRepository dispatchRepository;

//	Code untuk GET total order perstatus
	@GetMapping("/total/{userId}")
	public ArrayList<HashMap<String, Object>> getTotalOrder(@PathVariable(value = "userId") Long userId) {
		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		String[] statusArray = new String[] { "Confirmed", "Finish", "Reported", "Hold", "Canceled" };

		for (String status : statusArray) {
			OrderMenuList orderMenuList = new OrderMenuList(status);
			int totalOrder = getOrderListByStatus(orderMenuList, userId).size();
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("status", status);
			data.put("count", totalOrder);
			listData.add(data);
		}

		return listData;
	}

//	Code untuk Cek Detail Order list
	@PostMapping("/list/{userId}")
	private HashMap<String, Object> getOrderListStatusPage(@Valid @RequestBody OrderMenuList orderMenuList,
			@PathVariable(value = "userId") Long userId) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		HashMap<Integer, ArrayList<HashMap<String, Object>>> orderList = new HashMap<Integer, ArrayList<HashMap<String, Object>>>();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		int limitDataPerPage = orderMenuList.pageSize;
		int page = 1;
		orderList.put(page, listData);
		for (HashMap<String, Object> data : getOrderListByStatus(orderMenuList, userId)) {
			orderList.get(page).add(data);
			if (orderList.get(page).size() == limitDataPerPage) {
				page++;
				listData = new ArrayList<HashMap<String, Object>>();
				orderList.put(page, listData);
			}
		}
		result.put("Total", getOrderListByStatus(orderMenuList, userId).size());
		result.put("Data", orderList.get(orderMenuList.getPageNumber()));
		return result;
	}

	private ArrayList<HashMap<String, Object>> getOrderListByStatus(@Valid @RequestBody OrderMenuList orderMenuList,
			@PathVariable(value = "userId") Long userId) {
		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		// adding Khusus yang Confirmed
		if (orderMenuList.getStatus().equals("Confirmed")) {
			for (Dispatch dispatch : dispatchRepository.getOrderListDispatch(userId)) {
				HashMap<String, Object> dataDispatch = convertDispatchToHashMap(dispatch);
				listData.add(dataDispatch);
			}
		}

		for (HashMap<String, Object> data : getOrderListByOrderId(userId)) {

			if (data.get("status").equals(orderMenuList.getStatus())
					&& !orderMenuList.getStatus().equals("Confirmed")) {
				listData.add(data);
			} else if (data.get("status").equals("Start") && orderMenuList.getStatus().equals("Confirmed")) {
				listData.add(data);
			}
		}
		return listData;
	}

	private ArrayList<HashMap<String, Object>> getOrderListByOrderId(Long userId) {
		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		for (History history : historyRepository.getOrderList(userId)) {

			listData.add(convertHistoryToHashMap(history));

		}
		return listData;
	}

	private HashMap<String, Object> convertHistoryToHashMap(History history) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		long priorityId = history.getOrderId().getTicketId().getPriorityId();
		data.put("orderId", history.getOrderId().getOrderId());
		data.put("ticketTitle", history.getOrderId().getTicketId().getTicketTitle());
		data.put("companyName", history.getOrderId().getTicketId().getBranchId().getCompanyId().getCompanyName());
		data.put("dispatchDate", history.getOrderId().getDispatchDate());
		data.put("dispatchTime", history.getOrderId().getDispatchTime());
		data.put("priority", codeRepository.findById(priorityId).orElse(null).getCodeName());
		data.put("createdOn", history.getCreatedOn());
		data.put("status", history.getDispatchStatus());
		return data;
	}

	private HashMap<String, Object> convertDispatchToHashMap(Dispatch dispatch) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		long priorityId = dispatch.getTicketId().getPriorityId();
		data.put("orderId", dispatch.getOrderId());
		data.put("ticketTitle", dispatch.getTicketId().getTicketTitle());
		data.put("companyName", dispatch.getTicketId().getBranchId().getCompanyId().getCompanyName());
		data.put("dispatchDate", dispatch.getDispatchDate());
		data.put("dispatchTime", dispatch.getDispatchTime());
		data.put("priority", codeRepository.findById(priorityId).orElse(null).getCodeName());
		data.put("createdOn", dispatch.getCreatedOn());
		data.put("status", codeRepository.findById(priorityId).orElse(null).getCodeName());
		return data;
	}

	@Data
	@NoArgsConstructor
	private static class OrderMenuList {
		private String status;
		private int pageNumber;
		private int pageSize;

		public OrderMenuList(String status) {
			super();
			this.status = status;
		}

	}
}
