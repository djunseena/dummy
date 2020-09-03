package com.fsm.repositories.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.JobDTO;
import com.fsm.dtos.JobUserWorkerDTO;
import com.fsm.dtos.UsersDTO;
import com.fsm.interfaces.Minio;
import com.fsm.models.Attendance;
import com.fsm.models.City;
import com.fsm.models.Code;
import com.fsm.models.Job;
import com.fsm.models.JobUserWorker;
import com.fsm.models.Role;
import com.fsm.models.UserWorkerStatus;
import com.fsm.models.Users;
import com.fsm.repositories.AttendanceRepository;
import com.fsm.repositories.CodeRepository;
import com.fsm.repositories.DispatchRepository;
import com.fsm.repositories.JobUserWorkerRepository;
import com.fsm.repositories.UserWorkerStatusRepository;
import com.fsm.repositories.UsersRepository;
import com.fsm.utility.HashUtil.SHA_256;

import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidExpiresRangeException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.XmlParserException;
import lombok.Data;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("users")
public class UsersRepositoryController extends Minio{

	@Autowired
	UsersRepository usersRepository;

	@Autowired
	UserWorkerStatusRepository userWorkerStatusRepository;

	@Autowired
	CodeRepository codeRepository;

	@Autowired
	DispatchRepository dispatchRepository;

	@Autowired
	JobUserWorkerRepository jobUserWorkerRepository;

	@Autowired
	AttendanceRepository attendanceRepository;

	ModelMapper modelMapper = new ModelMapper();

	public UsersDTO convertUserToDTO(Users users) {
		UsersDTO usersDto = modelMapper.map(users, UsersDTO.class);
		return usersDto;
	}

	@GetMapping("listUsers")
	public HashMap<String, Object> getAllUsers(@RequestParam String search, Pageable pageable) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();
		ArrayList<Users> listUsersEntity = (ArrayList<Users>) usersRepository.getListUsers(search, pageable);

		for (Users userItem : listUsersEntity) {
			HashMap<String, Object> data = new HashMap<>();

			UsersDTO usersDTO = modelMapper.map(userItem, UsersDTO.class);

			data.put("userId", usersDTO.getUserId());
			data.put("userName", usersDTO.getUserName());
			data.put("userEmail", usersDTO.getUserEmail());
			data.put("roleId", usersDTO.getRoleId());
			data.put("userFullName", usersDTO.getUserFullName());

			listData.add(data);
		}

