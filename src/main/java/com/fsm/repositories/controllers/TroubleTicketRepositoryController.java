package com.fsm.repositories.controllers;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.TroubleTicketDTO;
import com.fsm.models.Dispatch;
import com.fsm.models.DispatchReport;
import com.fsm.models.History;
import com.fsm.models.TroubleTicket;
import com.fsm.repositories.CodeRepository;
import com.fsm.repositories.DispatchReportRepository;
import com.fsm.repositories.DispatchRepository;
import com.fsm.repositories.HistoryRepository;
import com.fsm.repositories.JobCategoryReportRepository;
import com.fsm.repositories.TroubleTicketRepository;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.fsm.interfaces.Minio;

@RestController
@RequestMapping("troubleTicket")
public class TroubleTicketRepositoryController extends Minio {

	@Autowired
	TroubleTicketRepository troubleticketRepository;

	@Autowired
	DispatchRepository dispatchRepository;

	@Autowired
	HistoryRepository historyRepository;

	@Autowired
	CodeRepository codeRepository;

	@Autowired
	JobCategoryReportRepository jobCategoryReportRepository;

	@Autowired
	DispatchReportRepository dispatchReportRepository;

	ModelMapper modelMapper = new ModelMapper();

	public TroubleTicketDTO convertToDTO(TroubleTicket troubleticket) {
		TroubleTicketDTO troubleTicketDTO = modelMapper.map(troubleticket, TroubleTicketDTO.class);
		return troubleTicketDTO;
	}

	@PostMapping("/create")
	public HashMap<String, Object> createTicket(@Valid @RequestBody TroubleTicket troubleticket) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		TroubleTicketDTO troubleTicketDTO = convertToDTO(troubleticket);

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		SimpleDateFormat formatter = new SimpleDateFormat("ddMMyy");
		String strDate = formatter.format(dateNow);

		String ticketCode = "IN" + strDate;

		troubleticketRepository.saveTicket(troubleticket.getTicketStatusId(), troubleticket.getCategoryId(),
				troubleticket.getBranchId().getBranchId(), troubleticket.getSlaId().getSlaId(),
				troubleticket.getJobId().getJobId(), troubleticket.getPicId().getPicId(),
				troubleticket.getTicketTitle().trim(), troubleticket.getTicketDescription().trim(),
				troubleticket.getTicketDurationTime(), troubleticket.getCreatedBy(), troubleticket.getLastModifiedBy(),
				ticketCode, troubleticket.getFileName(), troubleticket.getFilePath(), troubleticket.getPriorityId());
		troubleticketRepository.getTicketDueDate();
		showHashMap.put("Message", "Ticket Berhasil Dibuat");
		showHashMap.put("Data", troubleTicketDTO);
		showHashMap.put("Status", HttpStatus.OK);

