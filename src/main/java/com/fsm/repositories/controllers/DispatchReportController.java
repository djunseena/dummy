package com.fsm.repositories.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsm.dtos.DispatchReportDTO;
import com.fsm.interfaces.Minio;
import com.fsm.models.Dispatch;
import com.fsm.models.DispatchImageReport;
import com.fsm.models.DispatchReport;
import com.fsm.models.History;
import com.fsm.models.TroubleTicket;
import com.fsm.repositories.CodeRepository;
import com.fsm.repositories.DispatchImageReportRepository;
import com.fsm.repositories.DispatchReportRepository;
import com.fsm.repositories.DispatchRepository;
import com.fsm.repositories.HistoryRepository;
import com.fsm.repositories.TroubleTicketRepository;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
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
public class DispatchReportController extends Minio {

	@Autowired
	DispatchImageReportRepository dispatchImageReportRepository;

	@Autowired
	DispatchReportRepository dispatchReportRepository;

	@Autowired
	DispatchRepository dispatchRepository;

	@Autowired
	HistoryRepository historyRepository;

	@Autowired
	TroubleTicketRepository troubleTicketRepository;

	@Autowired
	CodeRepository codeRepository;

	ModelMapper modelMapper = new ModelMapper();

//	Convert entity to DTO
	public DispatchReportDTO convertToDTO(DispatchReport dispatchReport) {
		DispatchReportDTO dispatchReportDto = modelMapper.map(dispatchReport, DispatchReportDTO.class);
		return dispatchReportDto;
	}

//	Get DispatchReport data by ID
	@GetMapping("/GetDispatchReport/id/{id}")
	public HashMap<String, Object> getDispatchReportDataById(@PathVariable(value = "id") Long DispatchReportId) {

		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		HashMap<Object, Object> dataDispatchReport = new HashMap<Object, Object>();
		DispatchReport dispatchReport = dispatchReportRepository.findById(DispatchReportId).orElse(null);

		String message;
		String status;
		if (dispatchReport == null) {
			status = "Read Failed!";
			message = "Data is empty";
		} else {
			status = "Read Success!";
			message = "Show Data By Id";
		}

		dataDispatchReport.put("OrderID", dispatchReport.getOrderId().getOrderId());
		dataDispatchReport.put("dispatchReportDiagnostic", dispatchReport.getDispatchReportDiagnostic());
		dataDispatchReport.put("dispatchReportReportedFailure", dispatchReport.getDispatchReportReportedFailure());
		dataDispatchReport.put("dispatchReportAction", dispatchReport.getDispatchReportAction());
		dataDispatchReport.put("dispatchReportNote", dispatchReport.getDispatchReportNote());
		dataDispatchReport.put("dispatchReportRating", dispatchReport.getDispatchReportRating());

		showHashMap.put("Status", status);
		showHashMap.put("Message", message);
		showHashMap.put("Data", dispatchReport);

		return showHashMap;
	}

//	Get DispatchReport data by Order ID
	@GetMapping("/GetDispatchReport/")
	public HashMap<String, Object> getDispatchReportByOrderId(@RequestParam(value = "OrderId") Long OrderId)
			throws InvalidKeyException, InvalidEndpointException, InvalidPortException, ErrorResponseException,
			IllegalArgumentException, InsufficientDataException, InternalException, InvalidBucketNameException,
			InvalidResponseException, NoSuchAlgorithmException, XmlParserException, InvalidExpiresRangeException,
			IOException {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		HashMap<Object, Object> dataDispatchReport = new HashMap<Object, Object>();
		DispatchReport dispatchReport = dispatchReportRepository.findByOrderId(OrderId);

		String message;
		String status;
		if (dispatchReport == null) {
			status = "Read Failed!";
			message = "Data is empty";
		} else {
			status = "Read Success!";
			message = "Show Data By Order Id";
		}

		dataDispatchReport.put("OrderID", dispatchReport.getOrderId().getOrderId());
		dataDispatchReport.put("dispatchReportDiagnostic", dispatchReport.getDispatchReportDiagnostic());
		dataDispatchReport.put("dispatchReportReportedFailure", dispatchReport.getDispatchReportReportedFailure());
		dataDispatchReport.put("dispatchReportAction", dispatchReport.getDispatchReportAction());
		dataDispatchReport.put("dispatchReportNote", dispatchReport.getDispatchReportNote());
		dataDispatchReport.put("dispatchReportRating", dispatchReport.getDispatchReportRating());
		dataDispatchReport.put("dispatchListImage", getReportImage(dispatchReport.getDispatchReportId()));
		dataDispatchReport.put("dispatchSignatureImage", getSignature(dispatchReport.getDispatchReportId()));

		showHashMap.put("Status", status);
		showHashMap.put("Message", message);
		showHashMap.put("Data", dataDispatchReport);

		return showHashMap;
	}

//	Get all list data DispatchReport
	@GetMapping("/GetDispatchReport/all")
	public HashMap<String, Object> getListDispatchReport() {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		ArrayList<DispatchReportDTO> listDispatchReportDto = new ArrayList<DispatchReportDTO>();

		for (DispatchReport dispatchReport : dispatchReportRepository.findAll()) {
			DispatchReportDTO dispatchReportDto = convertToDTO(dispatchReport);
			listDispatchReportDto.add(dispatchReportDto);
		}

		String message;
		String status;
		if (listDispatchReportDto.isEmpty()) {
			status = "Read Failed!";
			message = "Data is Empty";
		} else {
			status = "Read Success!";
			message = "Show All Data";
		}
		showHashMap.put("Status", status);
		showHashMap.put("Message", message);
		showHashMap.put("Total Data", listDispatchReportDto.size());
		showHashMap.put("Data", listDispatchReportDto);

		return showHashMap;
	}

//	Post Data DispatchReport
	@PostMapping("dispatchReport/add/")
	public HashMap<String, Object> createDispatchReportNew(
			@RequestParam(value = "file") ArrayList<MultipartFile> listFile,
			@RequestParam(value = "imageReport") String imageReport,
			@RequestParam(value = "dataReport") String dataReport)
			throws InvalidKeyException, InvalidEndpointException, InvalidPortException, ErrorResponseException,
			IllegalArgumentException, InsufficientDataException, InternalException, InvalidBucketNameException,
			InvalidResponseException, NoSuchAlgorithmException, XmlParserException, IOException {

		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		boolean isValid = false;
		boolean isExist = false;

		String message = "";
		String status = "";

		ObjectMapper mapper = new ObjectMapper();

		DispatchReport dispatchReport = mapper.readValue(dataReport, DispatchReport.class);

		if (dispatchReportRepository.findAll().isEmpty()) {
			isValid = true;
		} else {
			isExist = validationDispatchReport(dispatchReport);
		}

		if (isExist) {
			isValid = false;
			status = "Failed!";
			message = "Data gagal ditambahkan. Report untuk Order ID " + dispatchReport.getOrderId().getOrderId()
					+ " sudah ada!.";
		} else {
			isValid = true;
		}
		if (isValid) {
			dispatchReport.setCreatedOn(dateNow);
			dispatchReport.setLastModifiedOn(dateNow);
			dispatchReport.setCreatedBy(1);
			dispatchReport.setLastModifiedBy(1);
			dispatchReport = dispatchReportRepository.save(dispatchReport);
			postToMinioMore(dispatchReport.getDispatchReportId(), listFile, imageReport);
			status = "Success!";
			message = "Data berhasil ditambahkan!";
		}

//    	Create New History With Status Reported
		History history = new History();

		history.setOrderId(dispatchReport.getOrderId());
		history.setDispatchAction(27);
		history.setReason(null);
		history.setDispatchStatus(codeRepository.findByCodeId(17).getCodeName());
		history.setCreatedOn(dateNow);
		history.setCreatedBy(1);
		historyRepository.save(history);

//		Find Order
		Dispatch dispatch = dispatchRepository.findById(dispatchReport.getOrderId().getOrderId()).orElse(null);

//		Find Trouble Ticket And Update Ticket Status to 30
		TroubleTicket troubleTicket = troubleTicketRepository.findById(dispatch.getTicketId().getTicketId())
				.orElse(null);
		troubleTicket.setTicketStatusId(30);
		troubleTicket.setLastModifiedOn(dateNow);
		troubleTicketRepository.save(troubleTicket);

		showHashMap.put("Status", status);
		showHashMap.put("Message", message);

		return showHashMap;
	}

