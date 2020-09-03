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

import com.fsm.dtos.FailureDTO;
import com.fsm.models.Action;
import com.fsm.models.DispatchReport;
import com.fsm.models.Failure;
import com.fsm.repositories.ActionRepository;
import com.fsm.repositories.DiagnosisRepository;
import com.fsm.repositories.DispatchReportRepository;
import com.fsm.repositories.FailureRepository;

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
public class FailureRepositoryController {

	@Autowired
	FailureRepository failureRepository;

	@Autowired
	FailureService failureService;

	@Autowired
	ActionRepository actionRepository;
	
	@Autowired
	DispatchReportRepository dispatchReportRepository;

	@Autowired
	DiagnosisRepository diagnosisRepository;

	ModelMapper modelMapper = new ModelMapper();

	public FailureDTO convertFailureToDTO(Failure failure) {
		FailureDTO failureDto = modelMapper.map(failure, FailureDTO.class);
		return failureDto;
	}
	private Failure convertToEntity(FailureDTO failureDTO) {
		Failure failure = modelMapper.map(failureDTO, Failure.class);
		return failure;
	}


	@CrossOrigin(allowCredentials = "true")
	@GetMapping("failure")
	public HashMap<String, Object> getFailure(@RequestParam(value = "pageNo") Integer pageNo,
			@RequestParam(value = "pageSize") Integer pageSize, @RequestParam(value = "diagnosisId") Long diagnosisId,
			@RequestParam(value = "filter", defaultValue = "") String filter) {

		HashMap<String, Object> mapResult = new HashMap<String, Object>();
		ArrayList<FailureDTO> listFailure = new ArrayList<FailureDTO>();

		for (Failure failure : failureService.getFailureService(pageNo, pageSize, diagnosisId, filter)) {
			FailureDTO failureDto = convertFailureToDTO(failure);
			listFailure.add(failureDto);
		}

		int total = failureService.getTotalFailureService(pageNo, 50, diagnosisId, filter);

		String message;
		if (listFailure.isEmpty()) {
			message = "Data Kosong";
		} else {
			message = "Data Failure";
		}

		mapResult.put("Message", message);
		mapResult.put("Total", total);
		mapResult.put("Data", listFailure);
		return mapResult;
	}

	@Service
	public class FailureService {

//		Code For get data failure repot
		public List<Failure> getFailureService(Integer pageNo, Integer pageSize, Long diagnosisId, String filter) {
			Pageable paging = PageRequest.of(pageNo, pageSize);

			Slice<Failure> pagedResult = failureRepository.getFailureByDiagnosisId(diagnosisId, paging, filter);

			List<Failure> listFailure = pagedResult.getContent();

			return listFailure;
		}

//		Code For get total data failure repot
		public int getTotalFailureService(Integer pageNo, Integer pageSize, Long diagnosisId, String filter) {
			Pageable paging = PageRequest.of(pageNo, pageSize);

			Slice<Failure> pagedResult = failureRepository.getFailureByDiagnosisId(diagnosisId, paging, filter);

			int totalFailure = pagedResult.getNumberOfElements();

			return totalFailure;
		}
	}

