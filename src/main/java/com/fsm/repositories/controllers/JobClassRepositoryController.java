package com.fsm.repositories.controllers;

import java.sql.Timestamp;
import java.text.ParseException;
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

import com.fsm.dtos.JobClassDTO;
import com.fsm.models.JobCategory;
import com.fsm.models.JobClass;
import com.fsm.repositories.JobCategoryRepository;
import com.fsm.repositories.JobClassRepository;

@RestController
@RequestMapping("jobClass_repo")
public class JobClassRepositoryController {

	ModelMapper modelMapper = new ModelMapper();

	@Autowired
	JobClassRepository jobClassRepository;

	@Autowired
	JobCategoryRepository jobCategoryRepository;

	public JobClassDTO convertToDTO(JobClass jobClass) {
		JobClassDTO jobClassDto = modelMapper.map(jobClass, JobClassDTO.class);
		return jobClassDto;
	}

	private JobClass convertToEntity(JobClassDTO jobClassDto) {
		JobClass jobClass = modelMapper.map(jobClassDto, JobClass.class);
		return jobClass;
	}

	// Get All
	@GetMapping("/JobClass/all")
	public HashMap<String, Object> getAllJobClass(@RequestParam String search, Pageable pageable) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		List<JobClassDTO> listJobClass = new ArrayList<JobClassDTO>();
		for (JobClass tempJobClass : jobClassRepository.getDataJobClass(search, pageable)) {
			JobClassDTO jobClassDTO = convertToDTO(tempJobClass);
			if (jobClassDTO.isDeleted() == false) {
				listJobClass.add(jobClassDTO);
			}
		}
		ArrayList<JobClass> sizeTotal = (ArrayList<JobClass>) jobClassRepository.findAllJobClasses();
		String message;
		if (listJobClass.isEmpty()) {
			message = "Data Kosong";
		} else {
			message = "Data Job Class";
		}
		showHashMap.put("Message", message);
		showHashMap.put("Total", sizeTotal.size());
		showHashMap.put("Data", listJobClass);

		return showHashMap;
	}

	// Create a new JobClass DTO Mapper
	@PostMapping("/JobClass/create")
	public HashMap<String, Object> createJobClassDTOMapper(@Valid @RequestBody JobClassDTO body) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		JobClass jobClassEntity = modelMapper.map(body, JobClass.class);
		Boolean isFind = checkJobClassName(body);
			if (isFind == false) {
			jobClassRepository.save(jobClassEntity);
			body.setJobClassId(jobClassEntity.getJobClassId());
			result.put("Status", HttpStatus.OK);
			result.put("Message", "Job Class Berhasil Dibuat");
			result.put("Data", body);
			}else {
				result.put("Status", HttpStatus.BAD_REQUEST);
				result.put("Message", "Job Class Gagal Dibuat, Nama Job Class : '"+ body.getJobClassName() +"' Sudah Terdaftar");
			}

		return result;
	}

	// Update JobClass DTO Mapper
	@PutMapping("/JobClass/update/{id}")
	public HashMap<String, Object> update(@PathVariable(value = "id") Long id,
			@Valid @RequestBody JobClassDTO jobClassDTO) {
		HashMap<String, Object> process = new HashMap<String, Object>();
		JobClass tempJobClass = jobClassRepository.findById(id).orElse(null);
		ArrayList<JobClass> checkData = jobClassRepository.checkDuplicateData(jobClassDTO.getJobClassName(), id);
		
		if (checkData.isEmpty()) {
			jobClassDTO.setJobClassId(tempJobClass.getJobClassId());
			jobClassDTO.setCreatedBy(tempJobClass.getCreatedBy());
			jobClassDTO.setCreatedOn(tempJobClass.getCreatedOn());
		    if (jobClassDTO.getJobClassName() == null) {
		    	jobClassDTO.setJobClassName(tempJobClass.getJobClassName());
		    }
		    
		    tempJobClass = convertToEntity(jobClassDTO);
		    
		    jobClassRepository.save(tempJobClass);
		    process.put("Status", HttpStatus.OK);
		    process.put("Message", "Job Class Berhasil Diubah");
		    process.put("Data", tempJobClass);
		}else {
			 process.put("Status", HttpStatus.BAD_REQUEST);
			 process.put("Message", "Job Class Gagal Diubah, Nama Job Class : '"+ jobClassDTO.getJobClassName() +"' Sudah Terdaftar");
		}
		return process;
	}

	@GetMapping("/listJobClass")
	public HashMap<String, Object> getListJobClass() throws ParseException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		ArrayList<JobClass> listJobClassEntity = (ArrayList<JobClass>) jobClassRepository.findAllJobClasses();

		for (JobClass item : listJobClassEntity) {
			HashMap<String, Object> data = new HashMap<>();
			JobClassDTO jobClassDTO = modelMapper.map(item, JobClassDTO.class);
			data.put("jobClassId", jobClassDTO.getJobClassId());
			data.put("jobClassName", jobClassDTO.getJobClassName());
			listData.add(data);
		}
		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;

	}

	@PutMapping("deleteJobClass")
	public HashMap<String, Object> deleteJobClass(@RequestParam Long jobClassId, @RequestBody JobClass body) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		try {
			JobClass jobClass = jobClassRepository.findById(jobClassId).orElse(null);
			JobCategory checkJobClass = jobCategoryRepository.checkJobClass(jobClassId);
			if (checkJobClass == null) {
				jobClass.setLastModifiedOn(dateNow);
				jobClass.setDeleted(true);
				jobClass.setLastModifiedBy(body.getLastModifiedBy());
				jobClassRepository.save(jobClass);
				message = "Job Class Berhasil Dihapus";
				result.put("Status", HttpStatus.OK);
			} else {
				message = "Job Class Gagal Dihapus, Karena Data Masih Digunakan";
				result.put("Status", HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			message = e.getMessage();
		}
		result.put("Message", message);
		return result;
	}

	private Boolean checkJobClassName (@Valid @RequestBody JobClassDTO body) {
		Boolean isFind = false;
		ArrayList<JobClass> checking = jobClassRepository.checkJobClassName(body.getJobClassName());
		if (checking.isEmpty()) {
			isFind = false;
		} else {
			isFind = true;
		}
		return isFind;
	}
}
