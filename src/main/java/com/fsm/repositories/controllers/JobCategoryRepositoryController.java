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

import com.fsm.dtos.JobCategoryDTO;
import com.fsm.models.Diagnosis;
import com.fsm.models.Job;
import com.fsm.models.JobCategory;
import com.fsm.models.JobCategoryReport;
import com.fsm.repositories.DiagnosisRepository;
import com.fsm.repositories.JobCategoryReportRepository;
import com.fsm.repositories.JobCategoryRepository;
import com.fsm.repositories.JobRepository;

@RestController
@RequestMapping("jobCategory_repo")
public class JobCategoryRepositoryController {
	ModelMapper modelMapper = new ModelMapper();

	@Autowired
	JobCategoryRepository jobCategoryRepository;

	@Autowired
	DiagnosisRepository diagnosisRepository;

	@Autowired
	JobRepository jobRepository;

	@Autowired
	JobCategoryReportRepository jobCategoryReportRepository;

	public JobCategoryDTO convertToDTO(JobCategory jobCategory) {
		JobCategoryDTO jobCategoryDto = modelMapper.map(jobCategory, JobCategoryDTO.class);
		return jobCategoryDto;
	}

	private JobCategory convertToEntity(JobCategoryDTO jobCategoryDto) {
		JobCategory jobCategory = modelMapper.map(jobCategoryDto, JobCategory.class);
		return jobCategory;
	}

	// Get All
	@GetMapping("/JobCategory/all")
	public HashMap<String, Object> getAllJobCategory(@RequestParam String search, Pageable pageable) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		List<JobCategoryDTO> listJobCategory = new ArrayList<JobCategoryDTO>();
		for (JobCategory tempJobCategory : jobCategoryRepository.getDataJobCategory(search, pageable)) {
			JobCategoryDTO jobCategoryDTO = convertToDTO(tempJobCategory);
			if (jobCategoryDTO.isDeleted() == false) {
				listJobCategory.add(jobCategoryDTO);
			}
		}
		ArrayList<JobCategory> sizeTotal = (ArrayList<JobCategory>) jobCategoryRepository.findAllJobCategories();
		String message;
		if (listJobCategory.isEmpty()) {
			message = "Data Kosong";
		} else {
			message = "Data Job Category";
		}
		showHashMap.put("Message", message);
		showHashMap.put("Total", sizeTotal.size());
		showHashMap.put("Data", listJobCategory);

		return showHashMap;
	}

	// get all by id
	@GetMapping("/JobCategory/id/{id}")
	public HashMap<String, Object> getJobCategoryDataById(@PathVariable(value = "id") Long jobCategoryId) {

		HashMap<String, Object> jobCategoryMap = new HashMap<String, Object>();
		JobCategory jobCategory = jobCategoryRepository.findById(jobCategoryId).orElse(null);

		JobCategoryDTO jobCategoryDto = convertToDTO(jobCategory);

		String message;
		if (jobCategory == null) {
			message = "Data Kosong";
		} else {
			message = "Data Job Category Berdasarkan Id";
		}
		jobCategoryMap.put("Message", message);
		jobCategoryMap.put("Data", jobCategoryDto);

		return jobCategoryMap;
	}