	@PutMapping("failure/deleteFailure")
	public HashMap<String, Object> deleteFailure(@RequestParam Long failureId, @RequestBody Failure body) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		try {
			Failure failure = failureRepository.findById(failureId).orElse(null);
			Action checkFailure = actionRepository.checkFailure(failureId);
			DispatchReport checkReportFailure = dispatchReportRepository.checkingData(failure.getFailureDesc());
			if (checkFailure == null && checkReportFailure == null) {
				failure.setLastModifiedOn(dateNow);
				failure.setDeleted(true);
				failure.setLastModifiedBy(body.getLastModifiedBy());
				failureRepository.save(failure);
				message = "Failure Berhasil Dihapus";
				result.put("Status", HttpStatus.OK);
			} else {
				message = "Failure Tidak Bisa Dihapus, Karena Masih Digunakan";
				result.put("Status", HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			message = e.getMessage();
		}
		result.put("Message", message);
		return result;
	}
	
	@PostMapping("failure/create")
	public HashMap<String, Object> createDataDiagnosis(@Valid @RequestBody FailureDTO body){
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		ModelMapper modelMapper = new ModelMapper();
		Failure failureEntity = modelMapper.map(body, Failure.class);
		Boolean isFind = checkDataAlreadyExist(body);
			if(isFind == false) {
				failureRepository.save(failureEntity);
				body.setFailureId(failureEntity.getFailureId());
				
				result.put("Status", HttpStatus.OK);
				result.put("Message", "Failure Berhasil Dibuat");
				result.put("Data", body);
			}else {
				Long diagnosisId = body.getDiagnosisId().getDiagnosisId();
				String diagnosisDesc = diagnosisRepository.findById(diagnosisId).orElse(null).getDiagnosisDesc();
				result.put("Status", HttpStatus.BAD_REQUEST);
				result.put("Message", "Failure Gagal Dibuat, Nama Failure : '"+ body.getFailureDesc() +"' dengan "
				+ "Diagonis : '"+ diagnosisDesc +"' Sudah Terdaftar");
			}
		return result;
	}
	@PutMapping("failure/update/{id}")
	public HashMap<String, Object> updateData (@PathVariable(value = "id") Long id,@Valid @RequestBody FailureDTO failureDTO){
		HashMap<String, Object> result = new HashMap<String, Object>();
		Failure tempFailure = failureRepository.findById(id).orElse(null);
		ArrayList<Failure> checkData = failureRepository.checkDuplicateData(failureDTO.getFailureDesc(), failureDTO.getDiagnosisId().getDiagnosisId(), id);
			if(checkData.isEmpty()) {
				failureDTO.setFailureId(tempFailure.getFailureId());
				failureDTO.setCreatedBy(tempFailure.getCreatedBy());
				failureDTO.setCreatedOn(tempFailure.getCreatedOn());
				if(failureDTO.getFailureDesc() == null) {
					failureDTO.setFailureDesc(tempFailure.getFailureDesc());
				}
				tempFailure = convertToEntity(failureDTO);
				failureRepository.save(tempFailure);
				
				result.put("Status", HttpStatus.OK);
				result.put("Message", "Failure Berhasil Diubah");
				result.put("Data", tempFailure);
			}else {
				Long diagnosisId = failureDTO.getDiagnosisId().getDiagnosisId();
				String diagnosisDesc = diagnosisRepository.findById(diagnosisId).orElse(null).getDiagnosisDesc();
				result.put("Status", HttpStatus.BAD_REQUEST);
				result.put("Message", "Failure Gagal Diubah, Nama Failure : '"+ failureDTO.getFailureDesc() +"' dengan "
				+ "Diagonis : '"+ diagnosisDesc +"' Sudah Terdaftar");
			}
		return result;
	}
	@GetMapping("failure/all")
	public HashMap<String,Object> getAllFailure(@RequestParam String search, Pageable pageable)throws InvalidKeyException, InvalidEndpointException, InvalidPortException, ErrorResponseException,
	IllegalArgumentException, InsufficientDataException, InternalException, InvalidBucketNameException,
	InvalidResponseException, NoSuchAlgorithmException, XmlParserException, InvalidExpiresRangeException,
	IOException{
		HashMap<String, Object> result = new HashMap<String, Object>();
		List<FailureDTO> listFailure = new ArrayList<FailureDTO>();
		for(Failure tempFailure : failureRepository.getDataFailure(search, pageable)) {
			FailureDTO failureDTO = convertFailureToDTO(tempFailure);
			if(failureDTO.isDeleted() == false) {
				listFailure.add(failureDTO);
			}
		}
		ArrayList<Failure> sizeTotal = (ArrayList<Failure>) failureRepository.findAllFailure();
		String message ;
		if(listFailure.isEmpty()) {
			message = "Data Kosong";
		}else {
			message = "Data Action";
		}
		result.put("Status", HttpStatus.OK);
		result.put("Message", message);
		result.put("Total", sizeTotal.size());
		result.put("Data", listFailure);
		return result;
	}
	private Boolean checkDataAlreadyExist (@Valid @RequestBody FailureDTO body) {
		Boolean isFind = false ;
		ArrayList<Failure> checking = failureRepository.checkDataAlreadyExist(body.getFailureDesc(), body.getDiagnosisId().getDiagnosisId());
			if(checking.isEmpty()) {
				isFind = false ;
			}else {
				isFind = true;
			}
		return isFind;
	}
}
