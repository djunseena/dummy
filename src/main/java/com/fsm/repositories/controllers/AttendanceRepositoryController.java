package com.fsm.repositories.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.controllers.UserWorkerStatusController;
import com.fsm.models.Attendance;
import com.fsm.models.UserWorkerStatus;
import com.fsm.models.Users;
import com.fsm.repositories.AttendanceRepository;
import com.fsm.repositories.UserWorkerStatusRepository;
import com.fsm.repositories.UsersRepository;

import lombok.Data;
import lombok.NoArgsConstructor;

@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping("api")
public class AttendanceRepositoryController {

	@Autowired
	UsersRepository usersRepository;

	@Autowired
	AttendanceRepository attendanceRepository;

	@Autowired
	UserWorkerStatusRepository userWorkerStatusRepository;

	@Autowired
	UserWorkerStatusController userWorkerStatusController;

	@PostMapping("/checkIn")
	public HashMap<String, Object> userCheckin(@Valid @RequestBody UserAttendance userAttendance) throws Exception {

		HashMap<String, Object> mapResult = new HashMap<String, Object>();

		String status = "";
		String message = "";
		LocalDateTime localNow = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		Timestamp dateNow = Timestamp.valueOf(formatter.format(localNow));

		try {
//    		Update latitude & longitude in Users Table
			Users users = usersRepository.findById(userAttendance.getUsers().getUserId()).orElse(null);
			users.setUserLatitude(userAttendance.getLatitute());
			users.setUserLongatitude(userAttendance.getLongitude());
			users.setLastModifiedOn(dateNow);
			users.setLastModifiedBy(userAttendance.getUsers().getUserId());
			usersRepository.save(users);

//    		Update user status in User_Worker_Status Table
			UserWorkerStatus userWorkerStatus = userWorkerStatusRepository
					.findByWorkerId(userAttendance.getUsers().getUserId());
			userWorkerStatus.setStatus(1);
			userWorkerStatus.setLastModifiedBy(userAttendance.getUsers().getUserId());
			userWorkerStatus.setLastModifiedOn(dateNow);
			userWorkerStatusRepository.save(userWorkerStatus);

//	    	Insert to Attendance Table
			Attendance attendance = new Attendance();
			attendance.setUserId(userAttendance.getUsers());
			attendance.setCheckIn(dateNow);
			attendance.setCheckInLat(userAttendance.getLatitute());
			attendance.setCheckInLong(userAttendance.getLongitude());
			attendance.setCreatedOn(dateNow);
			attendance.setCreateadBy(userAttendance.getUsers().getUserId());
			attendance.setLastModifiedOn(dateNow);
			attendance.setLastModifiedBy(userAttendance.getUsers().getUserId());
			attendance.setDeleted(false);
			attendanceRepository.save(attendance);

			status = "Success!";
			message = "Check-In Berhasil";
		} catch (Exception e) {
			status = "Failed!";
			message = "Check-In Gagal : " + e;
		}

		mapResult.put("Status", status);
		mapResult.put("Message", message);

		return mapResult;
	}

	@PutMapping("/checkOut")
	public HashMap<String, Object> userCheckout(@Valid @RequestBody UserAttendance userAttendance)
			throws MessagingException, IOException {

		HashMap<String, Object> mapResult = new HashMap<String, Object>();

		String status = "";
		String message = "";
		LocalDateTime localNow = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		Timestamp dateNow = Timestamp.valueOf(formatter.format(localNow));

		try {
//    		Update latitude & longitude in Users Table
			Users users = usersRepository.findById(userAttendance.getUsers().getUserId()).orElse(null);
			users.setUserLatitude(userAttendance.getLatitute());
			users.setUserLongatitude(userAttendance.getLongitude());
			users.setLastModifiedOn(dateNow);
			users.setLastModifiedBy(userAttendance.getUsers().getUserId());
			usersRepository.save(users);

//    		Update user status in User_Worker_Status Table
			UserWorkerStatus userWorkerStatus = userWorkerStatusRepository
					.findByWorkerId(userAttendance.getUsers().getUserId());
			userWorkerStatus.setStatus(3);
			userWorkerStatus.setLastModifiedBy(userAttendance.getUsers().getUserId());
			userWorkerStatus.setLastModifiedOn(dateNow);
			userWorkerStatusRepository.save(userWorkerStatus);

//	    	Insert to Attendance Table
			Attendance attendance = attendanceRepository
					.findCheckinAttendanceByUser(userAttendance.getUsers().getUserId());
			attendance.setUserId(userAttendance.getUsers());
			attendance.setCheckOut(dateNow);
			attendance.setCheckOutLat(userAttendance.getLatitute());
			attendance.setCheckOutLong(userAttendance.getLongitude());
			attendance.setLastModifiedOn(dateNow);
			attendance.setLastModifiedBy(userAttendance.getUsers().getUserId());
			attendanceRepository.save(attendance);

			status = "Success!";
			message = "Check-Out Berhasil";
		} catch (Exception e) {
			status = "Failed!";
			message = "Check-Out Gagal : " + e;
		}

		mapResult.put("Status", status);
		mapResult.put("Message", message);

		return mapResult;
	}

