package com.fsm.repositories.controllers;

import java.util.HashMap;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.TroubleTicketDTO;
import com.fsm.models.TroubleTicket;
import com.fsm.repositories.CodeRepository;
import com.fsm.repositories.JobCategoryReportRepository;
import com.fsm.repositories.TroubleTicketRepository;
import com.fsm.interfaces.Minio;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
public class DetailTicketRepositoryController extends Minio {

	@Autowired
	private TroubleTicketRepository troubleticketRepository;

	@Autowired
	CodeRepository codeRepository;

	@Autowired
	JobCategoryReportRepository jobCategoryReportRepository;

	ModelMapper modelMapper = new ModelMapper();

//	Convert Entity to DTO
	private TroubleTicketDTO convertToDTO(TroubleTicket troubleTicket) {
		TroubleTicketDTO troubleTicketDTO = modelMapper.map(troubleTicket, TroubleTicketDTO.class);
		return troubleTicketDTO;
	}

	@GetMapping("/updateDetailTicket/{id}")
	public HashMap<String, Object> getDataById(@PathVariable(value = "id") long id) throws ErrorResponseException,
			IllegalArgumentException, InsufficientDataException, InternalException, InvalidBucketNameException,
			InvalidResponseException, XmlParserException, InvalidEndpointException, InvalidPortException, IOException,
			InvalidExpiresRangeException, InvalidKeyException, NoSuchAlgorithmException {
		HashMap<String, Object> mapTroubleTicket = new HashMap<String, Object>();
		HashMap<String, Object> mapTemporary = new HashMap<String, Object>();

		TroubleTicket troubleTicket = troubleticketRepository.findById(id).orElse(null);

		TroubleTicketDTO troubleTicketDTO = convertToDTO(troubleTicket);

		Long jobCategoryId = troubleTicketDTO.getJobId().getJobCategoryId().getJobCategoryId();
		mapTemporary.put("ticketId", troubleTicketDTO.getTicketId());
		mapTemporary.put("ticketCode", troubleTicketDTO.getTicketCode());
		mapTemporary.put("ticketTitle", troubleTicketDTO.getTicketTitle());
		mapTemporary.put("companyId", troubleTicketDTO.getBranchId().getCompanyId());
		mapTemporary.put("ticketStatusId", troubleTicketDTO.getTicketStatusId());
		mapTemporary.put("categoryId", troubleTicketDTO.getCategoryId());
		mapTemporary.put("branchId", troubleTicketDTO.getBranchId().getBranchId());
		mapTemporary.put("branchName", troubleTicketDTO.getBranchId().getBranchName());
		mapTemporary.put("picId", troubleTicketDTO.getPicId().getPicId());
		mapTemporary.put("priorityId", troubleTicketDTO.getPriorityId());
		mapTemporary.put("slaId", troubleTicketDTO.getSlaId().getSlaId());
		mapTemporary.put("ticketDate", troubleTicketDTO.getTicketDate());
		mapTemporary.put("ticketTime", troubleTicketDTO.getTicketTime());
		mapTemporary.put("ticketDurationTime", troubleTicketDTO.getTicketDurationTime());
		mapTemporary.put("ticketDueDate", troubleTicketDTO.getTicketDueDate());
		mapTemporary.put("jobId", troubleTicketDTO.getJobId().getJobId());
		mapTemporary.put("jobCategoryId", troubleTicketDTO.getJobId().getJobCategoryId());
		mapTemporary.put("jobClassId", troubleTicketDTO.getJobId().getJobCategoryId().getJobClassId());
		mapTemporary.put("ticketDesc", troubleTicketDTO.getTicketDescription());
		mapTemporary.put("reportId", jobCategoryReportRepository.findByJobCategoryId(jobCategoryId).getReportId());
		mapTemporary.put("fileName", troubleTicketDTO.getFileName());
		mapTemporary.put("filePath", troubleTicketDTO.getFilePath());

		String message;
		if (troubleTicket == null) {
			message = "Data Kosong";
		} else {
			message = "Data Tiket Berdasarkan Id";
		}

		mapTroubleTicket.put("Message", message);
		mapTroubleTicket.put("Data", mapTemporary);

		return mapTroubleTicket;
	}

	@GetMapping("/detailTicket/{id}")
	public HashMap<String, Object> getDetailById(@PathVariable(value = "id") long id) throws ErrorResponseException,
			IllegalArgumentException, InsufficientDataException, InternalException, InvalidBucketNameException,
			InvalidResponseException, XmlParserException, InvalidEndpointException, InvalidPortException, IOException,
			InvalidExpiresRangeException, InvalidKeyException, NoSuchAlgorithmException {
		HashMap<String, Object> mapTroubleTicket = new HashMap<String, Object>();
		HashMap<String, Object> mapTemporary = new HashMap<String, Object>();

		TroubleTicket troubleTicket = troubleticketRepository.findById(id).orElse(null);

		TroubleTicketDTO troubleTicketDTO = convertToDTO(troubleTicket);

		Long jobCategoryId = troubleTicketDTO.getJobId().getJobCategoryId().getJobCategoryId();
		Long priorityId = troubleTicketDTO.getPriorityId();
		Long categoryId = troubleTicketDTO.getCategoryId();
		Long reportId = jobCategoryReportRepository.findByJobCategoryId(jobCategoryId).getReportId();
		mapTemporary.put("ticketId", troubleTicketDTO.getTicketId());
		mapTemporary.put("ticketCode", troubleTicketDTO.getTicketCode());
		mapTemporary.put("ticketTitle", troubleTicketDTO.getTicketTitle());
		mapTemporary.put("customerName", troubleTicketDTO.getBranchId().getCompanyId().getCompanyName());
		mapTemporary.put("branchName", troubleTicketDTO.getBranchId().getBranchName());
		mapTemporary.put("picName", troubleTicketDTO.getPicId().getPicName());
		mapTemporary.put("slaName",
				troubleTicketDTO.getSlaId().getSlaTypeId().getSlaTypeName() + " / "
						+ troubleTicketDTO.getSlaId().getSlaResponseTime() + " Min (Response Time) / "
						+ troubleTicketDTO.getSlaId().getSlaResolutionTime() + " Hour (Resolution Time)");
		mapTemporary.put("priorityName", codeRepository.findById(priorityId).orElse(null).getCodeName());
		mapTemporary.put("ticketDate", troubleTicketDTO.getTicketDate());
		mapTemporary.put("ticketTime", troubleTicketDTO.getTicketTime());
		mapTemporary.put("categoryName", codeRepository.findById(categoryId).orElse(null).getCodeName());
		mapTemporary.put("ticketDurationTime", troubleTicketDTO.getTicketDurationTime());
		mapTemporary.put("jobCategoryName", troubleTicketDTO.getJobId().getJobCategoryId().getJobCategoryName());
		mapTemporary.put("jobClassName",
				troubleTicketDTO.getJobId().getJobCategoryId().getJobClassId().getJobClassName());
		mapTemporary.put("ticketDesc", troubleTicketDTO.getTicketDescription());
		mapTemporary.put("reportName", codeRepository.findById(reportId).orElse(null).getCodeName());
		mapTemporary.put("jobName", troubleTicketDTO.getJobId().getJobName());
		mapTemporary.put("filePath", troubleTicketDTO.getFilePath());
		String message;
		if (troubleTicket == null) {
			message = "Data Kosong";
		} else {
			message = "Data Ticket Berdasarkan Id";
		}

		mapTroubleTicket.put("Message", message);
		mapTroubleTicket.put("Data", mapTemporary);

		return mapTroubleTicket;
	}
}
