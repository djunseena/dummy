//package com.fsm.repositories.controllers;
//
//import java.sql.Date;
//import java.sql.Time;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Slice;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.fsm.models.Code;
//import com.fsm.models.Dispatch;
//import com.fsm.models.UserWorkerStatus;
//import com.fsm.models.Users;
//import com.fsm.repositories.CodeRepository;
//import com.fsm.repositories.DispatchRepository;
//import com.fsm.repositories.UserWorkerStatusRepository;

//
//import com.fsm.dtos.WorkerDTO;
//import com.fsm.models.Worker;
//import com.fsm.repositories.WorkerRepository;
//
//import com.fsm.repositories.UsersRepository;
//
//@RestController
//@RequestMapping("worker_repo")
//public class WorkerRepositoryController {


//	@Autowired
//	WorkerRepository workerRepository;
//
//	ModelMapper modelMapper = new ModelMapper();
//
//	@GetMapping("getAllWorker")
//	public List<Object> getAllWorker() {
//		return this.workerRepository.getAllWorker();
//	}
//
//	@GetMapping("getAllWorkerForDispatch")
//	public List<Object> getAllWorkerForDispatch(@RequestParam Date dispatchDate, @RequestParam Time dispatchTime,
//			@RequestParam Long ticketId) {
//		return this.workerRepository.getAllWorkerForDispatch(dispatchDate, dispatchTime, ticketId);
//	}
//
//	@GetMapping("getAllWorkerForDispatchBySecondaryArea")
//	public List<Object> getAllWorkerForDispatchBySecondaryArea(@RequestParam Date dispatchDate,
//			@RequestParam Time dispatchTime, @RequestParam Long ticketId) {
//		return this.workerRepository.getAllWorkerForDispatchBySecondaryArea(dispatchDate, dispatchTime, ticketId);
//	}
//
//	@GetMapping("getTotalAllWorkerAvailable")
//	public Object getTotalAllWorkerAvailable() {
//		return this.workerRepository.getTotalAllWorkerAvailable();
//	}
//
//	@GetMapping("getAllWorkerAvailable")
//	public Slice<Object> getAllWorkerAvailable(Pageable pageable) {
//		return this.workerRepository.getAllWorkerAvailable(pageable);
//	}
//
//	@GetMapping("getDetailWorkerOnMonitoring")
//	public Object getDetailWorkerOnMonitoring(@RequestParam Long orderId) {
//		return this.workerRepository.getDetailWorkerOnMonitoring(orderId);
//	}
//
//	@GetMapping("getCounterWorkerOnMonitoring")
//	public List<Object> getCounterWorkerOnMonitoring() {
//		return this.workerRepository.getCounterWorkerOnMonitoring();
//	}
//
//	@GetMapping("monitoring/technician")
//	public List<Object> getMonitoringTechnician() {
//		return this.workerRepository.getMonitoringTechnician();
//	}
//
//	public WorkerDTO convertToDTO(Worker worker) {
//		WorkerDTO workerDto = modelMapper.map(worker, WorkerDTO.class);
//		return workerDto;
//	}
//
//	@GetMapping("monitoring/technician/{status}")
//	public List<Object> getMonitoringTechnician(@PathVariable(value = "status") int status) {
//		return this.workerRepository.getMonitoringTechnicianStatus(status);
//	}
//
//	@GetMapping("getDataTechnicianOnMonitoring")
//	public List<Object> getDataTechnicianOnMonitoring() {
//		return this.workerRepository.getDataTechnicianOnMonitoring();
//	}
//
//}
