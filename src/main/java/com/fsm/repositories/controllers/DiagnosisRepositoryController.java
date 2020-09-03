package com.fsm.repositories.controllers;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.DiagnosisDTO;
import com.fsm.models.Diagnosis;
import com.fsm.models.DispatchReport;
import com.fsm.models.Failure;
import com.fsm.repositories.DiagnosisRepository;
import com.fsm.repositories.DispatchReportRepository;
import com.fsm.repositories.FailureRepository;
import com.fsm.repositories.JobCategoryRepository;

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
@RequestMapping("api")
public class DiagnosisRepositoryController {

	ModelMapper modelMapper = new ModelMapper();

	@Autowired
	DiagnosisRepository diagnosisRepository;

	@Autowired
	FailureRepository failureRepository;

	@Autowired
	DispatchReportRepository dispatchReportRepository;

	@Autowired
	JobCategoryRepository jobCategoryRepository;
	
	public DiagnosisDTO convertToDTO(Diagnosis diagnosis) {
		DiagnosisDTO diagnosisDto = modelMapper.map(diagnosis, DiagnosisDTO.class);
		return diagnosisDto;
	}
	private Diagnosis convertToEntity(DiagnosisDTO diagnosisDTO) {
		Diagnosis diagnosis = modelMapper.map(diagnosisDTO, Diagnosis.class);
		return diagnosis;
	}

	@Autowired
	private DiagnosisService service;

	@CrossOrigin(allowCredentials = "true")
	@GetMapping("/diagnosis/{jobCategoryId}")
	public HashMap<String, Object> getCityByFilter(@PathVariable(value = "jobCategoryId") Long jobCategoryId,
			@RequestParam(value = "pageNo") Integer pageNo, @RequestParam(value = "pageSize") Integer pageSize,
			@RequestParam(value = "filter") String filter) {
		HashMap<String, Object> mapResult = new HashMap<String, Object>();
		ArrayList<DiagnosisDTO> listDiagnosisDto = new ArrayList<DiagnosisDTO>();

		for (Diagnosis diagnosis : service.getDiagnosisService(jobCategoryId, pageNo, pageSize, filter)) {
			DiagnosisDTO diagnosisDto = convertToDTO(diagnosis);
			listDiagnosisDto.add(diagnosisDto);
		}

		int total = service.getTotalDiagnosisService(jobCategoryId, pageNo, 50, filter);

		String message;
		if (listDiagnosisDto.isEmpty()) {
			message = "Data is Empty";
		} else {
			message = "Show Data By Filter";
		}

		mapResult.put("Message", message);
		mapResult.put("Total", total);
		mapResult.put("Data", listDiagnosisDto);

		return mapResult;
	}

	@Service
	public class DiagnosisService {

		@Autowired
		DiagnosisRepository diagnosisRepository;

//		Code For get data diagnosis report
		public List<Diagnosis> getDiagnosisService(Long jobCategoryId, Integer pageNo, Integer pageSize,
				String filter) {
			Pageable paging = PageRequest.of(pageNo, pageSize);

			Slice<Diagnosis> pagedResult = diagnosisRepository.getFilterByJobCategoryId(jobCategoryId, filter, paging);

			List<Diagnosis> listDiagnosis = pagedResult.getContent();

			return listDiagnosis;
		}

//		Code For get total data diagnosis report
		public int getTotalDiagnosisService(Long jobCategoryId, Integer pageNo, Integer pageSize, String filter) {
			Pageable paging = PageRequest.of(pageNo, pageSize);

			Slice<Diagnosis> pagedResult = diagnosisRepository.getFilterByJobCategoryId(jobCategoryId, filter, paging);

			int totalDiagnosis = pagedResult.getNumberOfElements();

			return totalDiagnosis;
		}
	}

