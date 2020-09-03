package com.fsm.repositories.controllers;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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

import com.fsm.dtos.JobDTO;
import com.fsm.models.Job;
import com.fsm.models.JobUserWorker;
import com.fsm.models.TroubleTicket;
import com.fsm.repositories.JobCategoryRepository;
import com.fsm.repositories.JobRepository;
import com.fsm.repositories.JobUserWorkerRepository;
import com.fsm.repositories.TroubleTicketRepository;

import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidExpiresRangeException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.XmlParserException;

@RestController
@RequestMapping("job_repo")
public class JobRepositoryController {

	ModelMapper modelMapper = new ModelMapper();

	@Autowired
	JobRepository jobRepository;

	@Autowired
	JobUserWorkerRepository jobUserWorkerRepository;

	@Autowired
	TroubleTicketRepository troubleTicketRepository;

	@Autowired
	JobCategoryRepository jobCategoryRepository;

	public JobDTO convertToDTO(Job job) {
		JobDTO jobDto = modelMapper.map(job, JobDTO.class);
		return jobDto;
	}

	private Job convertToEntity(JobDTO jobDto) {
		Job job = modelMapper.map(jobDto, Job.class);
		return job;
	}

	// Get All
	@GetMapping("/Job/all")
	public HashMap<String, Object> getAllJob(@RequestParam String search, Pageable pageable) throws InvalidKeyException, InvalidEndpointException, InvalidPortException, ErrorResponseException,
	IllegalArgumentException, InsufficientDataException, InternalException, InvalidBucketNameException,
	InvalidResponseException, NoSuchAlgorithmException, XmlParserException, InvalidExpiresRangeException,
	IOException{
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		List<JobDTO> listJob = new ArrayList<JobDTO>();
		for (Job tempJob : jobRepository.getDataJob(search, pageable)) {
			JobDTO jobDTO = convertToDTO(tempJob);
			if (jobDTO.isDeleted() == false) {
				listJob.add(jobDTO);
			}
		}
		ArrayList<Job> sizeTotal = (ArrayList<Job>) jobRepository.findAllJobs();
		String message;
		if (listJob.isEmpty()) {
			message = "Data Kosong";
		} else {
			message = "Data Job";
		}
		showHashMap.put("Message", message);
		showHashMap.put("Total", sizeTotal.size());
		showHashMap.put("Data", listJob);

		return showHashMap;
	}

	// Create a new Job DTO Mapper
	@PostMapping("/Job/create")
	public HashMap<String, Object> createJobDTOMapper(@Valid @RequestBody JobDTO body) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		Job jobEntity = modelMapper.map(body, Job.class);
		Boolean isFind = checkDataAlreadyExist(body);
		if(isFind == false) {	
				jobRepository.save(jobEntity);
				body.setJobId(jobEntity.getJobId());
						
				result.put("Status", HttpStatus.OK);
				result.put("Message", "Job Berhasil Dibuat");
				result.put("Data", body);
			}else {
				Long categoryId = body.getJobCategoryId().getJobCategoryId();
				String categoryName = jobCategoryRepository.findById(categoryId).orElse(null).getJobCategoryName();
				result.put("Status", HttpStatus.BAD_REQUEST);
				result.put("Message", "Job Gagal Dibuat, Nama Job : '"+ body.getJobName() +"' dengan"
				+ " Job Category : '"+ categoryName +"' Sudah Terdaftar");
			}

