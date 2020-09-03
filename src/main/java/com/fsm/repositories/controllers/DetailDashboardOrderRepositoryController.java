package com.fsm.repositories.controllers;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.HistoryDTO;
import com.fsm.interfaces.Minio;
import com.fsm.models.DispatchImageReport;
import com.fsm.models.DispatchReport;
import com.fsm.models.History;
import com.fsm.repositories.DispatchImageReportRepository;
import com.fsm.repositories.DispatchReportRepository;
import com.fsm.repositories.HistoryRepository;

import io.minio.MinioClient;
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
public class DetailDashboardOrderRepositoryController extends Minio {

	@Autowired
	HistoryRepository historyRepository;

	@Autowired
	DispatchReportRepository dispatchReportRepository;

	@Autowired
	DispatchImageReportRepository dispatchImageReportRepository;

	ModelMapper modelMapper = new ModelMapper();

//	Convert Entity to DTO
	private HistoryDTO convertToDTO(History history) {
		HistoryDTO historyDTO = modelMapper.map(history, HistoryDTO.class);
		return historyDTO;
	}

	@GetMapping("/detailFinishOrder/{id}")
	public HashMap<String, Object> getFinishDataById(@PathVariable(value = "id") long id) throws InvalidKeyException,
			ErrorResponseException, IllegalArgumentException, InsufficientDataException, InternalException,
			InvalidBucketNameException, InvalidResponseException, NoSuchAlgorithmException, XmlParserException,
			InvalidEndpointException, InvalidPortException, IOException, InvalidExpiresRangeException {
		HashMap<String, Object> mapFinishOrder = new HashMap<String, Object>();
		HashMap<String, Object> mapTemporary = new HashMap<String, Object>();

		History history = historyRepository.getDetailFinishedOrder(id);

		HistoryDTO historyDTO = convertToDTO(history);

		Long orderId = historyDTO.getOrderId().getOrderId();
		mapTemporary.put("historyId", historyDTO.getHistoryId());
		mapTemporary.put("ticketCode", historyDTO.getOrderId().getTicketId().getTicketCode());
		mapTemporary.put("ticketTitle", historyDTO.getOrderId().getTicketId().getTicketTitle());
		mapTemporary.put("companyName",
				historyDTO.getOrderId().getTicketId().getBranchId().getCompanyId().getCompanyName());
		mapTemporary.put("branchName",
				historyDTO.getOrderId().getTicketId().getBranchId().getBranchName());
		mapTemporary.put("picName", historyDTO.getOrderId().getTicketId().getPicId().getPicName());
		mapTemporary.put("workerName", historyDTO.getOrderId().getUserId().getUserFullName());
		mapTemporary.put("startJob", historyDTO.getOrderId().getStartJob());
		mapTemporary.put("endJob", historyDTO.getOrderId().getEndJob());
		mapTemporary.put("description", historyDTO.getOrderId().getDispatchDesc());
		DispatchReport dispatchReport = dispatchReportRepository.findByOrderId(orderId);
		if (dispatchReport != null) {
			mapTemporary.put("dispatchReportRating", dispatchReport.getDispatchReportRating());
			Long dispatchReportId = dispatchReportRepository.findByOrderId(orderId).getDispatchReportId();
			mapTemporary.put("listImageReport", getReportImage(dispatchReportId));
			mapTemporary.put("signatureReport", getSignature(dispatchReportId));
			mapTemporary.put("dispatchReportDiagnostic", dispatchReport.getDispatchReportDiagnostic());
			mapTemporary.put("dispatchReportFailure", dispatchReport.getDispatchReportReportedFailure());
			mapTemporary.put("dispatchReportAction", dispatchReport.getDispatchReportAction());
		} else {
			mapTemporary.put("dispatchReportRating", 0);
			mapTemporary.put("listImageReport", "-");
			mapTemporary.put("signatureReport", "-");
			mapTemporary.put("dispatchReportDiagnostic", "-");
			mapTemporary.put("dispatchReportFailure", "-");
			mapTemporary.put("dispatchReportAction", "-");
		}

		String message;
		if (history == null) {
			message = "Data Kosong";
		} else {
			message = "Data Finish Order Berdasarkan Id";
		}

		mapFinishOrder.put("Message", message);
		mapFinishOrder.put("Data", mapTemporary);

		return mapFinishOrder;
	}

