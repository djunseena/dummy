package com.fsm.repositories.controllers;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.UsersDTO;
import com.fsm.models.Users;
import com.fsm.repositories.TroubleTicketRepository;
import com.fsm.repositories.UsersRepository;
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
@RequestMapping("/api")
public class ListWorkerRepositoryController extends Minio {

	@Autowired
	UsersRepository usersRepository;

	@Autowired
	TroubleTicketRepository troubleTicketRepository;

	@GetMapping("/listWorkerForDispatch")
	public HashMap<String, Object> showListWorkerForDispatch(@RequestParam Long ticketId,
			@RequestParam String dispatchDate, @RequestParam String dispatchTime, @RequestParam Long jobId)
			throws ParseException, InvalidKeyException, ErrorResponseException, IllegalArgumentException,
			InsufficientDataException, InternalException, InvalidBucketNameException, InvalidResponseException,
			NoSuchAlgorithmException, XmlParserException, InvalidEndpointException, InvalidPortException, IOException,
			InvalidExpiresRangeException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();
		String dispatch = dispatchDate + "T" + dispatchTime;
		LocalDateTime dispatchDateTime = LocalDateTime.parse(dispatch);
		Timestamp dispatchTimestamp = Timestamp.valueOf(dispatchDateTime);

		ArrayList<Users> listWorkerEntity = (ArrayList<Users>) usersRepository
				.getAllWorkerForDispatchPrimary(dispatchTimestamp, ticketId, jobId);
		for (Users item : listWorkerEntity) {
			HashMap<String, Object> data = new HashMap<>();
			UsersDTO usersDTO = modelMapper.map(item, UsersDTO.class);
			String urlImage = "";
			data.put("workerId", usersDTO.getUserId());
			data.put("workerName", usersDTO.getUserFullName());
			data.put("workerAddress", usersDTO.getUserAddress());
			data.put("workerAddressDetail", usersDTO.getUserAddressDetail());
			if (usersDTO.getUserImage() != null) {
				urlImage = minio().presignedGetObject(bucketName, usersDTO.getUserImage());
			}
			data.put("imageUrl", urlImage);
			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;

	}

	@GetMapping("/listWorkerForDispatchBoth")
	public HashMap<String, Object> showListWorkerForDispatchBoth(@RequestParam Long ticketId,
			@RequestParam Date dispatchDate, @RequestParam Time dispatchTime, @RequestParam Long jobId)
			throws ParseException, InvalidKeyException, ErrorResponseException, IllegalArgumentException,
			InsufficientDataException, InternalException, InvalidBucketNameException, InvalidResponseException,
			NoSuchAlgorithmException, XmlParserException, InvalidEndpointException, InvalidPortException, IOException,
			InvalidExpiresRangeException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();
		String dispatch = dispatchDate + "T" + dispatchTime;
		LocalDateTime dispatchDateTime = LocalDateTime.parse(dispatch);
		Timestamp dispatchTimestamp = Timestamp.valueOf(dispatchDateTime);

		ArrayList<Users> listWorkerEntity = (ArrayList<Users>) usersRepository
				.getAllWorkerForDispatchByBoth(dispatchTimestamp, ticketId, jobId);
		for (Users item : listWorkerEntity) {
			HashMap<String, Object> data = new HashMap<>();
			UsersDTO usersDTO = modelMapper.map(item, UsersDTO.class);
			String urlImage = "";
			data.put("workerId", usersDTO.getUserId());
			data.put("workerName", usersDTO.getUserFullName());
			data.put("workerAddress", usersDTO.getUserAddress());
			data.put("workerAddressDetail", usersDTO.getUserAddressDetail());
			if (usersDTO.getUserImage() != null) {
				urlImage = minio().presignedGetObject(bucketName, usersDTO.getUserImage());
			}
			data.put("imageUrl", urlImage);
			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;

	}

}