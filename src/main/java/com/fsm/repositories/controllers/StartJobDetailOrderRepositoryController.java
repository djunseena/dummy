package com.fsm.repositories.controllers;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.HistoryDTO;
import com.fsm.models.ClientCompanyBranch;
import com.fsm.models.Dispatch;
import com.fsm.models.History;
import com.fsm.models.TroubleTicket;
import com.fsm.models.UserWorkerStatus;
import com.fsm.repositories.ClientCompanyBranchRepository;
import com.fsm.repositories.CodeRepository;
import com.fsm.repositories.DispatchRepository;
import com.fsm.repositories.HistoryRepository;
import com.fsm.repositories.TroubleTicketRepository;
import com.fsm.repositories.UserWorkerStatusRepository;

@RestController
@RequestMapping("api")
public class StartJobDetailOrderRepositoryController {

	@Autowired
	private DispatchRepository dispatchRepository;

	@Autowired
	private TroubleTicketRepository troubleTicketRepository;

	@Autowired
	private HistoryRepository historyRepository;

	@Autowired
	private UserWorkerStatusRepository userWorkerStatusRepository;

	@Autowired
	private CodeRepository codeRepository;

	@Autowired
	private ClientCompanyBranchRepository clientCompanyBranchRepository;

	ModelMapper modelMapper = new ModelMapper();

	public HistoryDTO convertToDTO(History History) {
		return modelMapper.map(History, HistoryDTO.class);
	}

	public History convertToEntity(HistoryDTO HistoryDto) {
		return modelMapper.map(HistoryDto, History.class);
	}

	// API Create History
	@PostMapping("/history/startJob")
	public HashMap<String, Object> createHistory(@Valid @RequestBody HistoryDTO historyDto,
			@RequestParam(value = "teknisiLat") BigDecimal teknisiLat,
			@RequestParam(value = "teknisiLong") BigDecimal teknisiLong) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		boolean isNear = false;
		String message;
		String status;
		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);

		Dispatch dispatch = dispatchRepository.findById(historyDto.getOrderId().getOrderId()).orElse(null);

		isNear = getDistance(teknisiLat, teknisiLong, dispatch.getTicketId().getBranchId().getBranchId());

		if (isNear) {

//		Create New History With Status Start
			History history = convertToEntity(historyDto);

//  	Find last history by Order ID to check whether it comes from the hold action
			if (historyRepository.findAll().isEmpty()) {
				history.setDispatchAction(23);
			} else if (historyRepository.findByOrderIdFix(history.getOrderId().getOrderId()) == null) {
				history.setDispatchAction(23);
			} else if (historyRepository.findByOrderIdFix(history.getOrderId().getOrderId())
					.getDispatchAction() == 25) {
				history.setDispatchAction(26);
			} else {
				history.setDispatchAction(23);
			}

			history.setDispatchStatus(codeRepository.findByCodeId(16).getCodeName());
			history.setCreatedOn(dateNow);
			history.setCreatedBy(dispatch.getUserId().getUserId());
			historyRepository.save(history);

//		Update Dispatch Start Job
			dispatch.setLastModifiedBy(dispatch.getUserId().getUserId());
			dispatch.setStartJob(dateNow);
			dispatchRepository.save(dispatch);

			for (Dispatch dis : dispatchRepository.findAll()) {
				// Update worker status
				for (UserWorkerStatus uws : userWorkerStatusRepository.findAll()) {
					if (dis.getUserId().getUserId() == uws.getUserId().getUserId()
							&& dis.getOrderId() == history.getOrderId().getOrderId()) {
						uws.setStatus(2);
						uws.setLastModifiedOn(dateNow);
						userWorkerStatusRepository.save(uws);

						// Update trouble ticket
						for (TroubleTicket tt : troubleTicketRepository.findAll()) {
							if (dis.getTicketId().getTicketId() == tt.getTicketId()) {
								tt.setTicketStatusId(10);
								tt.setLastModifiedBy(dis.getUserId().getUserId());
								tt.setLastModifiedOn(dateNow);
								troubleTicketRepository.save(tt);
							}
						}
					}
				}

			}

			status = "Success!";
			message = "Start Job";

		} else {
			status = "Failed!";
			message = "Can't Start Job, because location so far";
		}

		showHashMap.put("Status", status);
		showHashMap.put("Message", message);

		return showHashMap;
	}

//	Code untuk memastikan apakah jarak lat long worker dengan lat long client tidak lebih dari 2KM
	@GetMapping("/geofencing/{id}")
	public boolean getDistance(@RequestParam(value = "teknisiLat") BigDecimal teknisiLat,
			@RequestParam(value = "teknisiLong") BigDecimal teknisiLong, @PathVariable(value = "id") Long BranchId) {
		boolean isNear = false;

		double maxDistance = 2000; // toleransi distance 2KM/2000 M

		ClientCompanyBranch clientCompanyBranch = clientCompanyBranchRepository.findById(BranchId).get();

		double teknisiLatDouble = teknisiLat.doubleValue();
		double companyLatDouble = clientCompanyBranch.getBranchLatitude().doubleValue();
		double teknisiLongDouble = teknisiLong.doubleValue();
		double companyLongDouble = clientCompanyBranch.getBranchLongitude().doubleValue();

		double radius = 6371; // Radius of earth

		double latDistance = Math.toRadians(companyLatDouble - teknisiLatDouble);
		double longDistance = Math.toRadians(companyLongDouble - teknisiLongDouble);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(teknisiLatDouble))
				* Math.cos(Math.toRadians(companyLatDouble)) * Math.sin(longDistance / 2) * Math.sin(longDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = radius * c * 1000; // convert to meters

		distance = Math.pow(distance, 2);
		BigDecimal result = new BigDecimal(Math.sqrt(distance));
		BigDecimal radiusBd = new BigDecimal(maxDistance);

		if (result.compareTo(radiusBd) == -1) {
			isNear = true;
		} else {
			isNear = false;
		}
		return isNear;
	}

//	Code untuk menghitung jarak antara lat long worker dengan lat long client
	@GetMapping("/geofencing/total/{id}")
	public BigDecimal getTotalDistance(@RequestParam(value = "teknisiLat") BigDecimal teknisiLat,
			@RequestParam(value = "teknisiLong") BigDecimal teknisiLong, @PathVariable(value = "id") Long BranchId) {

		ClientCompanyBranch clientCompanyBranch = clientCompanyBranchRepository.findById(BranchId).get();

		double teknisiLatDouble = teknisiLat.doubleValue();
		double companyLatDouble = clientCompanyBranch.getBranchLatitude().doubleValue();
		double teknisiLongDouble = teknisiLong.doubleValue();
		double companyLongDouble = clientCompanyBranch.getBranchLongitude().doubleValue();

		double radius = 6371; // Radius or earth (2km)

		double latDistance = Math.toRadians(companyLatDouble - teknisiLatDouble);
		double longDistance = Math.toRadians(companyLongDouble - teknisiLongDouble);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(teknisiLatDouble))
				* Math.cos(Math.toRadians(companyLatDouble)) * Math.sin(longDistance / 2) * Math.sin(longDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = radius * c * 1000; // convert to meters

		distance = Math.pow(distance, 2);
		BigDecimal result = new BigDecimal(Math.sqrt(distance));

		return result;
	}

}