		int totalListUser = usersRepository.getTotalListUsers(search);
		int totalListUserPage = (int) Math.ceil((listData.size() / 10) + 1);

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);
		result.put("TotalData", totalListUser);
		result.put("TotalPage", totalListUserPage);

		return result;
	}

	@GetMapping("getUserDetail/{userId}")
	public HashMap<String, Object> getUserDetail(@PathVariable(value = "userId") Long userId) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		Users userEntity = usersRepository.getAdminByUserId(userId);

		HashMap<String, Object> userData = new HashMap<>();

		UsersDTO usersDTO = modelMapper.map(userEntity, UsersDTO.class);

		userData.put("primaryAreaId", usersDTO.getPrimaryAreaId());
		userData.put("roleId", usersDTO.getRoleId());
		userData.put("userName", usersDTO.getUserName());
		userData.put("userPassword", usersDTO.getUserPassword());
		userData.put("userAddress", usersDTO.getUserAddress());
		userData.put("userAddressDetail", usersDTO.getUserAddressDetail());
		userData.put("phone", usersDTO.getPhone());
		userData.put("userEmail", usersDTO.getUserEmail());
		userData.put("userFullName", usersDTO.getUserFullName());

		listData.add(userData);

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;
	}

	@CrossOrigin(allowCredentials = "true")
	@PostMapping("register")
	public HashMap<String, Object> register(@RequestBody Users newUsers) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		Users users = new Users();

		String username = newUsers.getUserName().trim();
		String email = newUsers.getUserEmail().trim();

		if (usersRepository.checkDupUsersName(username) == null && usersRepository.checkDupUsersEmail(email) == null ){
			users.setPrimaryAreaId(newUsers.getPrimaryAreaId());
			users.setSecondaryAreaId(newUsers.getPrimaryAreaId());
			users.setRoleId(newUsers.getRoleId());
			users.setUserName(username);
			users.setUserPassword(SHA_256.digestAsHex(newUsers.getUserPassword()));
			users.setUserAddress(newUsers.getUserAddress().trim());
			users.setUserAddressDetail(newUsers.getUserAddressDetail().trim());
			users.setPhone(newUsers.getPhone().trim());
			users.setMobilePhone(newUsers.getMobilePhone().trim());
			users.setUserEmail(email);
			users.setUserIdentity(newUsers.getUserIdentity());
			users.setUserIdentityNo(newUsers.getUserIdentityNo().trim());
			users.setUserGender(newUsers.getUserGender());
			users.setUserImage(newUsers.getUserImage());
			users.setCreatedBy(newUsers.getUserId());
			users.setCreatedOn(dateNow);
			users.setLastModifiedBy(newUsers.getUserId());
			users.setLastModifiedOn(dateNow);
			users.setDeleted(false);
			users.setUserLatitude(newUsers.getUserLatitude());
			users.setUserLongatitude(newUsers.getUserLongatitude());
			users.setUserFullName(newUsers.getUserFullName().trim());

			usersRepository.save(users);

			Long latestUserId = usersRepository.getLatestUserId();

			users.setCreatedBy(latestUserId);
			users.setLastModifiedBy(latestUserId);

			usersRepository.save(users);

			message = "User Berhasil Dibuat";

			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);
		} else if(usersRepository.checkDupUsersName(username) != null && usersRepository.checkDupUsersEmail(email) == null ){
			message = "User Gagal Dibuat. Username : '"+ 
			usersRepository.checkDupUsersName(username).getUserName()
			+"' Telah Digunakan";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		} else if(usersRepository.checkDupUsersName(username) == null && usersRepository.checkDupUsersEmail(email) != null ){
			message = "User Gagal Dibuat. Email : '"+
			usersRepository.checkDupUsersEmail(email).getUserEmail()
			+"' Telah Digunakan";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		} else {
			message = "User Gagal Dibuat. Username : '"+ 
			usersRepository.checkDupUsersName(username).getUserName()
			+"' dan Email : '" +
			usersRepository.checkDupUsersEmail(email).getUserEmail()
			+"' Telah Digunakan";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		}

		return showHashMap;
	}

	//sample push
	@PostMapping("createUsers")
	public HashMap<String, Object> createUsers(@RequestBody Users newUsers) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		Users users = new Users();

		String username = newUsers.getUserName().trim();
		String email = newUsers.getUserEmail().trim();

		if (usersRepository.checkDupUsersName(username) == null && usersRepository.checkDupUsersEmail(email) == null ){
			users.setPrimaryAreaId(newUsers.getPrimaryAreaId());
			users.setSecondaryAreaId(newUsers.getPrimaryAreaId());
			users.setRoleId(newUsers.getRoleId());
			users.setUserName(username);
			users.setUserPassword(SHA_256.digestAsHex(newUsers.getUserPassword()));
			users.setUserAddress(newUsers.getUserAddress().trim());
			users.setUserAddressDetail(newUsers.getUserAddressDetail().trim());
			users.setPhone(newUsers.getPhone().trim());
			users.setMobilePhone(newUsers.getMobilePhone().trim());
			users.setUserEmail(email);
			users.setUserIdentity(newUsers.getUserIdentity());
			users.setUserIdentityNo(newUsers.getUserIdentityNo().trim());
			users.setUserGender(newUsers.getUserGender());
			users.setUserImage(newUsers.getUserImage());
			users.setCreatedBy(newUsers.getCreatedBy());
			users.setCreatedOn(dateNow);
			users.setLastModifiedBy(newUsers.getLastModifiedBy());
			users.setLastModifiedOn(dateNow);
			users.setDeleted(false);
			users.setUserLatitude(newUsers.getUserLatitude());
			users.setUserLongatitude(newUsers.getUserLongatitude());
			users.setUserFullName(newUsers.getUserFullName().trim());

			usersRepository.save(users);

			message = "User Berhasil Dibuat";

			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);
		} else if(usersRepository.checkDupUsersName(username) != null && usersRepository.checkDupUsersEmail(email) == null ){
			message = "User Gagal Dibuat. Username : '"+ username + "' Telah Digunakan";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		} else if(usersRepository.checkDupUsersName(username) == null && usersRepository.checkDupUsersEmail(email) != null ){
			message = "User Gagal Dibuat. Email : '"+ email +"' Telah Digunakan";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		} else {
			message = "User Gagal Dibuat. Username : '"+ username +"' dan Email : '" + email +"' Telah Digunakan";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		}

		return showHashMap;
	}

	@PutMapping("updateUsers/{userId}")
	public HashMap<String, Object> updateUsers(@PathVariable(value = "userId") Long userId,
			@RequestBody Users updatedUsers) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		Users users = usersRepository.findById(userId).orElse(null);

		String username = updatedUsers.getUserName().trim();
		String email = updatedUsers.getUserEmail().trim();

		if (usersRepository.checkDupUsersName(username) == null && usersRepository.checkDupUsersEmail(email) == null) {
			users.setPrimaryAreaId(updatedUsers.getPrimaryAreaId());
			users.setRoleId(updatedUsers.getRoleId());
			users.setUserName(username);
			users.setUserAddress(updatedUsers.getUserAddress().trim());
			users.setUserAddressDetail(updatedUsers.getUserAddressDetail().trim());
			users.setPhone(updatedUsers.getPhone().trim());
			users.setMobilePhone(updatedUsers.getMobilePhone().trim());
			users.setUserEmail(email);
			users.setUserIdentity(updatedUsers.getUserIdentity());
			users.setUserIdentityNo(updatedUsers.getUserIdentityNo().trim());
			users.setUserGender(updatedUsers.getUserGender());
			users.setUserImage(updatedUsers.getUserImage());
			users.setLastModifiedBy(updatedUsers.getLastModifiedBy());
			users.setLastModifiedOn(dateNow);
			users.setDeleted(false);
			users.setUserLatitude(updatedUsers.getUserLatitude());
			users.setUserLongatitude(updatedUsers.getUserLongatitude());
			users.setUserFullName(updatedUsers.getUserFullName().trim());

			usersRepository.save(users);

			message = "User Berhasil Diubah";

			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);
		} else if (usersRepository.checkDupUsersName(username) == users && usersRepository.checkDupUsersEmail(email) == users) {
			users.setPrimaryAreaId(updatedUsers.getPrimaryAreaId());
			users.setRoleId(updatedUsers.getRoleId());
			users.setUserName(username);
			users.setUserAddress(updatedUsers.getUserAddress());
			users.setUserAddressDetail(updatedUsers.getUserAddressDetail());
			users.setPhone(updatedUsers.getPhone());
			users.setMobilePhone(updatedUsers.getMobilePhone());
			users.setUserEmail(email);
			users.setUserIdentity(updatedUsers.getUserIdentity());
			users.setUserIdentityNo(updatedUsers.getUserIdentityNo());
			users.setUserGender(updatedUsers.getUserGender());
			users.setUserImage(updatedUsers.getUserImage());
			users.setLastModifiedBy(updatedUsers.getLastModifiedBy());
			users.setLastModifiedOn(dateNow);
			users.setDeleted(false);
			users.setUserLatitude(updatedUsers.getUserLatitude());
			users.setUserLongatitude(updatedUsers.getUserLongatitude());
			users.setUserFullName(updatedUsers.getUserFullName());

			usersRepository.save(users);

			message = "User Berhasil Diubah";

			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);
		} else if(usersRepository.checkDupUsersName(username) != null && usersRepository.checkDupUsersEmail(email) == null ){
			message = "User Gagal Diubah. Username : '"+ username +"' Telah Digunakan";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		} else if(usersRepository.checkDupUsersName(username) == null && usersRepository.checkDupUsersEmail(email) != null ){
			message = "User Gagal Diubah. Email : '"+ email +"' Telah Digunakan";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		} else {
			message = "User Gagal Diubah. Username : '"+ username +"' dan Email : '" + email +"' Telah Digunakan";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		}

		return showHashMap;
	}

	@PutMapping("deleteUsers/{userId}")
	public HashMap<String, Object> deleteUsers(@PathVariable(value = "userId") Long userId,
			@RequestBody BodyDeleted bodyDeleted) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		String status = "";
		Users users = usersRepository.findById(userId).orElse(null);

		users.setLastModifiedBy(bodyDeleted.getLastModifiedBy());
		users.setLastModifiedOn(dateNow);
		users.setDeleted(true);
		usersRepository.save(users);
		message = "User Berhasil Dihapus";
		status = "Success!";
		showHashMap.put("Status", status);
		showHashMap.put("Message", message);

		return showHashMap;
	}

	@Data
	@NoArgsConstructor
	private static class BodyDeleted {
		private Long LastModifiedBy;
	}

	@Data
	@NoArgsConstructor
	private static class Worker {
		private City primaryAreaId;
		private City secondaryAreaId;
		private Role roleId;
		private String userName;
		private String userPassword;
		private String userAddress;
		private String phone;
		private String mobilePhone;
		private String userEmail;
		private int userIdentity;
		private String userIdentityNo;
		private int userGender;
		private String userImage;
		private long createdBy;
		private long lastModifiedBy;
		private BigDecimal userLatitude;
		private BigDecimal userLongatitude;
		private String userFullName;
		private List<Job> jobId;
	}

	@GetMapping("listWorker")
	public HashMap<String, Object> getAllWorker(@RequestParam String search, Pageable pageable) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();
		ArrayList<Users> listWorkerEntity = (ArrayList<Users>) usersRepository.getListWorker(search, pageable);

		for (Users workerItem : listWorkerEntity) {
			HashMap<String, Object> data = new HashMap<>();

			UsersDTO usersDTO = modelMapper.map(workerItem, UsersDTO.class);

			data.put("userId", usersDTO.getUserId());
			data.put("userName", usersDTO.getUserName());
			data.put("userEmail", usersDTO.getUserEmail());
			data.put("userFullName", usersDTO.getUserFullName());

			listData.add(data);
		}

		int totalListWorker = usersRepository.getTotalListWorker(search);
		int totalListWorkerPage = (int) Math.ceil((listData.size() / 10) + 1);

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);
		result.put("TotalData", totalListWorker);
		result.put("TotalPage", totalListWorkerPage);

		return result;
	}

	@GetMapping("getWorkerDetail/{userId}")
	public HashMap<String, Object> getWorkerDetail(@PathVariable(value = "userId") Long userId) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		Users workerEntity = usersRepository.getWorkerByUserId(userId);

		HashMap<String, Object> workerData = new HashMap<>();

		UsersDTO usersDTO = modelMapper.map(workerEntity, UsersDTO.class);

		workerData.put("primaryAreaId", usersDTO.getPrimaryAreaId());
		workerData.put("secondaryAreaId", usersDTO.getSecondaryAreaId());
		workerData.put("roleId", usersDTO.getRoleId());
		workerData.put("userName", usersDTO.getUserName());
		workerData.put("userPassword", usersDTO.getUserPassword());
		workerData.put("userAddress", usersDTO.getUserAddress());
		workerData.put("phone", usersDTO.getPhone());
		workerData.put("mobilePhone", usersDTO.getMobilePhone());
		workerData.put("userEmail", usersDTO.getUserEmail());
		workerData.put("userIdentity", usersDTO.getUserIdentity());
		workerData.put("userIdentityNo", usersDTO.getUserIdentityNo());
		workerData.put("userFullName", usersDTO.getUserFullName());
		workerData.put("userGender", usersDTO.getUserGender());
		workerData.put("userLatitude", usersDTO.getUserLatitude());
		workerData.put("userLongatitude", usersDTO.getUserLongatitude());
		workerData.put("userImage", usersDTO.getUserImage());

		ArrayList<JobUserWorker> listJobUserWorkerByUserId = jobUserWorkerRepository
				.getJobUserWorkerByUserId(usersDTO.getUserId());
		ArrayList<JobUserWorkerDTO> listJobDTO = new ArrayList<>();
		ArrayList<JobDTO> listJobId = new ArrayList<>();

		for (JobUserWorker jobUserWorkerItem : listJobUserWorkerByUserId) {
			JobUserWorkerDTO jobUserWorkerDTO = new JobUserWorkerDTO();
			jobUserWorkerDTO = modelMapper.map(jobUserWorkerItem, JobUserWorkerDTO.class);
			listJobDTO.add(jobUserWorkerDTO);
		}

		for (JobUserWorkerDTO item : listJobDTO) {
			JobDTO jobDTO = new JobDTO();
			jobDTO = modelMapper.map(item.getJobId(), JobDTO.class);
			listJobId.add(jobDTO);
		}

		workerData.put("jobId", listJobId);

		listData.add(workerData);

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;
	}

	@PostMapping("createWorker")
	public HashMap<String, Object> createWorker(@RequestBody Worker newWorker) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		Users worker = new Users();

		String username = newWorker.getUserName();
		String email = newWorker.getUserEmail();

		if (usersRepository.checkDupUsersName(username) == null && usersRepository.checkDupUsersEmail(email) == null) {
			worker.setPrimaryAreaId(newWorker.getPrimaryAreaId());
			worker.setSecondaryAreaId(newWorker.getSecondaryAreaId());
			worker.setRoleId(newWorker.getRoleId());
			worker.setUserName(newWorker.getUserName());
			worker.setUserPassword(SHA_256.digestAsHex(newWorker.getUserPassword()));
			worker.setUserAddress(newWorker.getUserAddress());
			worker.setUserAddressDetail(null);
			worker.setPhone(newWorker.getPhone());
			worker.setMobilePhone(newWorker.getMobilePhone());
			worker.setUserEmail(newWorker.getUserEmail());
			worker.setUserIdentity(newWorker.getUserIdentity());
			worker.setUserIdentityNo(newWorker.getUserIdentityNo());
			worker.setUserGender(newWorker.getUserGender());
			worker.setUserImage(newWorker.getUserImage());
			worker.setCreatedBy(newWorker.getCreatedBy());
			worker.setCreatedOn(dateNow);
			worker.setLastModifiedBy(newWorker.getLastModifiedBy());
			worker.setLastModifiedOn(dateNow);
			worker.setDeleted(false);
			worker.setUserLatitude(newWorker.getUserLatitude());
			worker.setUserLongatitude(newWorker.getUserLongatitude());
			worker.setUserFullName(newWorker.getUserFullName());

			usersRepository.save(worker);

			Long latestUserId = usersRepository.getLatestUserId();
			Users lastAddedUsers = usersRepository.getUsersByUserId(latestUserId);

			List<JobUserWorker> jobUserWorkers = new ArrayList<>();

			for (int i = 0; i < newWorker.getJobId().size(); i++) {
				JobUserWorker jobUserWorker = new JobUserWorker();

				jobUserWorker.setUserId(lastAddedUsers);
				Job jobid = newWorker.getJobId().get(i);
				jobUserWorker.setJobId(jobid);
				jobUserWorker.setDeleted(false);

				jobUserWorkers.add(jobUserWorker);
			}

			jobUserWorkerRepository.saveAll(jobUserWorkers);

			UserWorkerStatus userWorkerStatus = new UserWorkerStatus();

			userWorkerStatus.setUserId(lastAddedUsers);
			userWorkerStatus.setStatus((int) 3);
			userWorkerStatus.setCreatedBy(lastAddedUsers.getCreatedBy());
			userWorkerStatus.setCreatedOn(dateNow);
			userWorkerStatus.setLastModifiedBy(lastAddedUsers.getCreatedBy());
			userWorkerStatus.setLastModifiedOn(dateNow);
			userWorkerStatus.setDeleted(false);

			userWorkerStatusRepository.save(userWorkerStatus);

			message = "Worker Berhasil Dibuat";

			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);
		} else if(usersRepository.checkDupUsersName(username) != null && usersRepository.checkDupUsersEmail(email) == null ){
			message = "Worker Gagal Dibuat. Username : '"+ username +"' Telah Digunakan";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		} else if(usersRepository.checkDupUsersName(username) == null && usersRepository.checkDupUsersEmail(email) != null ){
			message = "Worker Gagal Dibuat. Email : '"+ email +"' Telah Digunakan";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		} else {
			message = "Worker Gagal Dibuat. Username : '"+ username +"' dan Email : '" + email +"' Telah Digunakan";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		}

		return showHashMap;
	}

	@PutMapping("updateWorker/{userId}")
	public HashMap<String, Object> updateWorker(@PathVariable(value = "userId") Long userId,
			@RequestBody Worker updatedWorker) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		Users worker = usersRepository.findById(userId).orElse(null);

		String username = updatedWorker.getUserName().trim();
		String email = updatedWorker.getUserEmail().trim();

		if (usersRepository.checkDupUsersName(username) == null && usersRepository.checkDupUsersEmail(email) == null) {
			worker.setPrimaryAreaId(updatedWorker.getPrimaryAreaId());
			worker.setSecondaryAreaId(updatedWorker.getSecondaryAreaId());
			worker.setRoleId(updatedWorker.getRoleId());
			worker.setUserName(username);
			worker.setUserAddress(updatedWorker.getUserAddress().trim());
			worker.setPhone(updatedWorker.getPhone().trim());
			worker.setMobilePhone(updatedWorker.getMobilePhone().trim());
			worker.setUserEmail(email);
			worker.setUserIdentity(updatedWorker.getUserIdentity());
			worker.setUserIdentityNo(updatedWorker.getUserIdentityNo().trim());
			worker.setUserGender(updatedWorker.getUserGender());
			worker.setUserImage(updatedWorker.getUserImage());
			worker.setLastModifiedBy(updatedWorker.getLastModifiedBy());
			worker.setLastModifiedOn(dateNow);
			worker.setUserLatitude(updatedWorker.getUserLatitude());
			worker.setUserLongatitude(updatedWorker.getUserLongatitude());
			worker.setUserFullName(updatedWorker.getUserFullName().trim());

			usersRepository.save(worker);

			JobUserWorker listJob = jobUserWorkerRepository.getObjectJobUserWorkerByUserId(userId);
			if (listJob == null) {
				List<JobUserWorker> jobUserWorkers = new ArrayList<>();
				for (int i = 0; i < updatedWorker.getJobId().size(); i++) {
					JobUserWorker jobUserWorker = new JobUserWorker();

					jobUserWorker.setUserId(worker);
					Job jobid = updatedWorker.getJobId().get(i);
					jobUserWorker.setJobId(jobid);
					jobUserWorker.setDeleted(false);

					jobUserWorkers.add(jobUserWorker);
				}

				jobUserWorkerRepository.saveAll(jobUserWorkers);
			} else {
				List<JobUserWorker> listJobDefault = jobUserWorkerRepository.getJobUserWorkerByUserId(userId);
				Long jobUserWorkerId = jobUserWorkerRepository.getObjectJobUserWorkerByUserId(userId)
						.getJobUserWorkerId();

				if (updatedWorker.getJobId().size() == listJobDefault.size()) {
					for (int i = 0; i < listJobDefault.size(); i++) {
						jobUserWorkerRepository.updateJob(updatedWorker.getJobId().get(i).getJobId(),
								jobUserWorkerId + i);
					}
				} else if (updatedWorker.getJobId().size() < listJobDefault.size()) {
					jobUserWorkerRepository.deleteJob(userId);
					List<JobUserWorker> jobUserWorkers = new ArrayList<>();
					for (int i = 0; i < updatedWorker.getJobId().size(); i++) {
						JobUserWorker jobUserWorker = new JobUserWorker();
						jobUserWorker.setUserId(worker);
						Job jobid = updatedWorker.getJobId().get(i);
						jobUserWorker.setJobId(jobid);
						jobUserWorker.setDeleted(false);
						jobUserWorkers.add(jobUserWorker);
					}
					jobUserWorkerRepository.saveAll(jobUserWorkers);
				} else if (updatedWorker.getJobId().size() > listJobDefault.size()) {
					int batas = updatedWorker.getJobId().size() - listJobDefault.size();

					for (int i = 0; i <= listJobDefault.size() - 1; i++) {
						jobUserWorkerRepository.updateJob(updatedWorker.getJobId().get(i).getJobId(),
								jobUserWorkerId + i);
					}

					List<JobUserWorker> jobUserWorkers = new ArrayList<>();

					for (int i = 0; i < batas; i++) {
						JobUserWorker jobUserWorker = new JobUserWorker();
						jobUserWorker.setUserId(worker);
						Job jobid = updatedWorker.getJobId().get(i + listJobDefault.size());
						jobUserWorker.setJobId(jobid);
						jobUserWorker.setDeleted(false);
						jobUserWorkers.add(jobUserWorker);
					}

					jobUserWorkerRepository.saveAll(jobUserWorkers);
				} else if (updatedWorker.getJobId().size() == 0) {
					jobUserWorkerRepository.deleteJob(userId);
				}
			}
			message = "Worker Berhasil Diubah";

			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);
		} else if (usersRepository.checkDupUsersName(username) == worker && usersRepository.checkDupUsersEmail(email) == worker) {
			worker.setPrimaryAreaId(updatedWorker.getPrimaryAreaId());
			worker.setSecondaryAreaId(updatedWorker.getSecondaryAreaId());
			worker.setRoleId(updatedWorker.getRoleId());
			worker.setUserName(username);
			worker.setUserAddress(updatedWorker.getUserAddress().trim());
			worker.setPhone(updatedWorker.getPhone().trim());
			worker.setMobilePhone(updatedWorker.getMobilePhone().trim());
			worker.setUserEmail(email);
			worker.setUserIdentity(updatedWorker.getUserIdentity());
			worker.setUserIdentityNo(updatedWorker.getUserIdentityNo().trim());
			worker.setUserGender(updatedWorker.getUserGender());
			worker.setUserImage(updatedWorker.getUserImage());
			worker.setLastModifiedBy(updatedWorker.getLastModifiedBy());
			worker.setLastModifiedOn(dateNow);
			worker.setUserLatitude(updatedWorker.getUserLatitude());
			worker.setUserLongatitude(updatedWorker.getUserLongatitude());
			worker.setUserFullName(updatedWorker.getUserFullName().trim());

			usersRepository.save(worker);

			JobUserWorker listJob = jobUserWorkerRepository.getObjectJobUserWorkerByUserId(userId);
			if (listJob == null) {
				List<JobUserWorker> jobUserWorkers = new ArrayList<>();
				for (int i = 0; i < updatedWorker.getJobId().size(); i++) {
					JobUserWorker jobUserWorker = new JobUserWorker();

					jobUserWorker.setUserId(worker);
					Job jobid = updatedWorker.getJobId().get(i);
					jobUserWorker.setJobId(jobid);
					jobUserWorker.setDeleted(false);

					jobUserWorkers.add(jobUserWorker);
				}

				jobUserWorkerRepository.saveAll(jobUserWorkers);
			} else {
				List<JobUserWorker> listJobDefault = jobUserWorkerRepository.getJobUserWorkerByUserId(userId);
				Long jobUserWorkerId = jobUserWorkerRepository.getObjectJobUserWorkerByUserId(userId)
						.getJobUserWorkerId();

				if (updatedWorker.getJobId().size() == listJobDefault.size()) {
					for (int i = 0; i < listJobDefault.size(); i++) {
						jobUserWorkerRepository.updateJob(updatedWorker.getJobId().get(i).getJobId(),
								jobUserWorkerId + i);
					}
				} else if (updatedWorker.getJobId().size() < listJobDefault.size()) {
					jobUserWorkerRepository.deleteJob(userId);
					List<JobUserWorker> jobUserWorkers = new ArrayList<>();
					for (int i = 0; i < updatedWorker.getJobId().size(); i++) {
						JobUserWorker jobUserWorker = new JobUserWorker();
						jobUserWorker.setUserId(worker);
						Job jobid = updatedWorker.getJobId().get(i);
						jobUserWorker.setJobId(jobid);
						jobUserWorker.setDeleted(false);
						jobUserWorkers.add(jobUserWorker);
					}
					jobUserWorkerRepository.saveAll(jobUserWorkers);
				} else if (updatedWorker.getJobId().size() > listJobDefault.size()) {
					int batas = updatedWorker.getJobId().size() - listJobDefault.size();

					for (int i = 0; i <= listJobDefault.size() - 1; i++) {
						jobUserWorkerRepository.updateJob(updatedWorker.getJobId().get(i).getJobId(),
								jobUserWorkerId + i);
					}

					List<JobUserWorker> jobUserWorkers = new ArrayList<>();

					for (int i = 0; i < batas; i++) {
						JobUserWorker jobUserWorker = new JobUserWorker();
						jobUserWorker.setUserId(worker);
						Job jobid = updatedWorker.getJobId().get(i + listJobDefault.size());
						jobUserWorker.setJobId(jobid);
						jobUserWorker.setDeleted(false);
						jobUserWorkers.add(jobUserWorker);
					}

					jobUserWorkerRepository.saveAll(jobUserWorkers);
				} else if (updatedWorker.getJobId().size() == 0) {
					jobUserWorkerRepository.deleteJob(userId);
				}
			}
			message = "Worker Berhasil Diubah";

			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);
		} else if(usersRepository.checkDupUsersName(username) != null && usersRepository.checkDupUsersEmail(email) == null ){
			message = "Worker Gagal Diubah. Username : '"+ username +"' Telah Digunakan";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		} else if(usersRepository.checkDupUsersName(username) == null && usersRepository.checkDupUsersEmail(email) != null ){
			message = "Worker Gagal Diubah. Email : '"+ email +"' Telah Digunakan";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		} else {
			message = "Worker Gagal Diubah. Username : '"+ username +"' dan Email : '" + email +"' Telah Digunakan";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		}

		return showHashMap;
	}

	@PutMapping("deleteWorker/{userId}")
	public HashMap<String, Object> deleteWorker(@PathVariable(value = "userId") Long userId,
			@RequestBody BodyDeleted bodyDeleted) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		Users worker = usersRepository.findById(userId).orElse(null);

		Attendance attendance = attendanceRepository.findCheckinAttendanceByUser(userId);

		if (attendance == null) {
			worker.setLastModifiedBy(bodyDeleted.getLastModifiedBy());
			worker.setLastModifiedOn(dateNow);
			worker.setDeleted(true);
			usersRepository.save(worker);

			List<JobUserWorker> listJobDefault = jobUserWorkerRepository.getJobUserWorkerByUserId(userId);

			for (JobUserWorker item : listJobDefault) {
				item.setDeleted(true);

				jobUserWorkerRepository.save(item);
			}
			message = "Worker Berhasil Dihapus";

			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);
		} else {
			message = "Worker Tidak Bisa Dihapus, Karena Data Masih Digunakan";
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		}

		return showHashMap;
	}

	// Api untuk button standby list on monitoring
	@GetMapping("getDataTechnicianStandbyOnMonitoring")
	public HashMap<String, Object> getDataTechnicianStandBy(Pageable pageable, @RequestParam String search)
			throws InvalidKeyException, InvalidEndpointException, InvalidPortException, ErrorResponseException,
			IllegalArgumentException, InsufficientDataException, InternalException, InvalidBucketNameException,
			InvalidResponseException, NoSuchAlgorithmException, XmlParserException, InvalidExpiresRangeException,
			IOException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();

		for (Users item : usersRepository.findTechnicianStandbyPaging(pageable, search)) {
			HashMap<String, Object> data = new HashMap<String, Object>();
			UserWorkerStatus usersWorkerStatus = userWorkerStatusRepository.searchByUserId(item.getUserId());
			Code code = codeRepository.findById(Long.valueOf(usersWorkerStatus.getStatus())).get();
			String urlImage = " ";
			if (item.getUserImage() != null) {
				urlImage = minio().presignedGetObject(bucketName, item.getUserImage());

			}

			data.put("userId", item.getUserId());
			data.put("userImage", urlImage);
			data.put("userName", item.getUserFullName());
			data.put("userEmail", item.getUserEmail());
			data.put("mobilePhone", item.getMobilePhone());
			data.put("workerStatus", code.getCodeName());
			listData.add(data);
		}
		ArrayList<Users> listAllStandBy = usersRepository.findTechnicianStandby();
		String message = " ";
		if (listData.isEmpty()) {
			message = "Data Kosong";

		} else {
			message = "Data Teknisi Standby";
		}
		result.put("Status", HttpStatus.OK);
		result.put("Message", message);
		result.put("Total", listAllStandBy.size());
		result.put("Size", listData.size());
		result.put("Data", listData);
		return result;
	}

	// api pie chart technician
	@GetMapping("getDataTechnicianOnMonitoring")
	public HashMap<String, Object> countDataTechnicianOnMonitoring() {
		HashMap<String, Object> result = new HashMap<String, Object>();
		HashMap<String, Object> technician = new HashMap<String, Object>();
		ArrayList<Users> listAssigment = usersRepository.findTechnicianAssignment();
		ArrayList<Users> listOnDuty = usersRepository.findTechnicianOnDuty();
		ArrayList<Users> listStandBy = usersRepository.findTechnicianStandby();

		technician.put("Assigment", listAssigment.size());
		technician.put("OnDuty", listOnDuty.size());
		technician.put("StandBy", listStandBy.size());

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Menghitung Data Berhasil");
		result.put("Data", technician);
		return result;
	}

	// api pie chart worker avaibility
	@GetMapping("getDataWorkerAvaibility")
	public HashMap<String, Object> countDataTechnicianAvaibility() {
		HashMap<String, Object> result = new HashMap<String, Object>();
		HashMap<String, Object> technicianAvaibility = new HashMap<String, Object>();
		ArrayList<UserWorkerStatus> listAvaiable = userWorkerStatusRepository.countDataTechnicianAvailable();
		ArrayList<UserWorkerStatus> listOff = userWorkerStatusRepository.countDataTechnicianOff();

		technicianAvaibility.put("Avaiable", listAvaiable.size());
		technicianAvaibility.put("Off", listOff.size());

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Menghitung Data Berhasil");
		result.put("Data", technicianAvaibility);
		return result;

	}

	// api get location technician Maps on monitoring
	@GetMapping("getLocationTechnician")
	public HashMap<String, Object> findAllLocationTechnician() {
		HashMap<String, Object> result = new HashMap<String, Object>();
		ArrayList<ArrayList<HashMap<String, Object>>> listDataTechnician = new ArrayList<ArrayList<HashMap<String, Object>>>();
		ArrayList<HashMap<String, Object>> listDataAssignment = listLocationTechnicianAssignment();
		ArrayList<HashMap<String, Object>> listDataOnDuty = listLocationTechnicianOnDuty();
		ArrayList<HashMap<String, Object>> listDataStandby = listLocationTechnicianStandBy();
		listDataTechnician.add(listDataAssignment);
		listDataTechnician.add(listDataOnDuty);
		listDataTechnician.add(listDataStandby);

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Pencarian Lokasi Teknisi Berhasil");
		result.put("Data", listDataTechnician);
		return result;
	}

	// method untuk location technician on assignment
	public ArrayList<HashMap<String, Object>> listLocationTechnicianAssignment() {
		ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		ArrayList<Users> techAssignment = usersRepository.findTechnicianAssignment();
		for (Users item : techAssignment) {
			HashMap<String, Object> listAssignment = new HashMap<String, Object>();
			listAssignment.put("userId", item.getUserId());
			listAssignment.put("name", item.getUserFullName());
			listAssignment.put("userLatitude", item.getUserLatitude());
			listAssignment.put("userLongitude", item.getUserLongatitude());
			listAssignment.put("status", "Assignment");
			result.add(listAssignment);
		}

		return result;
	}

	// method untuk location technician on duty
	public ArrayList<HashMap<String, Object>> listLocationTechnicianOnDuty() {
		ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		ArrayList<Users> techOnDuty = usersRepository.findTechnicianOnDuty();
		for (Users item : techOnDuty) {
			HashMap<String, Object> listOnDuty = new HashMap<String, Object>();
			listOnDuty.put("userId", item.getUserId());
			listOnDuty.put("name", item.getUserFullName());
			listOnDuty.put("userLatitude", item.getUserLatitude());
			listOnDuty.put("userLongitude", item.getUserLongatitude());
			listOnDuty.put("status", "Onduty");
			result.add(listOnDuty);
		}

		return result;
	}

	// method untuk location technician Stand by
	public ArrayList<HashMap<String, Object>> listLocationTechnicianStandBy() {
		ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		ArrayList<Users> techStandBy = usersRepository.findTechnicianStandby();
		for (Users item : techStandBy) {
			HashMap<String, Object> listStandBy = new HashMap<String, Object>();
			listStandBy.put("userId", item.getUserId());
			listStandBy.put("name", item.getUserFullName());
			listStandBy.put("userLatitude", item.getUserLatitude());
			listStandBy.put("userLongitude", item.getUserLongatitude());
			listStandBy.put("status", "Standby");
			result.add(listStandBy);
		}
		return result;
	}

	@GetMapping("/{id}")
	public HashMap<String, Object> getUsersById(@PathVariable(value = "id") Long id) throws InvalidKeyException,
			ErrorResponseException, IllegalArgumentException, InsufficientDataException, InternalException,
			InvalidBucketNameException, InvalidResponseException, NoSuchAlgorithmException, XmlParserException,
			InvalidEndpointException, InvalidPortException, IOException, InvalidExpiresRangeException {

		HashMap<String, Object> mapResult = new HashMap<String, Object>();

		Users users = usersRepository.findById(id).get();
		UsersDTO usersDto = convertUserToDTO(users);

		String message;

		if (users == null) {
			message = "Data Kosong";
		} else {
			message = "Data User Berdasarkan Id";
		}
		mapResult.put("Message", message);
		mapResult.put("Data", usersDto);

		return mapResult;
	}

}