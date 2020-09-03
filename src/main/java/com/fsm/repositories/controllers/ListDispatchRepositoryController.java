package com.fsm.repositories.controllers;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.DispatchDTO;
import com.fsm.models.Dispatch;
import com.fsm.repositories.DispatchRepository;
import com.fsm.repositories.HistoryRepository;
import com.ibm.icu.text.SimpleDateFormat;

@RestController
@RequestMapping("/api")
public class ListDispatchRepositoryController {

	@Autowired
	DispatchRepository listdispatchRepository;

	@Autowired
	HistoryRepository historyRepository;

	ModelMapper modelMapper = new ModelMapper();

	@GetMapping("listDispatch")
	public HashMap<String, Object> listDispatch(@RequestParam String search, Pageable pageable) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();
		ArrayList<Dispatch> listDispatchEntity = (ArrayList<Dispatch>) listdispatchRepository.getListDispatch(search,
				pageable);

		for (Dispatch item : listDispatchEntity) {
			HashMap<String, Object> data = new HashMap<>();

			DispatchDTO dispatchDTO = modelMapper.map(item, DispatchDTO.class);

			Long orderId = dispatchDTO.getOrderId();
			data.put("orderId", dispatchDTO.getOrderId());
			data.put("ticketCode", dispatchDTO.getTicketId().getTicketCode());
			data.put("ticketTitle", dispatchDTO.getTicketId().getTicketTitle());
			data.put("workerName", dispatchDTO.getUserId().getUserFullName());
			data.put("companyName", dispatchDTO.getTicketId().getBranchId().getCompanyId().getCompanyName());
			data.put("startJob", dispatchDTO.getStartJob());
			data.put("resolutionTime", dispatchDTO.getTicketId().getSlaId().getSlaResolutionTime() + " Hours");
			data.put("dispatchStatus", historyRepository.getStatusByOrderId(orderId).getDispatchStatus());
			listData.add(data);
		}

