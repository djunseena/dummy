package com.fsm.repositories.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.HistoryDTO;
import com.fsm.models.ClientCompanyBranch;
import com.fsm.models.Code;
import com.fsm.models.Dispatch;
import com.fsm.models.History;
import com.fsm.models.Reason;
import com.fsm.models.TroubleTicket;
import com.fsm.models.UserWorkerStatus;
import com.fsm.repositories.ClientCompanyBranchRepository;
import com.fsm.repositories.CodeRepository;
import com.fsm.repositories.DispatchRepository;
import com.fsm.repositories.HistoryRepository;
import com.fsm.repositories.ReasonRepository;
import com.fsm.repositories.TroubleTicketRepository;
import com.fsm.repositories.UserWorkerStatusRepository;

@RestController
@RequestMapping("api")
public class UpdateOrderStatusRepositoryController {

	@Autowired
	private HistoryRepository historyRepository;

	@Autowired
	private CodeRepository codeRepository;

	@Autowired
	private DispatchRepository dispatchRepository;

	@Autowired
	private UserWorkerStatusRepository userWorkerStatusRepository;

	@Autowired
	private TroubleTicketRepository troubleTicketRepository;

	@Autowired
	private ClientCompanyBranchRepository clientCompanyBranchRepository;

	@Autowired
	private ReasonRepository reasonRepository;

	ModelMapper modelMapper = new ModelMapper();

//	Convert Entity to DTO
	private HistoryDTO convertEntityToDTO(History history) {
		HistoryDTO historyDto = modelMapper.map(history, HistoryDTO.class);
		return historyDto;
	}

//	Convert DTO to Entity
	private History convertDTOToEntity(HistoryDTO historyDto) {
		History history = modelMapper.map(historyDto, History.class);
		return history;
	}

//	Get data History by filter
	@GetMapping("/history")
	public HashMap<String, Object> getAllData(@RequestParam(value = "dispatch_status") String dispatchStatus,
			@RequestParam("user_id") long userId) {
		HashMap<String, Object> mapResult = new HashMap<String, Object>();
		ArrayList<HistoryDTO> listHistoryDto = new ArrayList<HistoryDTO>();

		for (History history : historyRepository.findByFilter(dispatchStatus, userId)) {
			HistoryDTO historyDto = convertEntityToDTO(history);
			listHistoryDto.add(historyDto);
		}

		mapResult.put("Message", "Show Data By Filter");
		mapResult.put("Total", listHistoryDto.size());
		mapResult.put("Data", listHistoryDto);
		return mapResult;
	}

//	Update order detail status 
	@PutMapping("/history/create/{orderId}")
	public HashMap<String, Object> updateStatus(@RequestParam(value = "teknisiLat") BigDecimal teknisiLat,
			@RequestParam(value = "teknisiLong") BigDecimal teknisiLong, @PathVariable(value = "orderId") long id,
			@Valid @RequestBody HistoryDTO historyDto) {
		HashMap<String, Object> mapResult = new HashMap<String, Object>();

		boolean isNear = false;
		boolean isMatch = false;
		String message = "";
		String status = "";

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);

		Dispatch dispatch = dispatchRepository.findById(historyDto.getOrderId().getOrderId()).orElse(null);

		isNear = getDistanceUpdate(teknisiLat, teknisiLong, dispatch.getTicketId().getBranchId().getBranchId());

		String reason = "";

		History history = convertDTOToEntity(historyDto);

		history.setCreatedBy(dispatch.getUserId().getUserId());