// Create a new JobCategory DTO Mapper
	@PostMapping("/JobCategory/create")
	public HashMap<String, Object> createJobCategoryDTOMapper(@Valid @RequestBody JobCategoryDTO body) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		JobCategory jobCategoryEntity = modelMapper.map(body, JobCategory.class);

		Boolean isFind = checkDataAlreadyExist(body);
		if (isFind == false) {
			jobCategoryRepository.save(jobCategoryEntity);
			body.setJobCategoryId(jobCategoryEntity.getJobCategoryId());

			JobCategoryReport jobCategoryReport = new JobCategoryReport();
			jobCategoryReport.setJobCategoryId(jobCategoryEntity);
			jobCategoryReport.setReportId(42);
			jobCategoryReport.setCreatedBy(jobCategoryEntity.getCreatedBy());
			jobCategoryReport.setCreatedOn(jobCategoryEntity.getCreatedOn());
			jobCategoryReport.setLastModifiedBy(jobCategoryEntity.getLastModifiedBy());
			jobCategoryReport.setLastModifiedOn(jobCategoryEntity.getLastModifiedOn());
			jobCategoryReport.setDeleted(false);
			jobCategoryReportRepository.save(jobCategoryReport);
					
			result.put("Status", HttpStatus.OK);
			result.put("Message", "Job Category Berhasil Dibuat");
			result.put("Data", body);
		}else {
			result.put("Status", HttpStatus.BAD_REQUEST);
			result.put("Message", "Job Category Gagal Dibuat, Nama Job Category Sudah Terdaftar");
		}

		return result;
	}