		int totalList = listdispatchRepository.getTotalListDispatch(search);
		int totalListPage = (int) Math.ceil((listData.size() / 10) + 1);

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);
		result.put("TotalData", totalList);
		result.put("TotalPage", totalListPage);

		return result;
	}

	@GetMapping("listDispatchFilter")
	public HashMap<String, Object> listDispatchFilter(@RequestParam String search, @RequestParam String filter,
			Pageable pageable) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();
		ArrayList<Dispatch> listDispatchEntity = (ArrayList<Dispatch>) listdispatchRepository
				.getListDispatchFilter(search, filter, pageable);

		for (Dispatch item : listDispatchEntity) {
			HashMap<String, Object> data = new HashMap<>();

			DispatchDTO dispatchDTO = modelMapper.map(item, DispatchDTO.class);

			Long orderId = dispatchDTO.getOrderId();
			data.put("orderId", dispatchDTO.getOrderId());
			data.put("ticketCode", dispatchDTO.getTicketId().getTicketCode());
			data.put("ticketTitle", dispatchDTO.getTicketId().getTicketTitle());
			data.put("workerName", dispatchDTO.getUserId().getUserFullName());
			data.put("companyName", dispatchDTO.getTicketId().getBranchId().getCompanyId().getCompanyName());
			data.put("startJob", dispatchDTO.getStartJob());
			data.put("resolutionTime", dispatchDTO.getTicketId().getSlaId().getSlaResolutionTime());
			data.put("dispatchStatus", historyRepository.getStatusByOrderId(orderId).getDispatchStatus());
			listData.add(data);
		}

		int totalList = listdispatchRepository.getTotalListDispatchFilter(search, filter);
		int totalListPage = (int) Math.ceil((listData.size() / 10) + 1);

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);
		result.put("TotalData", totalList);
		result.put("TotalPage", totalListPage);

		return result;
	}

	@GetMapping("/chartDispatch")
	public HashMap<String, Object> chartByMonth(@RequestParam String year) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		String month1 = "01";
		String month2 = "02";
		String month3 = "03";
		String month4 = "04";
		String month5 = "05";
		String month6 = "06";
		String month7 = "07";
		String month8 = "08";
		String month9 = "09";
		String month10 = "10";
		String month11 = "11";
		String month12 = "12";

		HashMap<String, Object> jan = convertDispatchToChart(month1, year);
		result.put("Januari", jan);
		HashMap<String, Object> feb = convertDispatchToChart(month2, year);
		result.put("February", feb);
		HashMap<String, Object> mar = convertDispatchToChart(month3, year);
		result.put("March", mar);
		HashMap<String, Object> apr = convertDispatchToChart(month4, year);
		result.put("April", apr);
		HashMap<String, Object> may = convertDispatchToChart(month5, year);
		result.put("May", may);
		HashMap<String, Object> jun = convertDispatchToChart(month6, year);
		result.put("June", jun);
		HashMap<String, Object> jul = convertDispatchToChart(month7, year);
		result.put("July", jul);
		HashMap<String, Object> aug = convertDispatchToChart(month8, year);
		result.put("August", aug);
		HashMap<String, Object> sep = convertDispatchToChart(month9, year);
		result.put("September", sep);
		HashMap<String, Object> oct = convertDispatchToChart(month10, year);
		result.put("October", oct);
		HashMap<String, Object> nov = convertDispatchToChart(month11, year);
		result.put("November", nov);
		HashMap<String, Object> dec = convertDispatchToChart(month12, year);
		result.put("December", dec);

		return result;
	}

	private HashMap<String, Object> convertDispatchToChart(@RequestParam String month, @RequestParam String year) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		String dispatchStatusConfirmed = "Confirmed";
		String dispatchStatusStart = "Start";
		String dispatchStatusHold = "Hold";
		String dispatchStatusFinish = "Finish";
		String dispatchStatusCanceled = "Canceled";
		String dispatchStatusReported = "Reported";
		String month1 = "01";
		String month2 = "02";
		String month3 = "03";
		String month4 = "04";
		String month5 = "05";
		String month6 = "06";
		String month7 = "07";
		String month8 = "08";
		String month9 = "09";
		String month10 = "10";
		String month11 = "11";
		String month12 = "12";
		String nameOfMonth;

		int totalDispatchConfirmed = getChartDispatch(month, dispatchStatusConfirmed, year).size();
		int totalDispatchStart = getChartDispatch(month, dispatchStatusStart, year).size();
		int totalDispatchHold = getChartDispatch(month, dispatchStatusHold, year).size();
		int totalDispatchFinish = getChartDispatch(month, dispatchStatusFinish, year).size();
		int totalDispatchCanceled = getChartDispatch(month, dispatchStatusCanceled, year).size();
		int totalDispatchReported = getChartDispatch(month, dispatchStatusReported, year).size();
		if (month == month1) {
			nameOfMonth = "January";
			data.put("nameOfMonth", nameOfMonth);
			data.put("totalDispatchConfirmed", totalDispatchConfirmed);
			data.put("totalDispatchStart", totalDispatchStart);
			data.put("totalDispatchHold", totalDispatchHold);
			data.put("totalDispatchFinish", totalDispatchFinish);
			data.put("totalDispatchCanceled", totalDispatchCanceled);
			data.put("totalDispatchReported", totalDispatchReported);
		} else if (month == month2) {
			nameOfMonth = "February";
			data.put("nameOfMonth", nameOfMonth);
			data.put("totalDispatchConfirmed", totalDispatchConfirmed);
			data.put("totalDispatchStart", totalDispatchStart);
			data.put("totalDispatchHold", totalDispatchHold);
			data.put("totalDispatchFinish", totalDispatchFinish);
			data.put("totalDispatchCanceled", totalDispatchCanceled);
			data.put("totalDispatchReported", totalDispatchReported);
		} else if (month == month3) {
			nameOfMonth = "March";
			data.put("nameOfMonth", nameOfMonth);
			data.put("totalDispatchConfirmed", totalDispatchConfirmed);
			data.put("totalDispatchStart", totalDispatchStart);
			data.put("totalDispatchHold", totalDispatchHold);
			data.put("totalDispatchFinish", totalDispatchFinish);
			data.put("totalDispatchCanceled", totalDispatchCanceled);
			data.put("totalDispatchReported", totalDispatchReported);
		} else if (month == month4) {
			nameOfMonth = "April";
			data.put("nameOfMonth", nameOfMonth);
			data.put("totalDispatchConfirmed", totalDispatchConfirmed);
			data.put("totalDispatchStart", totalDispatchStart);
			data.put("totalDispatchHold", totalDispatchHold);
			data.put("totalDispatchFinish", totalDispatchFinish);
			data.put("totalDispatchCanceled", totalDispatchCanceled);
			data.put("totalDispatchReported", totalDispatchReported);
		} else if (month == month5) {
			nameOfMonth = "May";
			data.put("nameOfMonth", nameOfMonth);
			data.put("totalDispatchConfirmed", totalDispatchConfirmed);
			data.put("totalDispatchStart", totalDispatchStart);
			data.put("totalDispatchHold", totalDispatchHold);
			data.put("totalDispatchFinish", totalDispatchFinish);
			data.put("totalDispatchCanceled", totalDispatchCanceled);
			data.put("totalDispatchReported", totalDispatchReported);
		} else if (month == month6) {
			nameOfMonth = "June";
			data.put("nameOfMonth", nameOfMonth);
			data.put("totalDispatchConfirmed", totalDispatchConfirmed);
			data.put("totalDispatchStart", totalDispatchStart);
			data.put("totalDispatchHold", totalDispatchHold);
			data.put("totalDispatchFinish", totalDispatchFinish);
			data.put("totalDispatchCanceled", totalDispatchCanceled);
			data.put("totalDispatchReported", totalDispatchReported);
		} else if (month == month7) {
			nameOfMonth = "July";
			data.put("nameOfMonth", nameOfMonth);
			data.put("totalDispatchConfirmed", totalDispatchConfirmed);
			data.put("totalDispatchStart", totalDispatchStart);
			data.put("totalDispatchHold", totalDispatchHold);
			data.put("totalDispatchFinish", totalDispatchFinish);
			data.put("totalDispatchCanceled", totalDispatchCanceled);
			data.put("totalDispatchReported", totalDispatchReported);
		} else if (month == month8) {
			nameOfMonth = "August";
			data.put("nameOfMonth", nameOfMonth);
			data.put("totalDispatchConfirmed", totalDispatchConfirmed);
			data.put("totalDispatchStart", totalDispatchStart);
			data.put("totalDispatchHold", totalDispatchHold);
			data.put("totalDispatchFinish", totalDispatchFinish);
			data.put("totalDispatchCanceled", totalDispatchCanceled);
			data.put("totalDispatchReported", totalDispatchReported);
		} else if (month == month9) {
			nameOfMonth = "September";
			data.put("nameOfMonth", nameOfMonth);
			data.put("totalDispatchConfirmed", totalDispatchConfirmed);
			data.put("totalDispatchStart", totalDispatchStart);
			data.put("totalDispatchHold", totalDispatchHold);
			data.put("totalDispatchFinish", totalDispatchFinish);
			data.put("totalDispatchCanceled", totalDispatchCanceled);
			data.put("totalDispatchReported", totalDispatchReported);
		} else if (month == month10) {
			nameOfMonth = "October";
			data.put("nameOfMonth", nameOfMonth);
			data.put("totalDispatchConfirmed", totalDispatchConfirmed);
			data.put("totalDispatchStart", totalDispatchStart);
			data.put("totalDispatchHold", totalDispatchHold);
			data.put("totalDispatchFinish", totalDispatchFinish);
			data.put("totalDispatchCanceled", totalDispatchCanceled);
			data.put("totalDispatchReported", totalDispatchReported);
		} else if (month == month11) {
			nameOfMonth = "November";
			data.put("nameOfMonth", nameOfMonth);
			data.put("totalDispatchConfirmed", totalDispatchConfirmed);
			data.put("totalDispatchStart", totalDispatchStart);
			data.put("totalDispatchHold", totalDispatchHold);
			data.put("totalDispatchFinish", totalDispatchFinish);
			data.put("totalDispatchCanceled", totalDispatchCanceled);
			data.put("totalDispatchReported", totalDispatchReported);
		} else if (month == month12) {
			nameOfMonth = "December";
			data.put("nameOfMonth", nameOfMonth);
			data.put("totalDispatchConfirmed", totalDispatchConfirmed);
			data.put("totalDispatchStart", totalDispatchStart);
			data.put("totalDispatchHold", totalDispatchHold);
			data.put("totalDispatchFinish", totalDispatchFinish);
			data.put("totalDispatchCanceled", totalDispatchCanceled);
			data.put("totalDispatchReported", totalDispatchReported);
		}
		return data;
	}

	private ArrayList<HashMap<String, Object>> getChartDispatch(@RequestParam String month, @RequestParam String status,
			@RequestParam String year) {
		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		for (Dispatch dispatch : listdispatchRepository.getChartDispatch(status, month, year)) {
			HashMap<String, Object> dataTroubleTicket = convertChartDispatchToHashMap(dispatch);
			listData.add(dataTroubleTicket);
		}
		return listData;
	}

	private HashMap<String, Object> convertChartDispatchToHashMap(Dispatch dispatch) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("orderId", dispatch.getOrderId());
		return data;
	}

	@GetMapping("averageSolvingTime")
	public HashMap<String, Object> averageSolvingTime(
			@RequestParam(value = "period", required = false) Integer period) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		Long incident = (long) 33;
		Long task = (long) 32;
		Long request = (long) 31;
		if (period == 1) {
			for (int i = 0; i < 4; i++) {
				LocalDateTime localNow = LocalDateTime.now().minusDays(1 + i);
				Timestamp dateNow = Timestamp.valueOf(localNow);
				Date date = new Date(dateNow.getTime());
				BigDecimal avgIncident = listdispatchRepository.averageSolvingTime(incident, date);
				BigDecimal avgTask = listdispatchRepository.averageSolvingTime(task, date);
				BigDecimal avgRequest = listdispatchRepository.averageSolvingTime(request, date);
				ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
				HashMap<String, Object> data = new HashMap<String, Object>();
				data.put("avgIncident", avgIncident);
				data.put("avgTask", avgTask);
				data.put("avgRequest", avgRequest);
				listData.add(data);

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				String strDate = formatter.format(date);

				result.put(strDate, listData);
			}
		} else if (period == 2) {
			for (int i = 0; i < 7; i++) {
				LocalDateTime localNow = LocalDateTime.now().minusDays(1 + i);
				Timestamp dateNow = Timestamp.valueOf(localNow);
				Date date = new Date(dateNow.getTime());
				BigDecimal avgIncident = listdispatchRepository.averageSolvingTime(incident, date);
				BigDecimal avgTask = listdispatchRepository.averageSolvingTime(task, date);
				BigDecimal avgRequest = listdispatchRepository.averageSolvingTime(request, date);
				ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
				HashMap<String, Object> data = new HashMap<String, Object>();
				data.put("avgIncident", avgIncident);
				data.put("avgTask", avgTask);
				data.put("avgRequest", avgRequest);
				listData.add(data);

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
				String strDate = formatter.format(date);

				result.put(strDate, listData);
			}
		}

		return result;
	}

	@GetMapping("avgSolvingTime")
	public HashMap<String, Object> avgSolvingTime(@RequestParam(value = "period", required = false) Integer period) {
		HashMap<String, Object> listdata = new HashMap<String, Object>();
		Long incident = (long) 33;
		Long task = (long) 32;
		Long request = (long) 31;

		if (period == 1) {
			LocalDateTime localNow3 = LocalDateTime.now().minusDays(3);
			Timestamp dateNow3 = Timestamp.valueOf(localNow3);
			Date date3 = new Date(dateNow3.getTime());
			BigDecimal avgIncident3 = listdispatchRepository.averageSolvingTime(incident, date3);
			BigDecimal avgTask3 = listdispatchRepository.averageSolvingTime(task, date3);
			BigDecimal avgRequest3 = listdispatchRepository.averageSolvingTime(request, date3);

			HashMap<String, Object> data3 = new HashMap<String, Object>();
			SimpleDateFormat formatter3 = new SimpleDateFormat("yyyy-MM-dd");
			String strDate3 = formatter3.format(date3);
			data3.put("date", strDate3);
			data3.put("avgIncident", avgIncident3);
			data3.put("avgTask", avgTask3);
			data3.put("avgRequest", avgRequest3);

			LocalDateTime localNow2 = LocalDateTime.now().minusDays(2);
			Timestamp dateNow2 = Timestamp.valueOf(localNow2);
			Date date2 = new Date(dateNow2.getTime());
			BigDecimal avgIncident2 = listdispatchRepository.averageSolvingTime(incident, date2);
			BigDecimal avgTask2 = listdispatchRepository.averageSolvingTime(task, date2);
			BigDecimal avgRequest2 = listdispatchRepository.averageSolvingTime(request, date2);

			HashMap<String, Object> data2 = new HashMap<String, Object>();
			SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
			String strDate2 = formatter2.format(date2);
			data2.put("date", strDate2);
			data2.put("avgIncident", avgIncident2);
			data2.put("avgTask", avgTask2);
			data2.put("avgRequest", avgRequest2);

			LocalDateTime localNow1 = LocalDateTime.now().minusDays(1);
			Timestamp dateNow1 = Timestamp.valueOf(localNow1);
			Date date1 = new Date(dateNow1.getTime());
			BigDecimal avgIncident1 = listdispatchRepository.averageSolvingTime(incident, date1);
			BigDecimal avgTask1 = listdispatchRepository.averageSolvingTime(task, date1);
			BigDecimal avgRequest1 = listdispatchRepository.averageSolvingTime(request, date1);

			HashMap<String, Object> data1 = new HashMap<String, Object>();
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
			String strDate1 = formatter1.format(date1);
			data1.put("date", strDate1);
			data1.put("avgIncident", avgIncident1);
			data1.put("avgTask", avgTask1);
			data1.put("avgRequest", avgRequest1);

			LocalDateTime localNow = LocalDateTime.now();
			Timestamp dateNow = Timestamp.valueOf(localNow);
			Date date = new Date(dateNow.getTime());
			BigDecimal avgIncident = listdispatchRepository.averageSolvingTime(incident, date);
			BigDecimal avgTask = listdispatchRepository.averageSolvingTime(task, date);
			BigDecimal avgRequest = listdispatchRepository.averageSolvingTime(request, date);

			HashMap<String, Object> data = new HashMap<String, Object>();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String strDate = formatter.format(date);
			data.put("date", strDate);
			data.put("avgIncident", avgIncident);
			data.put("avgTask", avgTask);
			data.put("avgRequest", avgRequest);

			listdata.put("day3", data3);
			listdata.put("day2", data2);
			listdata.put("day1", data1);
			listdata.put("day0", data);
		}

		return listdata;
	}

	@GetMapping("averageSolvingTimeCustom")
	public ArrayList<HashMap<String, Object>> averageSolvingTimeCustom(@RequestParam java.sql.Date startDate,
			@RequestParam java.sql.Date endDate) {
		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		Long incident = (long) 33;
		Long task = (long) 32;
		Long request = (long) 31;
		BigDecimal avgIncident = listdispatchRepository.averageSolvingTimeCustom(incident, startDate, endDate);
		BigDecimal avgTask = listdispatchRepository.averageSolvingTimeCustom(task, startDate, endDate);
		BigDecimal avgRequest = listdispatchRepository.averageSolvingTimeCustom(request, startDate, endDate);

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("avgIncident", avgIncident);
		data.put("avgTask", avgTask);
		data.put("avgRequest", avgRequest);
		listData.add(data);

		return listData;

	}

	@GetMapping("averageResponseTime")
	public ArrayList<HashMap<String, Object>> averageResponseTime(
			@RequestParam(value = "period", required = false) Integer period) {
		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		Long priorityLow = (long) 19;
		Long priorityMedium = (long) 20;
		Long priorityHigh = (long) 21;
		Long priorityUrgent = (long) 22;
		if (period == 1) {
			LocalDateTime localNow = LocalDateTime.now().minusDays(4);
			;
			Timestamp dateNow = Timestamp.valueOf(localNow);
			Date date = new Date(dateNow.getTime());
			BigDecimal avgPriorityLow = listdispatchRepository.averageResponseTime(priorityLow, date);
			BigDecimal avgPriorityMedium = listdispatchRepository.averageResponseTime(priorityMedium, date);
			BigDecimal avgPriorityHigh = listdispatchRepository.averageResponseTime(priorityHigh, date);
			BigDecimal avgPriorityUrgent = listdispatchRepository.averageResponseTime(priorityUrgent, date);

			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("avgPriorityLow", avgPriorityLow);
			data.put("avgPriorityMedium", avgPriorityMedium);
			data.put("avgPriorityHigh", avgPriorityHigh);
			data.put("avgPriorityUrgent", avgPriorityUrgent);
			listData.add(data);
		} else if (period == 2) {
			LocalDateTime localNow = LocalDateTime.now().minusWeeks(1);
			Timestamp dateNow = Timestamp.valueOf(localNow);
			Date date = new Date(dateNow.getTime());
			BigDecimal avgPriorityLow = listdispatchRepository.averageResponseTime(priorityLow, date);
			BigDecimal avgPriorityMedium = listdispatchRepository.averageResponseTime(priorityMedium, date);
			BigDecimal avgPriorityHigh = listdispatchRepository.averageResponseTime(priorityHigh, date);
			BigDecimal avgPriorityUrgent = listdispatchRepository.averageResponseTime(priorityUrgent, date);

			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("avgPriorityLow", avgPriorityLow);
			data.put("avgPriorityMedium", avgPriorityMedium);
			data.put("avgPriorityHigh", avgPriorityHigh);
			data.put("avgPriorityUrgent", avgPriorityUrgent);
			listData.add(data);
		} else if (period == 3) {
			LocalDateTime localNow = LocalDateTime.now().minusMonths(1);
			Timestamp dateNow = Timestamp.valueOf(localNow);
			Date date = new Date(dateNow.getTime());
			BigDecimal avgPriorityLow = listdispatchRepository.averageResponseTime(priorityLow, date);
			BigDecimal avgPriorityMedium = listdispatchRepository.averageResponseTime(priorityMedium, date);
			BigDecimal avgPriorityHigh = listdispatchRepository.averageResponseTime(priorityHigh, date);
			BigDecimal avgPriorityUrgent = listdispatchRepository.averageResponseTime(priorityUrgent, date);

			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("avgPriorityLow", avgPriorityLow);
			data.put("avgPriorityMedium", avgPriorityMedium);
			data.put("avgPriorityHigh", avgPriorityHigh);
			data.put("avgPriorityUrgent", avgPriorityUrgent);
			listData.add(data);
		} else if (period == 4) {
			LocalDateTime localNow = LocalDateTime.now().minusYears(1);
			Timestamp dateNow = Timestamp.valueOf(localNow);
			Date date = new Date(dateNow.getTime());
			BigDecimal avgPriorityLow = listdispatchRepository.averageResponseTime(priorityLow, date);
			BigDecimal avgPriorityMedium = listdispatchRepository.averageResponseTime(priorityMedium, date);
			BigDecimal avgPriorityHigh = listdispatchRepository.averageResponseTime(priorityHigh, date);
			BigDecimal avgPriorityUrgent = listdispatchRepository.averageResponseTime(priorityUrgent, date);

			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("avgPriorityLow", avgPriorityLow);
			data.put("avgPriorityMedium", avgPriorityMedium);
			data.put("avgPriorityHigh", avgPriorityHigh);
			data.put("avgPriorityUrgent", avgPriorityUrgent);
			listData.add(data);
		} else if (period == 5) {
			LocalDateTime localNow = LocalDateTime.now().minusMonths(2);
			Timestamp dateNow = Timestamp.valueOf(localNow);
			Date date = new Date(dateNow.getTime());
			BigDecimal avgPriorityLow = listdispatchRepository.averageResponseTime(priorityLow, date);
			BigDecimal avgPriorityMedium = listdispatchRepository.averageResponseTime(priorityMedium, date);
			BigDecimal avgPriorityHigh = listdispatchRepository.averageResponseTime(priorityHigh, date);
			BigDecimal avgPriorityUrgent = listdispatchRepository.averageResponseTime(priorityUrgent, date);

			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("avgPriorityLow", avgPriorityLow);
			data.put("avgPriorityMedium", avgPriorityMedium);
			data.put("avgPriorityHigh", avgPriorityHigh);
			data.put("avgPriorityUrgent", avgPriorityUrgent);
			listData.add(data);
		} else if (period == 6) {
			LocalDateTime localNow = LocalDateTime.now().minusMonths(6);
			Timestamp dateNow = Timestamp.valueOf(localNow);
			Date date = new Date(dateNow.getTime());
			BigDecimal avgPriorityLow = listdispatchRepository.averageResponseTime(priorityLow, date);
			BigDecimal avgPriorityMedium = listdispatchRepository.averageResponseTime(priorityMedium, date);
			BigDecimal avgPriorityHigh = listdispatchRepository.averageResponseTime(priorityHigh, date);
			BigDecimal avgPriorityUrgent = listdispatchRepository.averageResponseTime(priorityUrgent, date);

			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("avgPriorityLow", avgPriorityLow);
			data.put("avgPriorityMedium", avgPriorityMedium);
			data.put("avgPriorityHigh", avgPriorityHigh);
			data.put("avgPriorityUrgent", avgPriorityUrgent);
			listData.add(data);
		}

		return listData;
	}

	@GetMapping("averageResponseTimeCustom")
	public ArrayList<HashMap<String, Object>> averageResponseTimeCustom(@RequestParam java.sql.Date startDate,
			@RequestParam java.sql.Date endDate) {
		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		Long priorityLow = (long) 19;
		Long priorityMedium = (long) 20;
		Long priorityHigh = (long) 21;
		Long priorityUrgent = (long) 22;
		BigDecimal avgPriorityLow = listdispatchRepository.averageResponseTimeCustom(priorityLow, startDate, endDate);
		BigDecimal avgPriorityMedium = listdispatchRepository.averageResponseTimeCustom(priorityMedium, startDate,
				endDate);
		BigDecimal avgPriorityHigh = listdispatchRepository.averageResponseTimeCustom(priorityHigh, startDate, endDate);
		BigDecimal avgPriorityUrgent = listdispatchRepository.averageResponseTimeCustom(priorityUrgent, startDate,
				endDate);

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("avgPriorityLow", avgPriorityLow);
		data.put("avgPriorityMedium", avgPriorityMedium);
		data.put("avgPriorityHigh", avgPriorityHigh);
		data.put("avgPriorityUrgent", avgPriorityUrgent);
		listData.add(data);

		return listData;

	}
}