		for (Code code : codeRepository.findAll()) {
//			hold button
			if (history.getDispatchStatus().equalsIgnoreCase("hold")) {

				if (isNear) {

					for (Reason reasonLoop : reasonRepository.getHold()) {
						if (reasonLoop.getReasonDesc().equalsIgnoreCase(historyDto.getReason())) {
							reason = reasonLoop.getReasonDesc();
							isMatch = true;
						}

						if (!reasonLoop.getReasonDesc().equalsIgnoreCase(historyDto.getReason())) {
							reason = historyDto.getReason();
							isMatch = true;
						}
					}
					if (isMatch) {
						history.setReason(reason);
						historyRepository.save(history);
						status = "Success!";
						message = "Update Status Success";
					} else {
						status = "Failed!";
						message = "Reason isn't match";
					}

					if (code.getCodeId() == 29) {
						history.setDispatchStatus(code.getCodeName());
						history.setDispatchAction(25);

						for (Dispatch dis : dispatchRepository.findAll()) {
							// Update worker status
							for (UserWorkerStatus uws : userWorkerStatusRepository.findAll()) {
								if (dis.getUserId().getUserId() == uws.getUserId().getUserId()
										&& dis.getOrderId() == history.getOrderId().getOrderId()) {
									uws.setStatus(1);
									userWorkerStatusRepository.save(uws);

									// Update trouble ticket
									for (TroubleTicket tt : troubleTicketRepository.findAll()) {
										if (dis.getTicketId().getTicketId() == tt.getTicketId()) {
											tt.setLastModifiedBy(dis.getUserId().getUserId());
											tt.setTicketStatusId(11);
											troubleTicketRepository.save(tt);
										}
									}
								}
							}

						}
					}
				} else if (!isNear) {
					status = "Failed!";
					message = "Can't Update Status, because location so far";
				}
			}
//			finish button
			else if (history.getDispatchStatus().equalsIgnoreCase("finish")) {
				if (isNear) {
					if (code.getCodeId() == 18) {
						history.setDispatchStatus(code.getCodeName());
						history.setDispatchAction(24);

						reason = historyDto.getReason();
						isMatch = true;

						if (isMatch) {
							history.setReason(reason);
							historyRepository.save(history);
							status = "Success!";
							message = "Update Status Success";
						} else {
							status = "Failed!";
							message = "Reason isn't match";
						}

						for (Dispatch dis : dispatchRepository.findAll()) {
							// Update worker status
							for (UserWorkerStatus uws : userWorkerStatusRepository.findAll()) {
								if (dis.getUserId().getUserId() == uws.getUserId().getUserId()
										&& dis.getOrderId() == history.getOrderId().getOrderId()) {
									uws.setStatus(1);
									userWorkerStatusRepository.save(uws);

									// Update trouble ticket
									for (TroubleTicket tt : troubleTicketRepository.findAll()) {
										if (dis.getTicketId().getTicketId() == tt.getTicketId()) {
											tt.setLastModifiedBy(dis.getUserId().getUserId());
											tt.setTicketStatusId(12);
											troubleTicketRepository.save(tt);
										}
									}

									// update end job dispatch
									if (history.getOrderId().getOrderId() == dis.getOrderId()) {
										dis.setLastModifiedBy(dis.getUserId().getUserId());
										dis.setEndJob(dateNow);
										dispatchRepository.save(dis);
									}
								}
							}

						}
					}
				} else if (!isNear) {
					status = "Failed!";
					message = "Can't Update Status, because location so far";
				}
			}

//			cancel button
			else if (history.getDispatchStatus().equalsIgnoreCase("canceled")) {

				if (code.getCodeId() == 15) {
					history.setDispatchStatus(code.getCodeName());
					history.setDispatchAction(28);

					for (Reason reasonLoop : reasonRepository.getCancel()) {
						if (reasonLoop.getReasonDesc().equalsIgnoreCase(historyDto.getReason())) {
							reason = reasonLoop.getReasonDesc();
							isMatch = true;
						}

						if (!reasonLoop.getReasonDesc().equalsIgnoreCase(historyDto.getReason())) {
							reason = historyDto.getReason();
							isMatch = true;
						}
					}

					if (isMatch) {
						history.setReason(reason);
						historyRepository.save(history);
						status = "Success!";
						message = "Update Status Success";
					} else {
						status = "Failed!";
						message = "Reason isn't match";
					}

					for (Dispatch dis : dispatchRepository.findAll()) {
						// Update worker status
						for (UserWorkerStatus uws : userWorkerStatusRepository.findAll()) {
							if (dis.getUserId().getUserId() == uws.getUserId().getUserId()
									&& dis.getOrderId() == history.getOrderId().getOrderId()) {
								uws.setLastModifiedBy(dis.getUserId().getUserId());
								uws.setStatus(1);
								userWorkerStatusRepository.save(uws);

								// Update trouble ticket
								for (TroubleTicket tt : troubleTicketRepository.findAll()) {
									if (dis.getTicketId().getTicketId() == tt.getTicketId()) {
										tt.setLastModifiedBy(dis.getUserId().getUserId());
										tt.setTicketStatusId(13);
										troubleTicketRepository.save(tt);
									}
								}

								if (dis.getStartJob() != null) {
									// update cancel end job dispatch
									if (history.getOrderId().getOrderId() == dis.getOrderId()) {
										dis.setLastModifiedBy(dis.getUserId().getUserId());
										dis.setEndJob(dateNow);
										dispatchRepository.save(dis);
									}
								}

							}
						}

					}
				}
			}
		}

		mapResult.put("Status", status);
		mapResult.put("Message", message);
		return mapResult;
	}

//	Code untuk memastikan apakah jarak lat long worker dengan lat long client tidak lebih dari 2KM
	@GetMapping("/geofencingUpdate/{id}")
	public boolean getDistanceUpdate(@RequestParam(value = "teknisiLat") BigDecimal teknisiLat,
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
	@GetMapping("/geofencingUpdate/total/{id}")
	public BigDecimal getTotalDistanceUpdate(@RequestParam(value = "teknisiLat") BigDecimal teknisiLat,
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