		return result;
	}

	// Update Job DTO Mapper
	@PutMapping("/Job/update/{id}")
	public HashMap<String, Object> update(@PathVariable(value = "id") Long id, @Valid @RequestBody JobDTO jobDTO) {
		HashMap<String, Object> process = new HashMap<String, Object>();
		Job tempJob = jobRepository.findById(id).orElse(null);
		ArrayList<Job> checkJob = jobRepository.checkDuplicateUpdate(jobDTO.getJobName(), jobDTO.getJobCategoryId().getJobCategoryId(), id);
		if (checkJob.isEmpty()) {
			jobDTO.setJobId(tempJob.getJobId());
			jobDTO.setCreatedBy(tempJob.getCreatedBy());
			jobDTO.setCreatedOn(tempJob.getCreatedOn());
		    if (jobDTO.getJobName() == null) {
		    	jobDTO.setJobName(tempJob.getJobName());
		    }
		    
		    tempJob = convertToEntity(jobDTO);
		    
		    jobRepository.save(tempJob);
		    process.put("Status", HttpStatus.OK);
		    process.put("Message", "Job Berhasil Dibuat");
		    process.put("Data", tempJob);
		}else {
			Long categoryId = jobDTO.getJobCategoryId().getJobCategoryId();
			String categoryName = jobCategoryRepository.findById(categoryId).orElse(null).getJobCategoryName();
			process.put("Status", HttpStatus.BAD_REQUEST);
		    process.put("Message", "Job Gagal Diubah, Nama Job : '"+ jobDTO.getJobName() +"' dengan "
			+ "Job Category : '"+ categoryName +"' Sudah Terdaftar");
		}
		return process;
	}

	@GetMapping("/listJobByJobCategory")
	public HashMap<String, Object> listJobCategoryByJobClass(@RequestParam Long jobCategoryId) throws ParseException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		ArrayList<Job> listJobEntity = (ArrayList<Job>) jobRepository.findIdByJobCategoryId(jobCategoryId);

		for (Job item : listJobEntity) {
			HashMap<String, Object> data = new HashMap<>();
			JobDTO jobDTO = modelMapper.map(item, JobDTO.class);
			data.put("jobId", jobDTO.getJobId());
			data.put("jobName", jobDTO.getJobName());
			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;

	}

	@GetMapping("/listJob")
	public HashMap<String, Object> getListJob() throws ParseException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		ArrayList<Job> listJobEntity = (ArrayList<Job>) jobRepository.findAllJobs();

		for (Job item : listJobEntity) {
			HashMap<String, Object> data = new HashMap<>();
			JobDTO jobDTO = modelMapper.map(item, JobDTO.class);
			data.put("jobId", jobDTO.getJobId());
			data.put("jobName", jobDTO.getJobName());
			data.put("jobCategoryId", jobDTO.getJobCategoryId().getJobCategoryId());
			listData.add(data);
		}
		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;

	}

	@PutMapping("deleteJob")
	public HashMap<String, Object> deleteJob(@RequestParam Long jobId, @RequestBody Job body) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		try {
			Job job = jobRepository.findById(jobId).orElse(null);
			JobUserWorker checkJobOnJobUserWorker = jobUserWorkerRepository.findUserWorkersStatusbyJobId(jobId);
			TroubleTicket checkJobIdOnTicket = troubleTicketRepository.findJobIdOnTroubleTicket(jobId);
			if (checkJobIdOnTicket == null && checkJobOnJobUserWorker == null) {
				job.setLastModifiedOn(dateNow);
				job.setDeleted(true);
				job.setLastModifiedBy(body.getLastModifiedBy());
				jobRepository.save(job);
				message = "Job Berhasil Dihapus";
				result.put("Status", HttpStatus.OK);

			} else {
				message = "Job Gagal Dihapus, Karena Data Masih Digunakan";
				result.put("Status", HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			message = e.getMessage();
		}

		result.put("Message", message);
		return result;
	}
	
	private Boolean checkDataAlreadyExist (@Valid @RequestBody JobDTO body) {
		Boolean isFind = false ;
		ArrayList<Job> checking = jobRepository.checkDataIsAlreadyExist(body.getJobName(), body.getJobCategoryId().getJobCategoryId());
		if(checking.isEmpty()) {
			isFind = false;
		}else {
			isFind = true ;
		}
		return isFind;
	}
}