	@PutMapping("diagnosis/deleteDiagnosis")
	public HashMap<String, Object> deleteDiagnosis(@RequestParam Long diagnosisId, @RequestBody Diagnosis body) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		try {
			Diagnosis diagnosis = diagnosisRepository.findById(diagnosisId).orElse(null);
			Failure checkDiagnosis = failureRepository.findDiagnosis(diagnosisId);
			DispatchReport checkReportDiagnosis = dispatchReportRepository.checkingData(diagnosis.getDiagnosisDesc());
			if (checkDiagnosis == null && checkReportDiagnosis == null) {
				diagnosis.setLastModifiedOn(dateNow);
				diagnosis.setDeleted(true);
				diagnosis.setLastModifiedBy(body.getLastModifiedBy());
				diagnosisRepository.save(diagnosis);
				message = "Diagnosis Berhasil Dihapus";
				result.put("Status", HttpStatus.OK);
			} else {
				message = "Diagnosis Tidak Bisa Dihapus, Karena Masih Digunakan";
				result.put("Status", HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			message = e.getMessage();
		}
		result.put("Message", message);
		return result;
	}
	
	@PostMapping("diagnosis/create")
	public HashMap<String, Object> createDataDiagnosis(@Valid @RequestBody DiagnosisDTO body){
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		ModelMapper modelMapper = new ModelMapper();
		Diagnosis diagnosisEntity = modelMapper.map(body, Diagnosis.class);
		Boolean isFind = checkDataAlreadyExist(body);
			if(isFind == false) {
				diagnosisRepository.save(diagnosisEntity);
				body.setDiagnosisId(diagnosisEntity.getDiagnosisId());
				
				result.put("Status", HttpStatus.OK);
				result.put("Message", "Diagnosis Berhasil Dibuat");
				result.put("Data", body);
			}else {
				Long jobCategoryId = body.getJobCategoryId().getJobCategoryId();
				String jobCategoryName = jobCategoryRepository.findById(jobCategoryId).orElse(null).getJobCategoryName();
				result.put("Status", HttpStatus.BAD_REQUEST);
				result.put("Message", "Diagnosis Gagal Dibuat, Nama Diagnosis : '"+ body.getDiagnosisDesc() +"' dengan "
				+ "Job Category : '"+ jobCategoryName +"' Sudah Terdaftar");
			}
		return result;
	}
	@PutMapping("diagnosis/update/{id}")
	public HashMap<String, Object> updateData (@PathVariable(value = "id") Long id,@Valid @RequestBody DiagnosisDTO diagnosisDTO){
		HashMap<String, Object> result = new HashMap<String, Object>();
		Diagnosis tempDiagnosis = diagnosisRepository.findById(id).orElse(null);
		ArrayList<Diagnosis> checkData = diagnosisRepository.checkDuplicateData(diagnosisDTO.getDiagnosisDesc(), diagnosisDTO.getJobCategoryId().getJobCategoryId(), id);
			if(checkData.isEmpty()) {
				diagnosisDTO.setDiagnosisId(tempDiagnosis.getDiagnosisId());
				diagnosisDTO.setCreatedBy(tempDiagnosis.getCreatedBy());
				diagnosisDTO.setCreatedOn(tempDiagnosis.getCreatedOn());
				if(diagnosisDTO.getDiagnosisDesc() == null) {
					diagnosisDTO.setDiagnosisDesc(tempDiagnosis.getDiagnosisDesc());
				}
				tempDiagnosis = convertToEntity(diagnosisDTO);
				diagnosisRepository.save(tempDiagnosis);
				
				result.put("Status", HttpStatus.OK);
				result.put("Message", "Diagnosis Berhasil Diubah");
				result.put("Data", tempDiagnosis);
			}else {
				Long jobCategoryId = diagnosisDTO.getJobCategoryId().getJobCategoryId();
				String jobCategoryName = jobCategoryRepository.findById(jobCategoryId).orElse(null).getJobCategoryName();
				result.put("Status", HttpStatus.BAD_REQUEST);
				result.put("Message", "Diagnosis Gagal Dibuat, Nama Diagnosis : '"+ diagnosisDTO.getDiagnosisDesc() +"' dengan "
				+ "Job Category : '"+ jobCategoryName +"' Sudah Terdaftar");
			}
		return result;
	}
	
	@GetMapping("diagnosis/all")
	public HashMap<String, Object> getAllDiagnosis (@RequestParam String search, Pageable pageable)throws InvalidKeyException, InvalidEndpointException, InvalidPortException, ErrorResponseException,
	IllegalArgumentException, InsufficientDataException, InternalException, InvalidBucketNameException,
	InvalidResponseException, NoSuchAlgorithmException, XmlParserException, InvalidExpiresRangeException,
	IOException{
		HashMap<String, Object> result = new HashMap<String, Object>();
		List<DiagnosisDTO> listDiagnosis = new ArrayList<DiagnosisDTO>();
			for(Diagnosis tempDiagnosis : diagnosisRepository.getDataDiagnosis(search, pageable)) {
				DiagnosisDTO diagnosisDTO = convertToDTO(tempDiagnosis);
				if(diagnosisDTO.isDeleted() == false) {
					listDiagnosis.add(diagnosisDTO);
				}
			}
		ArrayList<Diagnosis> sizeTotal = (ArrayList<Diagnosis>) diagnosisRepository.findAllDiagnosis();
		String message ;
		if(listDiagnosis.isEmpty()) {
			message = "Data Kosong";
		}else {
			message = "Data Action";
		}
		result.put("Status", HttpStatus.OK);
		result.put("Message", message);
		result.put("Total", sizeTotal.size());
		result.put("Data", listDiagnosis);
		return result;
	}
	private Boolean checkDataAlreadyExist (@Valid @RequestBody DiagnosisDTO body) {
		Boolean isFind = false ;
		ArrayList<Diagnosis> checking = diagnosisRepository.checkData(body.getDiagnosisDesc(), body.getJobCategoryId().getJobCategoryId());
			if(checking.isEmpty()) {
				isFind = false ;
			}else {
				isFind = true;
			}
		return isFind;
	}
}