	public boolean validationDispatchReport(DispatchReport dispatchReport) {
		boolean isExist = false;
		for (DispatchReport list : dispatchReportRepository.findAll()) {
			if (list.getOrderId().getOrderId() == dispatchReport.getOrderId().getOrderId()) {
				isExist = true;
			}
		}
		return isExist;
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

	public ArrayList<HashMap<String, Object>> getReportImage(Long dispatchReportId) throws InvalidEndpointException,
			InvalidPortException, InvalidKeyException, ErrorResponseException, IllegalArgumentException,
			InsufficientDataException, InternalException, InvalidBucketNameException, InvalidResponseException,
			NoSuchAlgorithmException, XmlParserException, IOException, InvalidExpiresRangeException {
		ArrayList<HashMap<String, Object>> listImage = new ArrayList<HashMap<String, Object>>();
		MinioClient minioClient = minio();
		for (DispatchImageReport dispatchImageReport : dispatchImageReportRepository
				.getImageReportOfDispatchImageReportByDispatchReportId(dispatchReportId)) {
			HashMap<String, Object> hashMapUrl = new HashMap<String, Object>();
			hashMapUrl.put("imageUrl",
					minioClient.presignedGetObject(bucketName, dispatchImageReport.getImageReportPath()));
			listImage.add(hashMapUrl);
		}
		return listImage;
	}

	public void postToMinioMore(@PathVariable(value = "dispatchReportId") Long dispatchReportId,
			@RequestParam(value = "file") ArrayList<MultipartFile> listFile,
			@RequestParam(value = "imageReport") String textObject)
			throws InvalidEndpointException, InvalidPortException, InvalidKeyException, ErrorResponseException,
			IllegalArgumentException, InsufficientDataException, InternalException, InvalidBucketNameException,
			InvalidResponseException, NoSuchAlgorithmException, XmlParserException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		ArrayList<DispatchImageReport> listData = new ArrayList<DispatchImageReport>();
		DispatchImageReport[] imageReportList = mapper.readValue(textObject, DispatchImageReport[].class);
		for (MultipartFile file : listFile) {
			DispatchImageReport imageReport = imageReportList[listFile.indexOf(file)];
			String pathName = "";
			if (imageReport.getImageReportType() == 37) {
				pathName = "signature";
			} else if (imageReport.getImageReportType() == 38) {
				pathName = "report";
			}
			pathName += "/" + file.getOriginalFilename();

			Path filePath = Files.createTempFile("file", ".jpg");
			file.transferTo(filePath);
			MinioClient minioClient = minio();
			PutObjectOptions options = new PutObjectOptions(file.getSize(), -1);
			minioClient.putObject(bucketName, pathName, filePath.toString(), options);
			Files.delete(filePath);

			imageReport.setImageReportPath(pathName);
			imageReport.setDispatchReportId(dispatchReportRepository.findById(dispatchReportId).get());
			imageReport = dispatchImageReportRepository.save(imageReport);
			listData.add(imageReport);
		}
	}
}