	public ArrayList<HashMap<String, Object>> getReportImage(Long dispatchReportId) throws InvalidEndpointException,
			InvalidPortException, InvalidKeyException, ErrorResponseException, IllegalArgumentException,
			InsufficientDataException, InternalException, InvalidBucketNameException, InvalidResponseException,
			NoSuchAlgorithmException, XmlParserException, IOException, InvalidExpiresRangeException {
		ArrayList<HashMap<String, Object>> listImage = new ArrayList<HashMap<String, Object>>();
		MinioClient minioClient = minio();
		for (DispatchImageReport dispatchImageReport : dispatchImageReportRepository
				.getImageReportOfDispatchImageReportByDispatchReportId(dispatchReportId)) {
			HashMap<String, Object> hashMapUrl = new HashMap<String, Object>();
			if (dispatchImageReport != null) {
				hashMapUrl.put("imageUrl",
						minioClient.presignedGetObject(bucketName, dispatchImageReport.getImageReportPath()));
				listImage.add(hashMapUrl);
			} else {
				hashMapUrl.put("imageUrl", "");
				listImage.add(hashMapUrl);
			}

		}
		return listImage;
	}

	public String getSignature(Long dispatchReportId) throws InvalidEndpointException, InvalidPortException,
			InvalidKeyException, ErrorResponseException, IllegalArgumentException, InsufficientDataException,
			InternalException, InvalidBucketNameException, InvalidResponseException, NoSuchAlgorithmException,
			XmlParserException, IOException, InvalidExpiresRangeException {
		MinioClient minioClient = minio();
		DispatchImageReport dispatchImageReport = dispatchImageReportRepository
				.getSignatureOfDispatchImageReportByDispatchReportId(dispatchReportId);

		if (dispatchImageReport == null) {
			return null;
		} else {
			return minioClient.presignedGetObject(bucketName, dispatchImageReport.getImageReportPath());
		}
	}

	@GetMapping("/detailCanceledOrder/{id}")
	public HashMap<String, Object> getCancelDataById(@PathVariable(value = "id") long id) {
		HashMap<String, Object> mapCanceledOrder = new HashMap<String, Object>();
		HashMap<String, Object> mapTemporary = new HashMap<String, Object>();

		History history = historyRepository.getDetailCanceledOrder(id);

		HistoryDTO historyDTO = convertToDTO(history);
		if (historyDTO.getDispatchStatus().equals("Canceled")) {
			Timestamp canceled = historyDTO.getCreatedOn();

			mapTemporary.put("historyId", historyDTO.getHistoryId());
			mapTemporary.put("ticketCode", historyDTO.getOrderId().getTicketId().getTicketCode());
			mapTemporary.put("ticketTitle", historyDTO.getOrderId().getTicketId().getTicketTitle());
			mapTemporary.put("companyName",
					historyDTO.getOrderId().getTicketId().getBranchId().getCompanyId().getCompanyName());
			mapTemporary.put("branchName",
					historyDTO.getOrderId().getTicketId().getBranchId().getBranchName());
			mapTemporary.put("picName", historyDTO.getOrderId().getTicketId().getPicId().getPicName());
			mapTemporary.put("workerName", historyDTO.getOrderId().getUserId().getUserFullName());
			mapTemporary.put("startJob", historyDTO.getOrderId().getStartJob());
			mapTemporary.put("canceledJob", canceled);
			mapTemporary.put("reason", historyDTO.getReason());

			String message;
			if (history == null) {
				message = "Data Kosong";
			} else {
				message = "Data Canceled Order Berdasarkan Id";
			}

			mapCanceledOrder.put("Message", message);
			mapCanceledOrder.put("Data", mapTemporary);
		}

		return mapCanceledOrder;
	}
}
