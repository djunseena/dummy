package com.fsm.repositories.controllers;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import com.fsm.dtos.WorkingTimeDTO;
import com.fsm.models.SLA;
import com.fsm.models.Users;
import com.fsm.models.WorkingTime;
import com.fsm.repositories.SLARepository;
import com.fsm.repositories.WorkingTimeRepository;

@RestController
@RequestMapping("api")
public class WorkingTimeRepositoryController {

	ModelMapper modelMapper = new ModelMapper();

	@Autowired
	WorkingTimeRepository workingTimeRepository;

	@Autowired
	SLARepository slaRepository;

	public WorkingTimeDTO convertToDTO(WorkingTime workingTime) {
		WorkingTimeDTO workingTimeDto = modelMapper.map(workingTime, WorkingTimeDTO.class);
		return workingTimeDto;
	}

	// Get All
	@GetMapping("listWTime")
	public Map<String, Object> getAllListWorkingTime(@RequestParam String search, Pageable pageable) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();
		ArrayList<WorkingTime> listWorkingTimeEntity = (ArrayList<WorkingTime>) workingTimeRepository.getListWorkingTime(search, pageable);
		int totalListWorkingTime = workingTimeRepository.getTotalListWorkingTime(search);
		int totalListPage = (int) Math.ceil((listData.size() / 10) + 1);
		for (WorkingTime item : listWorkingTimeEntity) {
			HashMap<String, Object> data = new HashMap<>();

			WorkingTimeDTO wtimeDTO = modelMapper.map(item, WorkingTimeDTO.class);

			data.put("wtimeId", wtimeDTO.getWTimeId());
			data.put("wtimeName", wtimeDTO.getWTimeName());
			data.put("wtimeDesc", wtimeDTO.getWTimeDesc());
			data.put("wtimeStart", wtimeDTO.getWTimeStart());
			data.put("wtimeEnd", wtimeDTO.getWTimeEnd());

			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);
		result.put("totalListWorkingTime", totalListWorkingTime);
		result.put("totalListPage", totalListPage);

		return result;
	}

	// Create a new WorkingTime DTO Mapper
	@PostMapping("workingTime/create")
	public HashMap<String, Object> createWorkingTime(@Valid @RequestBody WorkingTimeDTO body) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		WorkingTime workingTime = new WorkingTime();

		String wtimeName = body.getWTimeName().trim();
		Time wtimeStart = body.getWTimeStart();
		Time wtimeEnd = body.getWTimeEnd();

		if(workingTimeRepository.findWorkingTimeByWorkingTime(wtimeName, wtimeStart, wtimeEnd) == null){
			// ini proses save data ke database
			workingTime.setWTimeName(wtimeName);
			workingTime.setWTimeStart(wtimeStart);
			workingTime.setWTimeEnd(wtimeEnd);
			workingTime.setWTimeDesc(body.getWTimeDesc());
			workingTime.setCreatedBy(body.getCreatedBy());
			workingTime.setCreatedOn(dateNow);
			workingTime.setLastModifiedBy(body.getLastModifiedBy());
			workingTime.setLastModifiedOn(dateNow);
			workingTime.setDeleted(false);
			workingTimeRepository.save(workingTime);
			body.setWTimeId(workingTime.getWTimeId());

			// Maping result untuk response API
			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", "Working Time Berhasil Dibuat");
			showHashMap.put("Data", body);
		} else {
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", "Working Time Gagal Dibuat. Nama Working Time : '" + wtimeName +"' Telah Digunakan");
		}

		return showHashMap;
	}

	// Update WorkingTime DTO Mapper
	@PutMapping("workingTime/update/{id}")
	public HashMap<String, Object> update(@PathVariable(value = "id") Long id, @Valid @RequestBody WorkingTimeDTO body) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		WorkingTime workingTime = workingTimeRepository.findById(id).orElse(null);

		String wtimeName = body.getWTimeName().trim();
		Time wtimeStart = body.getWTimeStart();
		Time wtimeEnd = body.getWTimeEnd();

		if(workingTimeRepository.findWorkingTimeByWorkingTime(wtimeName, wtimeStart, wtimeEnd) == null){
			// ini proses save data ke database
			workingTime.setWTimeName(wtimeName);
			workingTime.setWTimeStart(wtimeStart);
			workingTime.setWTimeEnd(wtimeEnd);
			workingTime.setWTimeDesc(body.getWTimeDesc());
			workingTime.setLastModifiedBy(body.getLastModifiedBy());
			workingTime.setLastModifiedOn(dateNow);
			workingTimeRepository.save(workingTime);
			body.setWTimeId(workingTime.getWTimeId());

			// Maping result untuk response API
			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", "Working Time Berhasil Diubah");
			showHashMap.put("Data", body);
		} else if(workingTimeRepository.findWorkingTimeByWorkingTime(wtimeName, wtimeStart, wtimeEnd) == workingTime){
			// ini proses save data ke database
			workingTime.setWTimeName(wtimeName);
			workingTime.setWTimeStart(wtimeStart);
			workingTime.setWTimeEnd(wtimeEnd);
			workingTime.setLastModifiedBy(body.getLastModifiedBy());
			workingTime.setLastModifiedOn(dateNow);
			workingTimeRepository.save(workingTime);
			body.setWTimeId(workingTime.getWTimeId());

			// Maping result untuk response API
			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", "Working Time Berhasil Dibuat");
			showHashMap.put("Data", body);
		} else {
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", "Working Time Gagal Diubah. Nama Working Time : '" + wtimeName + "' Telah Digunakan");
		}

		return showHashMap;
	}

	@PutMapping("workingTime/delete/{WTimeId}")
	public HashMap<String, Object> deleteUOM(@PathVariable(value = "WTimeId") Long WTimeId,
			@Valid @RequestBody Users user) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);

		WorkingTime workingTimeEntity = workingTimeRepository.findById(WTimeId).orElse(null);
		SLA SLAEntity = slaRepository.findByWorkingTimeId(WTimeId);

		if (SLAEntity == null) {
			String message = "Working Time Berhasil Dihapus";
			workingTimeEntity.setDeleted(true);
			workingTimeEntity.setLastModifiedBy(user.getUserId());
			workingTimeEntity.setLastModifiedOn(dateNow);
			workingTimeRepository.save(workingTimeEntity);
			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);
		} else if (SLAEntity != null) {
			String message = "Working Time Gagal Dihapus, Karena Data Masih Digunakan";
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		}
		return showHashMap;
	}

	@GetMapping("listWorkingTime")
	public HashMap<String, Object> showListWorkingTime() throws ParseException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		ArrayList<WorkingTime> listWorkingTimeEntity = (ArrayList<WorkingTime>) workingTimeRepository.listWorkingTime();

		for (WorkingTime item : listWorkingTimeEntity) {
			HashMap<String, Object> data = new HashMap<>();
			WorkingTimeDTO workingTimeDTO = modelMapper.map(item, WorkingTimeDTO.class);
			data.put("wTimeId", workingTimeDTO.getWTimeId());
			data.put("wTimeName", workingTimeDTO.getWTimeName());
			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;

	}
}