// Update JobCategory DTO 
	@PutMapping("/JobCategory/update/{id}")
	public HashMap<String, Object> update(@PathVariable(value = "id") Long id,
			@Valid @RequestBody JobCategoryDTO jobCategoryDTO) {
		HashMap<String, Object> process = new HashMap<String, Object>();
		JobCategory tempJobCategory = jobCategoryRepository.findById(id).orElse(null);
		ArrayList<JobCategory> checkData = jobCategoryRepository.checkDuplicateUpdate(jobCategoryDTO.getJobCategoryName(), jobCategoryDTO.getJobClassId().getJobClassId(), id);
		if (checkData.isEmpty() ) {
			jobCategoryDTO.setJobCategoryId(tempJobCategory.getJobCategoryId());
			jobCategoryDTO.setCreatedBy(tempJobCategory.getCreatedBy());
			jobCategoryDTO.setCreatedOn(tempJobCategory.getCreatedOn());
		    if (jobCategoryDTO.getJobCategoryName() == null) {
		    	jobCategoryDTO.setJobCategoryName(tempJobCategory.getJobCategoryName());
		    }
			jobCategoryDTO.setLastModifiedBy(jobCategoryDTO.getLastModifiedBy());
			jobCategoryDTO.setLastModifiedOn(jobCategoryDTO.getLastModifiedOn());
		    tempJobCategory = convertToEntity(jobCategoryDTO);
		    
			jobCategoryRepository.save(tempJobCategory);
			JobCategoryReport listReport = jobCategoryReportRepository.getObjectJobCategoryReportByJobCategory(id);
			if (listReport == null) {
					JobCategoryReport jobCategoryReport = new JobCategoryReport();

					jobCategoryReport.setJobCategoryId(tempJobCategory);
					jobCategoryReport.setReportId(42);
					jobCategoryReport.setCreatedBy(jobCategoryDTO.getCreatedBy());
					jobCategoryReport.setCreatedOn(jobCategoryDTO.getCreatedOn());
					jobCategoryReport.setLastModifiedBy(jobCategoryDTO.getLastModifiedBy());
					jobCategoryReport.setLastModifiedOn(jobCategoryDTO.getLastModifiedOn());
					jobCategoryReport.setDeleted(false);

				jobCategoryReportRepository.save(jobCategoryReport);
			}
		    process.put("Status", HttpStatus.OK);
		    process.put("Message", "Job Category Berhasil Diubah");
		    process.put("Data", tempJobCategory);
		}else {
		    process.put("Status", HttpStatus.BAD_REQUEST);
		    process.put("Message", "Job Category Gagal Diubah, Nama Job Category Sudah Terdaftar");				
		}
		return process;
	}

	@GetMapping("/listJobCategoryByJobClass")
	public HashMap<String, Object> listJobCategoryByJobClass(@RequestParam Long jobClassId) throws ParseException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		ArrayList<JobCategory> listJobCategoryEntity = (ArrayList<JobCategory>) jobCategoryRepository
				.findIdByJobClassId(jobClassId);

		for (JobCategory item : listJobCategoryEntity) {
			HashMap<String, Object> data = new HashMap<>();
			JobCategoryDTO jobCategoryDTO = modelMapper.map(item, JobCategoryDTO.class);
			data.put("jobCategoryId", jobCategoryDTO.getJobCategoryId());
			data.put("jobCategoryName", jobCategoryDTO.getJobCategoryName());
			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;

	}

	@GetMapping("/listJobCategory")
	public HashMap<String, Object> getListJobCategory() throws ParseException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		ArrayList<JobCategory> listJobCategoryEntity = (ArrayList<JobCategory>) jobCategoryRepository
				.findAllJobCategories();

		for (JobCategory item : listJobCategoryEntity) {
			HashMap<String, Object> data = new HashMap<>();
			JobCategoryDTO jobCategoryDTO = modelMapper.map(item, JobCategoryDTO.class);
			data.put("jobCategoryId", jobCategoryDTO.getJobCategoryId());
			data.put("jobCategoryName", jobCategoryDTO.getJobClassId().getJobClassName()+" - "+jobCategoryDTO.getJobCategoryName());
			data.put("jobClassId", jobCategoryDTO.getJobClassId().getJobClassId());
			listData.add(data);
		}
		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;

	}

	@PutMapping("deleteJobCategory")
	public HashMap<String, Object> deleteJobCategory(@RequestParam Long jobCategoryId, @RequestBody JobCategory body) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		Long userId = body.getLastModifiedBy();
		try {
			JobCategory jobCategory = jobCategoryRepository.findById(jobCategoryId).orElse(null);
			Job checkJobCategoryOnJob = jobRepository.findJobCategory(jobCategoryId);
			Diagnosis checkJobCategoryOnDiagnosis = diagnosisRepository.findJobCategory(jobCategoryId);
			JobCategoryReport checkJobCategoryOnCategoryReport = jobCategoryReportRepository
					.findJobCategory(jobCategoryId);
			if (checkJobCategoryOnJob == null && checkJobCategoryOnDiagnosis == null
					&& checkJobCategoryOnCategoryReport == null) {
				jobCategory.setLastModifiedOn(dateNow);
				jobCategory.setDeleted(true);
				jobCategory.setLastModifiedBy(userId);
				jobCategoryRepository.save(jobCategory);
				message = "Job Category Berhasil Dihapus";
				result.put("Status", HttpStatus.OK);
			} else if (checkJobCategoryOnJob == null && checkJobCategoryOnDiagnosis == null
					&& checkJobCategoryOnCategoryReport != null) {
				JobCategoryReport jobCategoryReport = jobCategoryReportRepository
						.findJobCategory(jobCategoryId);
				jobCategoryReport.setDeleted(true);
				jobCategoryReport.setLastModifiedOn(dateNow);
				jobCategoryReport.setLastModifiedBy(userId);
				jobCategory.setLastModifiedOn(dateNow);
				jobCategory.setDeleted(true);
				jobCategory.setLastModifiedBy(userId);
				jobCategoryRepository.save(jobCategory);
				message = "Job Category Berhasil Dihapus";
				result.put("Status", HttpStatus.OK);
			} else {
				message = "Job Category Gagal Dihapus, Karena Data Masih Digunakan";
				result.put("Status", HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			message = e.getMessage();
		}
		result.put("Message", message);
		return result;
	}
	
	private Boolean checkDataAlreadyExist (@Valid @RequestBody JobCategoryDTO body) {
		Boolean isFind = false;
		ArrayList<JobCategory> checking = jobCategoryRepository.checkDataIsExist(body.getJobCategoryName(), body.getJobClassId().getJobClassId());
		if(checking.isEmpty()) {
			isFind = false;
		}else {
			isFind = true ;
		}
		return isFind;
	}
}