		return showHashMap;
	}

	@PutMapping("update/{id}")
	public HashMap<String, Object> updateTicket(@Valid @PathVariable("id") Long id,
			@RequestBody TroubleTicket updateTroubleTicket) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		TroubleTicket troubleTicket = troubleticketRepository.findById(id).orElse(null);

		String timestamp = troubleTicket.getTicketDate() + "T" + troubleTicket.getTicketTime();
		LocalDateTime ticketDateTime = LocalDateTime.parse(timestamp);
		Timestamp ticketTimestamp = Timestamp.valueOf(ticketDateTime);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(ticketTimestamp);
		calendar.add(Calendar.HOUR, updateTroubleTicket.getTicketDurationTime().intValue());
		Timestamp ticketDueDate = new Timestamp(calendar.getTimeInMillis());

		troubleTicket.setTicketTitle(updateTroubleTicket.getTicketTitle());
		troubleTicket.setPriorityId(updateTroubleTicket.getPriorityId());
		troubleTicket.setPicId(updateTroubleTicket.getPicId());
		troubleTicket.setCategoryId(updateTroubleTicket.getCategoryId());
		troubleTicket.setJobId(updateTroubleTicket.getJobId());
		troubleTicket.setTicketDurationTime(updateTroubleTicket.getTicketDurationTime());
		troubleTicket.setTicketDueDate(ticketDueDate);
		troubleTicket.setTicketDescription(updateTroubleTicket.getTicketDescription());
		troubleTicket.setFileName(updateTroubleTicket.getFileName());
		troubleTicket.setFilePath(updateTroubleTicket.getFilePath());
		troubleTicket.setLastModifiedBy(updateTroubleTicket.getLastModifiedBy());
		troubleTicket.setLastModifiedOn(dateNow);

		troubleticketRepository.save(troubleTicket);

		showHashMap.put("Message", "Ticket Berhasil Diubah");
		showHashMap.put("Data", troubleTicket);
		showHashMap.put("Status", HttpStatus.OK);

		return showHashMap;
	}

	@PutMapping("delete/{id}")
	public HashMap<String, Object> deleteTicket(@Valid @PathVariable("id") Long id,
			@RequestBody TroubleTicket deleteTroubleTicket) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		TroubleTicket troubleTicket = troubleticketRepository.findById(id).orElse(null);

		troubleTicket.setDeleted(true);
		troubleTicket.setLastModifiedBy(deleteTroubleTicket.getLastModifiedBy());
		troubleTicket.setLastModifiedOn(dateNow);

		troubleticketRepository.save(troubleTicket);

		showHashMap.put("Message", "Ticket Berhasil Dihapus");
		showHashMap.put("Data", troubleTicket);
		showHashMap.put("Status", HttpStatus.OK);

		return showHashMap;
	}

	@GetMapping("/ticket/report")
	public List<Object[]> getLoadDataWorkOrderAll(@RequestParam(defaultValue = 1 + "") Integer period) {
		List<Object[]> Obj = new ArrayList<Object[]>();
		if (period == 1) {
			Obj = this.troubleticketRepository.getLoadDataWorkOrderToday();
		} else if (period == 2) {
			Obj = this.troubleticketRepository.getLoadDataWorkOrderThisWeek();
		} else if (period == 3) {
			Obj = this.troubleticketRepository.getLoadDataWorkOrderThisMonth();
		} else if (period == 4) {
			Obj = this.troubleticketRepository.getLoadDataWorkOrderThisYear();
		} else if (period == 5) {
			Obj = this.troubleticketRepository.getLoadDataWorkOrderLast2Month();
		} else if (period == 6) {
			Obj = this.troubleticketRepository.getLoadDataWorkOrderLast6Month();
		}
		return Obj;
	}

	@GetMapping("/report_custom")
	public List<Object> getLoadDataWorkOrderCustoms(@RequestParam java.sql.Date start_date,
			@RequestParam java.sql.Date end_date) {
		return this.troubleticketRepository.getLoadDataWorkOrderCustom(start_date, end_date);
	}

	@GetMapping("getRowCountByDate")
	public long getRowCountByDate() {
		return this.troubleticketRepository.getRowCountByDate();
	}

	@GetMapping("listTroubleTicketOpen")
	public HashMap<String, Object> listTroubleTicket(Pageable pageable) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();
		ArrayList<TroubleTicket> listTroubleTicketEntity = (ArrayList<TroubleTicket>) troubleticketRepository
				.getTicketListOpen(pageable);

		for (TroubleTicket item : listTroubleTicketEntity) {
			HashMap<String, Object> data = new HashMap<>();

			TroubleTicketDTO troubleTicketDTO = modelMapper.map(item, TroubleTicketDTO.class);

			long ticketStatusId = troubleTicketDTO.getTicketStatusId();
			data.put("ticketId", troubleTicketDTO.getTicketId());
			data.put("ticketCode", troubleTicketDTO.getTicketCode());
			data.put("ticketTitle", troubleTicketDTO.getTicketTitle());
			data.put("jobId", troubleTicketDTO.getJobId().getJobId());
			data.put("companyName", troubleTicketDTO.getBranchId().getCompanyId().getCompanyName());
			data.put("status", codeRepository.findById(ticketStatusId).orElse(null).getCodeName());
			listData.add(data);
		}
		int totalList = troubleticketRepository.getTotalTicketListOpen();

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);
		result.put("TotalData", totalList);

		return result;
	}

	@GetMapping("listTroubleTicket")
	public HashMap<String, Object> listTroubleTicket(@RequestParam String search, Pageable pageable) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();
		ArrayList<TroubleTicket> listTroubleTicketEntity = (ArrayList<TroubleTicket>) troubleticketRepository
				.getListTicket(search, pageable);

		for (TroubleTicket item : listTroubleTicketEntity) {
			HashMap<String, Object> data = new HashMap<>();

			TroubleTicketDTO troubleTicketDTO = modelMapper.map(item, TroubleTicketDTO.class);

			Long ticketId = troubleTicketDTO.getTicketId();
			Long ticketStatusId = troubleTicketDTO.getTicketStatusId();
			Long jobCategoryId = troubleTicketDTO.getJobId().getJobCategoryId().getJobCategoryId();
			Long reportId = jobCategoryReportRepository.findByJobCategoryId(jobCategoryId).getReportId();
			data.put("ticketId", troubleTicketDTO.getTicketId());
			data.put("ticketCode", troubleTicketDTO.getTicketCode());
			data.put("ticketTitle", troubleTicketDTO.getTicketTitle());
			data.put("companyName", troubleTicketDTO.getBranchId().getCompanyId().getCompanyName());
			data.put("slaResolutionTime", troubleTicketDTO.getSlaId().getSlaResolutionTime() + " Hours");
			data.put("ticketStatus", codeRepository.findById(ticketStatusId).orElse(null).getCodeName());
			data.put("fileName", troubleTicketDTO.getFileName());
			data.put("filePath", troubleTicketDTO.getFilePath());
			data.put("reportName", codeRepository.findById(reportId).orElse(null).getCodeName());
			Dispatch dispatch = dispatchRepository.findByTicketId(ticketId);
			if (dispatch != null) {
				data.put("workerName", dispatch.getUserId().getUserFullName());
				data.put("startJob", dispatch.getStartJob() != null ? dispatch.getStartJob() : "-");
			} else {
				data.put("workerName", "-");
				data.put("startJob", "-");
			}
			listData.add(data);
		}

		int totalList = troubleticketRepository.getTotalListTicket(search);
		int totalListPage = (int) Math.ceil((listData.size() / 10) + 1);

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);
		result.put("TotalData", totalList);
		result.put("TotalPage", totalListPage);

		return result;
	}

	@GetMapping("listTroubleTicketFilter")
	public HashMap<String, Object> listTroubleTicketFilter(@RequestParam Long filter, @RequestParam String search,
			Pageable pageable) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();
		ArrayList<TroubleTicket> listTroubleTicketEntity = (ArrayList<TroubleTicket>) troubleticketRepository
				.getListTicketFilter(filter, search, pageable);

		for (TroubleTicket item : listTroubleTicketEntity) {
			HashMap<String, Object> data = new HashMap<>();

			TroubleTicketDTO troubleTicketDTO = modelMapper.map(item, TroubleTicketDTO.class);

			Long ticketId = troubleTicketDTO.getTicketId();
			Long ticketStatusId = troubleTicketDTO.getTicketStatusId();
			Long jobCategoryId = troubleTicketDTO.getJobId().getJobCategoryId().getJobCategoryId();
			Long reportId = jobCategoryReportRepository.findByJobCategoryId(jobCategoryId).getReportId();
			data.put("ticketId", troubleTicketDTO.getTicketId());
			data.put("ticketCode", troubleTicketDTO.getTicketCode());
			data.put("ticketTitle", troubleTicketDTO.getTicketTitle());
			data.put("companyName", troubleTicketDTO.getBranchId().getCompanyId().getCompanyName());
			data.put("slaResolutionTime", troubleTicketDTO.getSlaId().getSlaResolutionTime());
			data.put("ticketStatus", codeRepository.findById(ticketStatusId).orElse(null).getCodeName());
			data.put("fileName", troubleTicketDTO.getFileName());
			data.put("filePath", troubleTicketDTO.getFilePath());
			data.put("reportName", codeRepository.findById(reportId).orElse(null).getCodeName());
			Dispatch dispatch = dispatchRepository.findByTicketId(ticketId);
			if (dispatch != null) {
				data.put("workerName", dispatch.getUserId().getUserFullName());
				data.put("startJob", dispatch.getStartJob() != null ? dispatch.getStartJob() : "-");
			} else {
				data.put("workerName", "-");
				data.put("startJob", "-");
			}
			listData.add(data);
		}

		int totalList = troubleticketRepository.getTotalListTicketFilter(filter, search);
		int totalListPage = (int) Math.ceil((listData.size() / 10) + 1);

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);
		result.put("TotalData", totalList);
		result.put("TotalPage", totalListPage);

		return result;
	}

	@GetMapping("listTroubleTicketCancel")
	public HashMap<String, Object> listTroubleTicketCancel(@RequestParam String search, Pageable pageable) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();
		ArrayList<TroubleTicket> listTroubleTicketEntity = (ArrayList<TroubleTicket>) troubleticketRepository
				.getAllTroubleTicketCancel(search, pageable);

		for (TroubleTicket item : listTroubleTicketEntity) {
			HashMap<String, Object> data = new HashMap<>();

			TroubleTicketDTO troubleTicketDTO = modelMapper.map(item, TroubleTicketDTO.class);

			Long ticketId = troubleTicketDTO.getTicketId();
			data.put("ticketId", troubleTicketDTO.getTicketId());
			data.put("ticketCode", troubleTicketDTO.getTicketCode());
			data.put("ticketTitle", troubleTicketDTO.getTicketTitle());
			data.put("companyName", troubleTicketDTO.getBranchId().getCompanyId().getCompanyName());
			;
			History history = historyRepository.getHistoryCanceledByTicketId(ticketId);
			if (history != null) {
				data.put("reason", history.getReason());
				data.put("orderId", history.getOrderId().getOrderId());
			} else {
				data.put("reason", "-");
				data.put("orderId", null);
			}
			listData.add(data);
		}

		int totalList = troubleticketRepository.getTotalTroubleTicketCancel(search);
		int totalListPage = (int) Math.ceil((listData.size() / 10) + 1);

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);
		result.put("TotalData", totalList);
		result.put("TotalPage", totalListPage);

		return result;
	}

	@GetMapping("listTroubleTicketFinish")
	public HashMap<String, Object> listTroubleTicketFinish(@RequestParam String search, Pageable pageable) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();
		ArrayList<TroubleTicket> listTroubleTicketEntity = (ArrayList<TroubleTicket>) troubleticketRepository
				.getAllTroubleTicketFinish(search, pageable);

		for (TroubleTicket item : listTroubleTicketEntity) {
			HashMap<String, Object> data = new HashMap<>();

			TroubleTicketDTO troubleTicketDTO = modelMapper.map(item, TroubleTicketDTO.class);

			Long ticketId = troubleTicketDTO.getTicketId();
			data.put("ticketId", troubleTicketDTO.getTicketId());
			data.put("ticketCode", troubleTicketDTO.getTicketCode());
			data.put("ticketTitle", troubleTicketDTO.getTicketTitle());
			data.put("companyName", troubleTicketDTO.getBranchId().getCompanyId().getCompanyName());
			Dispatch dispatch = dispatchRepository.findReportByTicketId(ticketId);
			if (dispatch != null) {
				Long orderId = dispatch.getOrderId();
				data.put("orderId", orderId);
				data.put("workerName", dispatch.getUserId().getUserFullName());
				data.put("startJob", dispatch.getStartJob() != null ? dispatch.getStartJob() : "-");
				data.put("endJob", dispatch.getEndJob() != null ? dispatch.getEndJob() : "-");
				DispatchReport dispatchReport = dispatchReportRepository.findByOrderId(orderId);
				if (dispatchReport != null) {
					data.put("dispatchReportRating", dispatchReport.getDispatchReportRating());
				} else {
					data.put("dispatchReportRating", 0);
				}
			} else {
				data.put("orderId", null);
				data.put("workerName", "-");
				data.put("startJob", "-");
				data.put("endJob", "-");

			}
			listData.add(data);
		}

		int totalList = troubleticketRepository.getTotalTroubleTicketFinish(search);
		int totalListPage = (int) Math.ceil((listData.size() / 10) + 1);

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);
		result.put("TotalData", totalList);
		result.put("TotalPage", totalListPage);

		return result;
	}

	@GetMapping("listTroubleTicketFinishFilter")
	public HashMap<String, Object> listTroubleTicketFinishFilter(@RequestParam Integer filter,
			@RequestParam String search, Pageable pageable) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();
		ArrayList<TroubleTicket> listTroubleTicketEntity = (ArrayList<TroubleTicket>) troubleticketRepository
				.getAllTroubleTicketFinishFilter(filter, search, pageable);

		for (TroubleTicket item : listTroubleTicketEntity) {
			HashMap<String, Object> data = new HashMap<>();

			TroubleTicketDTO troubleTicketDTO = modelMapper.map(item, TroubleTicketDTO.class);

			Long ticketId = troubleTicketDTO.getTicketId();
			data.put("ticketId", troubleTicketDTO.getTicketId());
			data.put("ticketCode", troubleTicketDTO.getTicketCode());
			data.put("ticketTitle", troubleTicketDTO.getTicketTitle());
			data.put("companyName", troubleTicketDTO.getBranchId().getCompanyId().getCompanyName());
			Dispatch dispatch = dispatchRepository.findReportByTicketId(ticketId);
			if (dispatch != null) {
				Long orderId = dispatch.getOrderId();
				data.put("orderId", orderId);
				data.put("workerName", dispatch.getUserId().getUserFullName());
				data.put("startJob", dispatch.getStartJob() != null ? dispatch.getStartJob() : "-");
				data.put("endJob", dispatch.getEndJob() != null ? dispatch.getEndJob() : "-");
				DispatchReport dispatchReport = dispatchReportRepository.findByOrderId(orderId);
				if (dispatchReport != null) {
					data.put("dispatchReportRating", dispatchReport.getDispatchReportRating());
				} else {
					data.put("dispatchReportRating", 0);
				}
			} else {
				data.put("orderId", null);
				data.put("workerName", "-");
				data.put("startJob", "-");
				data.put("endJob", "-");

			}
			listData.add(data);
		}

		int totalList = troubleticketRepository.getTotalTroubleTicketFinishFilter(filter, search);
		int totalListPage = (int) Math.ceil((listData.size() / 10) + 1);

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);
		result.put("TotalData", totalList);
		result.put("TotalPage", totalListPage);

		return result;
	}

	@GetMapping("totalStatusTicket")
	public ArrayList<HashMap<String, Object>> totalStatusTicket(
			@RequestParam(value = "period", required = false) Integer period) {
		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		Long open = (long) 8;
		Long dispatch = (long) 9;
		Long inprogress = (long) 10;
		Long hold = (long) 11;
		Long finish = (long) 12;
		Long cancel = (long) 13;
		Long finishReported = (long) 30;
		if (period == 1) {
			LocalDateTime localNow = LocalDateTime.now();
			Timestamp dateNow = Timestamp.valueOf(localNow);
			Date date = new Date(dateNow.getTime());
			int totalTicketOpen = troubleticketRepository.getTotalStatus(open, date);
			int totalTicketDispatch = troubleticketRepository.getTotalStatus(dispatch, date);
			int totalTicketInProgress = troubleticketRepository.getTotalStatus(inprogress, date);
			int totalTicketHold = troubleticketRepository.getTotalStatus(hold, date);
			int totalTicketFinish = troubleticketRepository.getTotalStatus(finish, date);
			int totalTicketCancel = troubleticketRepository.getTotalStatus(cancel, date);
			int totalTicketFinishReported = troubleticketRepository.getTotalStatus(finishReported, date);

			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("totalTicketOpen", totalTicketOpen);
			data.put("totalTicketDispatch", totalTicketDispatch);
			data.put("totalTicketInProgress", totalTicketInProgress);
			data.put("totalTicketHold", totalTicketHold);
			data.put("totalTicketFinish", totalTicketFinish);
			data.put("totalTicketCancel", totalTicketCancel);
			data.put("totalTicketFinishReported", totalTicketFinishReported);
			listData.add(data);
		} else if (period == 2) {
			LocalDateTime localNow = LocalDateTime.now().minusWeeks(1);
			Timestamp dateNow = Timestamp.valueOf(localNow);
			Date date = new Date(dateNow.getTime());
			int totalTicketOpen = troubleticketRepository.getTotalStatus(open, date);
			int totalTicketDispatch = troubleticketRepository.getTotalStatus(dispatch, date);
			int totalTicketInProgress = troubleticketRepository.getTotalStatus(inprogress, date);
			int totalTicketHold = troubleticketRepository.getTotalStatus(hold, date);
			int totalTicketFinish = troubleticketRepository.getTotalStatus(finish, date);
			int totalTicketCancel = troubleticketRepository.getTotalStatus(cancel, date);
			int totalTicketFinishReported = troubleticketRepository.getTotalStatus(finishReported, date);

			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("totalTicketOpen", totalTicketOpen);
			data.put("totalTicketDispatch", totalTicketDispatch);
			data.put("totalTicketInProgress", totalTicketInProgress);
			data.put("totalTicketHold", totalTicketHold);
			data.put("totalTicketFinish", totalTicketFinish);
			data.put("totalTicketCancel", totalTicketCancel);
			data.put("totalTicketFinishReported", totalTicketFinishReported);
			listData.add(data);
		} else if (period == 3) {
			LocalDateTime localNow = LocalDateTime.now().minusMonths(1);
			Timestamp dateNow = Timestamp.valueOf(localNow);
			Date date = new Date(dateNow.getTime());
			int totalTicketOpen = troubleticketRepository.getTotalStatus(open, date);
			int totalTicketDispatch = troubleticketRepository.getTotalStatus(dispatch, date);
			int totalTicketInProgress = troubleticketRepository.getTotalStatus(inprogress, date);
			int totalTicketHold = troubleticketRepository.getTotalStatus(hold, date);
			int totalTicketFinish = troubleticketRepository.getTotalStatus(finish, date);
			int totalTicketCancel = troubleticketRepository.getTotalStatus(cancel, date);
			int totalTicketFinishReported = troubleticketRepository.getTotalStatus(finishReported, date);

			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("totalTicketOpen", totalTicketOpen);
			data.put("totalTicketDispatch", totalTicketDispatch);
			data.put("totalTicketInProgress", totalTicketInProgress);
			data.put("totalTicketHold", totalTicketHold);
			data.put("totalTicketFinish", totalTicketFinish);
			data.put("totalTicketCancel", totalTicketCancel);
			data.put("totalTicketFinishReported", totalTicketFinishReported);
			listData.add(data);
		} else if (period == 4) {
			LocalDateTime localNow = LocalDateTime.now().minusYears(1);
			Timestamp dateNow = Timestamp.valueOf(localNow);
			Date date = new Date(dateNow.getTime());
			int totalTicketOpen = troubleticketRepository.getTotalStatus(open, date);
			int totalTicketDispatch = troubleticketRepository.getTotalStatus(dispatch, date);
			int totalTicketInProgress = troubleticketRepository.getTotalStatus(inprogress, date);
			int totalTicketHold = troubleticketRepository.getTotalStatus(hold, date);
			int totalTicketFinish = troubleticketRepository.getTotalStatus(finish, date);
			int totalTicketCancel = troubleticketRepository.getTotalStatus(cancel, date);
			int totalTicketFinishReported = troubleticketRepository.getTotalStatus(finishReported, date);

			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("totalTicketOpen", totalTicketOpen);
			data.put("totalTicketDispatch", totalTicketDispatch);
			data.put("totalTicketInProgress", totalTicketInProgress);
			data.put("totalTicketHold", totalTicketHold);
			data.put("totalTicketFinish", totalTicketFinish);
			data.put("totalTicketCancel", totalTicketCancel);
			data.put("totalTicketFinishReported", totalTicketFinishReported);
			listData.add(data);
		} else if (period == 5) {
			LocalDateTime localNow = LocalDateTime.now().minusMonths(2);
			Timestamp dateNow = Timestamp.valueOf(localNow);
			Date date = new Date(dateNow.getTime());
			int totalTicketOpen = troubleticketRepository.getTotalStatus(open, date);
			int totalTicketDispatch = troubleticketRepository.getTotalStatus(dispatch, date);
			int totalTicketInProgress = troubleticketRepository.getTotalStatus(inprogress, date);
			int totalTicketHold = troubleticketRepository.getTotalStatus(hold, date);
			int totalTicketFinish = troubleticketRepository.getTotalStatus(finish, date);
			int totalTicketCancel = troubleticketRepository.getTotalStatus(cancel, date);
			int totalTicketFinishReported = troubleticketRepository.getTotalStatus(finishReported, date);

			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("totalTicketOpen", totalTicketOpen);
			data.put("totalTicketDispatch", totalTicketDispatch);
			data.put("totalTicketInProgress", totalTicketInProgress);
			data.put("totalTicketHold", totalTicketHold);
			data.put("totalTicketFinish", totalTicketFinish);
			data.put("totalTicketCancel", totalTicketCancel);
			data.put("totalTicketFinishReported", totalTicketFinishReported);
			listData.add(data);
		} else if (period == 6) {
			LocalDateTime localNow = LocalDateTime.now().minusMonths(6);
			Timestamp dateNow = Timestamp.valueOf(localNow);
			Date date = new Date(dateNow.getTime());
			int totalTicketOpen = troubleticketRepository.getTotalStatus(open, date);
			int totalTicketDispatch = troubleticketRepository.getTotalStatus(dispatch, date);
			int totalTicketInProgress = troubleticketRepository.getTotalStatus(inprogress, date);
			int totalTicketHold = troubleticketRepository.getTotalStatus(hold, date);
			int totalTicketFinish = troubleticketRepository.getTotalStatus(finish, date);
			int totalTicketCancel = troubleticketRepository.getTotalStatus(cancel, date);
			int totalTicketFinishReported = troubleticketRepository.getTotalStatus(finishReported, date);

			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("totalTicketOpen", totalTicketOpen);
			data.put("totalTicketDispatch", totalTicketDispatch);
			data.put("totalTicketInProgress", totalTicketInProgress);
			data.put("totalTicketHold", totalTicketHold);
			data.put("totalTicketFinish", totalTicketFinish);
			data.put("totalTicketCancel", totalTicketCancel);
			data.put("totalTicketFinishReported", totalTicketFinishReported);
			listData.add(data);
		}

		return listData;
	}

	@GetMapping("/totalStatusTicketCustom")
	public ArrayList<HashMap<String, Object>> totalStatusTicketCustom(@RequestParam Date startDate,
			@RequestParam Date endDate) {
		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		Long open = (long) 8;
		Long dispatch = (long) 9;
		Long inprogress = (long) 10;
		Long hold = (long) 11;
		Long finish = (long) 12;
		Long cancel = (long) 13;
		Long finishReported = (long) 30;
		int totalTicketOpen = troubleticketRepository.getTotalStatusCustom(open, startDate, endDate);
		int totalTicketDispatch = troubleticketRepository.getTotalStatusCustom(dispatch, startDate, endDate);
		int totalTicketInProgress = troubleticketRepository.getTotalStatusCustom(inprogress, startDate, endDate);
		int totalTicketHold = troubleticketRepository.getTotalStatusCustom(hold, startDate, endDate);
		int totalTicketFinish = troubleticketRepository.getTotalStatusCustom(finish, startDate, endDate);
		int totalTicketCancel = troubleticketRepository.getTotalStatusCustom(cancel, startDate, endDate);
		int totalTicketFinishReported = troubleticketRepository.getTotalStatusCustom(finishReported, startDate,
				endDate);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("totalTicketOpen", totalTicketOpen);
		data.put("totalTicketDispatch", totalTicketDispatch);
		data.put("totalTicketInProgress", totalTicketInProgress);
		data.put("totalTicketHold", totalTicketHold);
		data.put("totalTicketFinish", totalTicketFinish);
		data.put("totalTicketCancel", totalTicketCancel);
		data.put("totalTicketFinishReported", totalTicketFinishReported);
		listData.add(data);

		return listData;
	}

	@GetMapping("ticketBaseOnPriority")
	public ArrayList<HashMap<String, Object>> ticketBaseOnPriority(
			@RequestParam(value = "period", required = false) Integer period) {
		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		Long priorityLow = (long) 19;
		Long priorityMedium = (long) 20;
		Long priorityHigh = (long) 21;
		Long priorityUrgent = (long) 22;
		if (period == 1) {
			LocalDateTime localNow = LocalDateTime.now().minusDays(4);
			Timestamp dateNow = Timestamp.valueOf(localNow);
			Date date = new Date(dateNow.getTime());
			int totalPriorityLow = troubleticketRepository.ticketBaseOnPriority(priorityLow, date);
			int totalPriorityMedium = troubleticketRepository.ticketBaseOnPriority(priorityMedium, date);
			int totalPriorityHigh = troubleticketRepository.ticketBaseOnPriority(priorityHigh, date);
			int totalPriorityUrgent = troubleticketRepository.ticketBaseOnPriority(priorityUrgent, date);

			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("totalPriorityLow", totalPriorityLow);
			data.put("totalPriorityMedium", totalPriorityMedium);
			data.put("totalPriorityHigh", totalPriorityHigh);
			data.put("totalPriorityUrgent", totalPriorityUrgent);
			listData.add(data);
		} else if (period == 2) {
			LocalDateTime localNow = LocalDateTime.now().minusWeeks(1);
			Timestamp dateNow = Timestamp.valueOf(localNow);
			Date date = new Date(dateNow.getTime());
			int totalPriorityLow = troubleticketRepository.ticketBaseOnPriority(priorityLow, date);
			int totalPriorityMedium = troubleticketRepository.ticketBaseOnPriority(priorityMedium, date);
			int totalPriorityHigh = troubleticketRepository.ticketBaseOnPriority(priorityHigh, date);
			int totalPriorityUrgent = troubleticketRepository.ticketBaseOnPriority(priorityUrgent, date);
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("totalPriorityLow", totalPriorityLow);
			data.put("totalPriorityMedium", totalPriorityMedium);
			data.put("totalPriorityHigh", totalPriorityHigh);
			data.put("totalPriorityUrgent", totalPriorityUrgent);
			listData.add(data);
		} else if (period == 3) {
			LocalDateTime localNow = LocalDateTime.now().minusMonths(1);
			Timestamp dateNow = Timestamp.valueOf(localNow);
			Date date = new Date(dateNow.getTime());
			int totalPriorityLow = troubleticketRepository.ticketBaseOnPriority(priorityLow, date);
			int totalPriorityMedium = troubleticketRepository.ticketBaseOnPriority(priorityMedium, date);
			int totalPriorityHigh = troubleticketRepository.ticketBaseOnPriority(priorityHigh, date);
			int totalPriorityUrgent = troubleticketRepository.ticketBaseOnPriority(priorityUrgent, date);
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("totalPriorityLow", totalPriorityLow);
			data.put("totalPriorityMedium", totalPriorityMedium);
			data.put("totalPriorityHigh", totalPriorityHigh);
			data.put("totalPriorityUrgent", totalPriorityUrgent);
			listData.add(data);
		} else if (period == 4) {
			LocalDateTime localNow = LocalDateTime.now().minusYears(1);
			Timestamp dateNow = Timestamp.valueOf(localNow);
			Date date = new Date(dateNow.getTime());
			int totalPriorityLow = troubleticketRepository.ticketBaseOnPriority(priorityLow, date);
			int totalPriorityMedium = troubleticketRepository.ticketBaseOnPriority(priorityMedium, date);
			int totalPriorityHigh = troubleticketRepository.ticketBaseOnPriority(priorityHigh, date);
			int totalPriorityUrgent = troubleticketRepository.ticketBaseOnPriority(priorityUrgent, date);
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("totalPriorityLow", totalPriorityLow);
			data.put("totalPriorityMedium", totalPriorityMedium);
			data.put("totalPriorityHigh", totalPriorityHigh);
			data.put("totalPriorityUrgent", totalPriorityUrgent);
			listData.add(data);
		} else if (period == 5) {
			LocalDateTime localNow = LocalDateTime.now().minusMonths(2);
			Timestamp dateNow = Timestamp.valueOf(localNow);
			Date date = new Date(dateNow.getTime());
			int totalPriorityLow = troubleticketRepository.ticketBaseOnPriority(priorityLow, date);
			int totalPriorityMedium = troubleticketRepository.ticketBaseOnPriority(priorityMedium, date);
			int totalPriorityHigh = troubleticketRepository.ticketBaseOnPriority(priorityHigh, date);
			int totalPriorityUrgent = troubleticketRepository.ticketBaseOnPriority(priorityUrgent, date);
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("totalPriorityLow", totalPriorityLow);
			data.put("totalPriorityMedium", totalPriorityMedium);
			data.put("totalPriorityHigh", totalPriorityHigh);
			data.put("totalPriorityUrgent", totalPriorityUrgent);
			listData.add(data);
		} else if (period == 6) {
			LocalDateTime localNow = LocalDateTime.now().minusMonths(6);
			Timestamp dateNow = Timestamp.valueOf(localNow);
			Date date = new Date(dateNow.getTime());
			int totalPriorityLow = troubleticketRepository.ticketBaseOnPriority(priorityLow, date);
			int totalPriorityMedium = troubleticketRepository.ticketBaseOnPriority(priorityMedium, date);
			int totalPriorityHigh = troubleticketRepository.ticketBaseOnPriority(priorityHigh, date);
			int totalPriorityUrgent = troubleticketRepository.ticketBaseOnPriority(priorityUrgent, date);
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("totalPriorityLow", totalPriorityLow);
			data.put("totalPriorityMedium", totalPriorityMedium);
			data.put("totalPriorityHigh", totalPriorityHigh);
			data.put("totalPriorityUrgent", totalPriorityUrgent);
			listData.add(data);
		}

		return listData;
	}

	@GetMapping("ticketBaseOnPriorityCustom")
	public ArrayList<HashMap<String, Object>> ticketBaseOnPriorityCustom(@RequestParam Date startDate,
			@RequestParam Date endDate) {
		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		Long priorityLow = (long) 19;
		Long priorityMedium = (long) 20;
		Long priorityHigh = (long) 21;
		Long priorityUrgent = (long) 22;
		int totalPriorityLow = troubleticketRepository.ticketBaseOnPriorityCustom(priorityLow, startDate, endDate);
		int totalPriorityMedium = troubleticketRepository.ticketBaseOnPriorityCustom(priorityMedium, startDate,
				endDate);
		int totalPriorityHigh = troubleticketRepository.ticketBaseOnPriorityCustom(priorityHigh, startDate, endDate);
		int totalPriorityUrgent = troubleticketRepository.ticketBaseOnPriorityCustom(priorityUrgent, startDate,
				endDate);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("totalPriorityLow", totalPriorityLow);
		data.put("totalPriorityMedium", totalPriorityMedium);
		data.put("totalPriorityHigh", totalPriorityHigh);
		data.put("totalPriorityUrgent", totalPriorityUrgent);
		listData.add(data);

		return listData;
	}

	@GetMapping("numberOfTicket")
	public ArrayList<HashMap<String, Object>> numberOfTicket() {
		
		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		
		LocalDateTime localNowStartWeek1 = LocalDateTime.now().minusDays(31);
		LocalDateTime localNowEndWeek1 = LocalDateTime.now().minusDays(24);
		LocalDateTime localNowStartWeek2 = LocalDateTime.now().minusDays(23);
		LocalDateTime localNowEndWeek2 = LocalDateTime.now().minusDays(16);
		LocalDateTime localNowStartWeek3 = LocalDateTime.now().minusDays(15);
		LocalDateTime localNowEndWeek3 = LocalDateTime.now().minusDays(8);
		LocalDateTime localNowStartWeek4 = LocalDateTime.now().minusDays(7);
		LocalDateTime localNowEndWeek4 = LocalDateTime.now();

		Timestamp startWeek1 = Timestamp.valueOf(localNowStartWeek1);
		Timestamp endWeek1 = Timestamp.valueOf(localNowEndWeek1);
		Timestamp startWeek2 = Timestamp.valueOf(localNowStartWeek2);
		Timestamp endWeek2 = Timestamp.valueOf(localNowEndWeek2);
		Timestamp startWeek3 = Timestamp.valueOf(localNowStartWeek3);
		Timestamp endWeek3 = Timestamp.valueOf(localNowEndWeek3);
		Timestamp startWeek4 = Timestamp.valueOf(localNowStartWeek4);
		Timestamp endWeek4 = Timestamp.valueOf(localNowEndWeek4);

		Date dateStartWeek1 = new Date(startWeek1.getTime());
		Date dateEndWeek1 = new Date(endWeek1.getTime());
		Date dateStartWeek2 = new Date(startWeek2.getTime());
		Date dateEndWeek2 = new Date(endWeek2.getTime());
		Date dateStartWeek3 = new Date(startWeek3.getTime());
		Date dateEndWeek3 = new Date(endWeek3.getTime());
		Date dateStartWeek4 = new Date(startWeek4.getTime());
		Date dateEndWeek4 = new Date(endWeek4.getTime());

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("Week 1", convertNumberOfTicket(dateStartWeek1, dateEndWeek1));
		data.put("Week 2", convertNumberOfTicket(dateStartWeek2, dateEndWeek2));
		data.put("Week 3", convertNumberOfTicket(dateStartWeek3, dateEndWeek3));
		data.put("Week 4", convertNumberOfTicket(dateStartWeek4, dateEndWeek4));
		listData.add(data);

		return listData;
	}

	private HashMap<String, Object> convertNumberOfTicket(@RequestParam Date startDate, @RequestParam Date endDate) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		Long ticketStatusIdOpen = (long) 8;
		Long ticketStatusIdDispatched = (long) 9;
		Long ticketStatusIdClosed = (long) 30;
		int totalTicketStatusIdOpen = troubleticketRepository.numberOfTicket(ticketStatusIdOpen, startDate, endDate);
		int totalTicketStatusIdDispatched = troubleticketRepository.numberOfTicket(ticketStatusIdDispatched, startDate,
				endDate);
		int totalTicketStatusIdClosed = troubleticketRepository.numberOfTicket(ticketStatusIdClosed, startDate,
				endDate);
		data.put("totalTicketStatusIdOpen", totalTicketStatusIdOpen);
		data.put("totalTicketStatusIdDispatched", totalTicketStatusIdDispatched);
		data.put("totalTicketStatusIdClosed", totalTicketStatusIdClosed);
		return data;
	}
}