	@PutMapping("/periodicUpdateAttendance")
	public HashMap<String, Object> periodicUpdatesUserLocation(@Valid @RequestBody UserAttendance userAttendance)
			throws MessagingException, IOException {

		HashMap<String, Object> mapResult = new HashMap<String, Object>();

		String status = "";
		String message = "";
		LocalDateTime localNow = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		Timestamp dateNow = Timestamp.valueOf(formatter.format(localNow));

		try {
//    		Update latitude & longitude in Users Table
			Users users = usersRepository.findById(userAttendance.getUsers().getUserId()).orElse(null);
			users.setUserLatitude(userAttendance.getLatitute());
			users.setUserLongatitude(userAttendance.getLongitude());
			users.setLastModifiedOn(dateNow);
			users.setLastModifiedBy(userAttendance.getUsers().getUserId());
			usersRepository.save(users);

			status = "Success!";
			message = "Update Latitude dan Longitude Berhasil";
		} catch (Exception e) {
			status = "Failed!";
			message = "Update Latitude dan Longitude Gagal : " + e;
		}

		mapResult.put("Status", status);
		mapResult.put("Message", message);

		return mapResult;
	}

	@GetMapping("/checkAttendance")
	public HashMap<String, Object> checkAttendance(@RequestParam Long userId, @RequestParam String date)
			throws MessagingException, IOException, ParseException {

		HashMap<String, Object> mapResult = new HashMap<String, Object>();

		boolean isCheckin;
		Date checkIn = new Date();
		Date checkOut = new Date();

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date attendanceDate = dateFormat.parse(date);

		try {
			Attendance attendance = attendanceRepository.checkUserCheckin(userId, attendanceDate);

			if (attendance.getCheckIn() != null) {
				checkIn.setTime(attendance.getCheckIn().getTime());
				String stringCheckIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(checkIn);
				mapResult.put("checkIn", stringCheckIn);

				if (attendance.getCheckOut() != null) {
					checkOut.setTime(attendance.getCheckOut().getTime());
					String stringCheckOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(checkOut);
					mapResult.put("checkOut", stringCheckOut);
				} else {
					mapResult.put("checkOut", null);
				}
			} else {
				mapResult.put("checkIn", null);
			}

			isCheckin = true;
		} catch (Exception e) {
			isCheckin = false;

			mapResult.put("checkIn", null);
			mapResult.put("checkOut", null);
		}

		mapResult.put("isCheckin", isCheckin);

		return mapResult;
	}

	@DeleteMapping("/deleteAttendance")
	public HashMap<String, Object> deleteAttendance(@RequestParam Long userId, @RequestParam String date)
			throws MessagingException, IOException, ParseException {

		HashMap<String, Object> mapResult = new HashMap<String, Object>();
		String message = "";

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date attendanceDate = dateFormat.parse(date);

		try {
			Attendance attendance = attendanceRepository.checkUserCheckin(userId, attendanceDate);
			attendanceRepository.delete(attendance);
			message = "Delete Success";

		} catch (Exception e) {
			message = "Delete Failed : User belum checkOut -> " + e.getMessage();
		}

		mapResult.put("message", message);

		return mapResult;
	}

	@Data
	@NoArgsConstructor
	private static class UserAttendance {
		private Users users;
		private BigDecimal latitute;
		private BigDecimal longitude;
	}
}
