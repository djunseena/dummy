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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsm.dtos.UsersDTO;
import com.fsm.interfaces.Minio;
import com.fsm.models.Code;
import com.fsm.models.Users;
import com.fsm.repositories.CodeRepository;
import com.fsm.repositories.UsersRepository;

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
public class DetailAccountRepositoryController extends Minio {

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private CodeRepository codeRepository;

	ModelMapper modelMapper = new ModelMapper();

//	Convert entity to DTO
	public UsersDTO convertUserToDTO(Users users) {
		UsersDTO usersDto = modelMapper.map(users, UsersDTO.class);
		return usersDto;
	}

//	Get User data by ID
	@GetMapping("/worker/{id}")
	public HashMap<String, Object> getUsersById(@PathVariable(value = "id") Long id) throws InvalidKeyException,
			ErrorResponseException, IllegalArgumentException, InsufficientDataException, InternalException,
			InvalidBucketNameException, InvalidResponseException, NoSuchAlgorithmException, XmlParserException,
			InvalidEndpointException, InvalidPortException, IOException, InvalidExpiresRangeException {

		HashMap<String, Object> mapResult = new HashMap<String, Object>();
		HashMap<String, Object> mapTemp = new HashMap<String, Object>();

		Users users = usersRepository.findById(id).get();
		UsersDTO usersDto = convertUserToDTO(users);

		Code code = codeRepository.findById((long) users.getUserIdentity()).get();
		String identityType = code.getCodeName();

		String gender = "";
		if (users.getUserGender() == 1) {
			gender = "Laki-laki";
		} else if (users.getUserGender() == 2) {
			gender = "Perempuan";
		}

		mapTemp.put("userId", usersDto.getUserId());
		mapTemp.put("primaryAreaId", usersDto.getPrimaryAreaId());
		mapTemp.put("secondaryAreaId", usersDto.getSecondaryAreaId());
		mapTemp.put("roleId", usersDto.getRoleId());
		mapTemp.put("userName", usersDto.getUserName());
		mapTemp.put("userPassword", usersDto.getUserPassword());
		mapTemp.put("userAddress", usersDto.getUserAddress());
		mapTemp.put("userAddressDetail", usersDto.getUserAddressDetail());
		mapTemp.put("phone", usersDto.getPhone());
		mapTemp.put("mobilePhone", usersDto.getMobilePhone());
		mapTemp.put("userEmail", usersDto.getUserEmail());
		mapTemp.put("userIdentity", usersDto.getUserIdentity());
		mapTemp.put("userIdentityName", identityType);
		mapTemp.put("userIdentityNo", usersDto.getUserIdentityNo());
		mapTemp.put("userGender", usersDto.getUserGender());
		mapTemp.put("userGenderName", gender);
		mapTemp.put("userImage", usersDto.getUserImage());
		mapTemp.put("createdBy", usersDto.getCreatedBy());
		mapTemp.put("createdOn", usersDto.getCreatedOn());
		mapTemp.put("lastModifiedBy", usersDto.getLastModifiedBy());
		mapTemp.put("lastModifiedOn", usersDto.getLastModifiedOn());
		mapTemp.put("userLatitude", usersDto.getUserLatitude());
		mapTemp.put("userLongatitude", usersDto.getUserLongatitude());
		mapTemp.put("userFullName", usersDto.getUserFullName());
		mapTemp.put("deleted", usersDto.isDeleted());

		String urlImage = null;

		if (users.getUserImage() != null) {
			urlImage = minio().presignedGetObject(bucketName, usersDto.getUserImage());
		}

		mapResult.put("Message", "Show Data By Id");
		mapResult.put("Data", mapTemp);
		mapResult.put("Image", urlImage);

		return mapResult;
	}

//	Get all list data users
	@GetMapping("/worker/all")
	public HashMap<String, Object> getListUsers() {
		HashMap<String, Object> mapResult = new HashMap<String, Object>();
		ArrayList<UsersDTO> listUsersDto = new ArrayList<UsersDTO>();

		for (Users users : usersRepository.findAll()) {
			UsersDTO usersDto = convertUserToDTO(users);
			listUsersDto.add(usersDto);
		}

		String message;
		if (listUsersDto.isEmpty()) {
			message = "Data is Empty";
		} else {
			message = "Show All Data";
		}
		mapResult.put("Message", message);
		mapResult.put("Total", listUsersDto.size());
		mapResult.put("Data", listUsersDto);

		return mapResult;
	}

//	Update Detail Account Data / Users
	@PostMapping("/worker/update/image/")
	public HashMap<String, Object> detailAccountUserDataUpdateNew(@RequestParam(value = "userId") Long id,
			@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "textObject") String textObject)
			throws JsonParseException, JsonMappingException, IOException, InvalidKeyException, ErrorResponseException,
			IllegalArgumentException, InsufficientDataException, InternalException, InvalidBucketNameException,
			InvalidResponseException, NoSuchAlgorithmException, XmlParserException, InvalidEndpointException,
			InvalidPortException {

		HashMap<String, Object> mapResult = new HashMap<String, Object>();
		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		String status = "";
		String imagePath = null;
		ObjectMapper mapper = new ObjectMapper();
		Users updateUsers = mapper.readValue(textObject, Users.class);

		boolean isNIKExist = false;
		boolean isEmailExist = false;

		String regex = "^[A-Z0-9._%+-]+@[A-Z0-9-]+\\.[A-Z]{2,6}$";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(updateUsers.getUserEmail());

		if (!matcher.matches()) {
			mapResult.put("Message", "Update Gagal");
			mapResult.put("Detail", "Alamat e-mail tidak valid, silahkan masukan lagi");
			return mapResult;
		}
		if (!updateUsers.getMobilePhone().matches("\\d+")) {
			mapResult.put("Message", "Update Gagal");
			mapResult.put("Detail", "Nomor handphone harus berupa angka, silahkan masukan lagi");
			return mapResult;
		}

		isNIKExist = validationNIK(updateUsers.getUserIdentityNo(), id);
		if (isNIKExist) {
			mapResult.put("Message", "Update Gagal");
			mapResult.put("Detail", "Nomor user identity sudah tersedia, silahkan masukan lagi");
			return mapResult;
		}

		isEmailExist = validationEmail(updateUsers.getUserEmail(), id);
		if (isEmailExist) {
			mapResult.put("Message", "Update Gagal");
			mapResult.put("Detail", "E-mail sudah terdaftar, silahkan masukan lagi");
			return mapResult;
		}

//		Validasi length KTP
		if (updateUsers.getUserIdentity() == getUserIdentityKTP()) {
			if (updateUsers.getUserIdentityNo().length() != 16 || !updateUsers.getUserIdentityNo().matches("\\d+")) {
				mapResult.put("Message", "Update Gagal");
				mapResult.put("Detail", "Nomor KTP harus 16 digit dan hanya angka saja, silahkan masukan lagi");
				return mapResult;
			}
		}
//		Validasi length SIM
		else if (updateUsers.getUserIdentity() == getUserIdentitySIM()) {
			if (updateUsers.getUserIdentityNo().length() != 12 || !updateUsers.getUserIdentityNo().matches("\\d+")) {
				mapResult.put("Message", "Update Gagal");
				mapResult.put("Detail", "Nomor SIM harus 12 digit dan hanya angka saja, silahkan masukan lagi");
				return mapResult;
			}
		}
//		Validasi length Passpor
		else if (updateUsers.getUserIdentity() == getUserIdentityPasspor()) {
			if (!updateUsers.getUserIdentityNo().matches("[a-zA-Z0-9]+")) {
				mapResult.put("Message", "Update Gagal");
				mapResult.put("Detail", "Tidak boleh terdapat spesial karakter, silahkan masukan lagi");
				return mapResult;
			}	
			else if (updateUsers.getUserIdentityNo().length() < 7 ) {
				mapResult.put("Message", "Update Gagal");
				mapResult.put("Detail", "Nomor paspor minimal 7 digit, silahkan masukan lagi");
				return mapResult;			
			}
			else if (updateUsers.getUserIdentityNo().length() > 9) {
				mapResult.put("Message", "Update Gagal");
				mapResult.put("Detail", "Nomor paspor maksimal 9 digit, silahkan input lagi");
				return mapResult;	
			}				
		}

		Users users = usersRepository.findById(id).get();

		if (!file.isEmpty()) {
			imagePath = "Users/" + file.getOriginalFilename();
			Path filePath = Files.createTempFile("file", ".jpg");
			file.transferTo(filePath);
			PutObjectOptions options = new PutObjectOptions(file.getSize(), -1);
			minio().putObject(bucketName, imagePath, filePath.toString(), options);
			Files.delete(filePath);

			users.setUserImage(imagePath);
		}

		users.setPrimaryAreaId(updateUsers.getPrimaryAreaId());
		users.setSecondaryAreaId(updateUsers.getSecondaryAreaId());
		users.setUserFullName(updateUsers.getUserFullName());
		users.setUserIdentity(updateUsers.getUserIdentity());
		users.setUserIdentityNo(updateUsers.getUserIdentityNo());
		users.setMobilePhone(updateUsers.getMobilePhone());
		users.setUserEmail(updateUsers.getUserEmail());
		users.setUserAddress(updateUsers.getUserAddress());
		users.setCreatedOn(dateNow);
		users.setLastModifiedOn(dateNow);
		users.setCreatedBy(users.getUserId());
		users.setLastModifiedBy(users.getUserId());

		usersRepository.save(users);

		if (updateUsers == null || users == null) {
			status = "Gagal";
			message = "Data gagal diganti";
		} else {
			status = "Sukses";
			message = "Data berhasil diganti";
		}

		mapResult.put("Status", status);
		mapResult.put("Message", message);

		return mapResult;
	}

	@PostMapping("/worker/update/")
	public HashMap<String, Object> detailAccountUserDataUpdate(@RequestParam(value = "userId") Long id,
			@RequestBody Users updateUsers) {

		HashMap<String, Object> mapResult = new HashMap<String, Object>();
		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		String status = "";

		boolean isNIKExist = false;
		boolean isEmailExist = false;

		String regex = "^[A-Z0-9._%+-]+@[A-Z0-9-]+\\.[A-Z]{2,6}$";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(updateUsers.getUserEmail());

		if (!matcher.matches()) {
			mapResult.put("Message", "Update Gagal");
			mapResult.put("Detail", "Alamat e-mail tidak valid, silahkan masukan lagi");
			return mapResult;
		}
		if (!updateUsers.getMobilePhone().matches("\\d+")) {
			mapResult.put("Message", "Update Gagal");
			mapResult.put("Detail", "Nomor handphone harus berupa angka, silahkan masukan lagi");
			return mapResult;
		}

		isNIKExist = validationNIK(updateUsers.getUserIdentityNo(), id);
		if (isNIKExist) {
			mapResult.put("Message", "Update Gagal");
			mapResult.put("Detail", "Nomor user identity sudah tersedia, silahkan masukan lagi");
			return mapResult;
		}

		isEmailExist = validationEmail(updateUsers.getUserEmail(), id);
		if (isEmailExist) {
			mapResult.put("Message", "Update Gagal");
			mapResult.put("Detail", "E-mail sudah terdaftar, silahkan masukan lagi");
			return mapResult;
		}

//		Validasi length KTP
		if (updateUsers.getUserIdentity() == getUserIdentityKTP()) {
			if (updateUsers.getUserIdentityNo().length() != 16 || !updateUsers.getUserIdentityNo().matches("\\d+")) {
				mapResult.put("Message", "Update Gagal");
				mapResult.put("Detail", "Nomor KTP harus 16 digit dan hanya angka saja, silahkan masukan lagi");
				return mapResult;
			}
		}
//		Validasi length SIM
		else if (updateUsers.getUserIdentity() == getUserIdentitySIM()) {
			if (updateUsers.getUserIdentityNo().length() != 12 || !updateUsers.getUserIdentityNo().matches("\\d+")) {
				mapResult.put("Message", "Update Gagal");
				mapResult.put("Detail", "Nomor SIM harus 12 digit dan hanya angka saja, silahkan masukan lagi");
				return mapResult;
			}
		}
//		Validasi length Passpor
		else if (updateUsers.getUserIdentity() == getUserIdentityPasspor()) {
			if (!updateUsers.getUserIdentityNo().matches("[a-zA-Z0-9]+")) {
				mapResult.put("Message", "Update Gagal");
				mapResult.put("Detail", "Tidak boleh terdapat spesial karakter, silahkan masukan lagi");
				return mapResult;
			}	
			else if (updateUsers.getUserIdentityNo().length() < 7 ) {
				mapResult.put("Message", "Update Gagal");
				mapResult.put("Detail", "Nomor paspor minimal 7 digit, silahkan masukan lagi");
				return mapResult;			
			}
			else if (updateUsers.getUserIdentityNo().length() > 9) {
				mapResult.put("Message", "Update Gagal");
				mapResult.put("Detail", "Nomor paspor maksimal 9 digit, silahkan input lagi");
				return mapResult;	
			}				
		}

		Users users = usersRepository.findById(id).get();

		users.setPrimaryAreaId(updateUsers.getPrimaryAreaId());
		users.setSecondaryAreaId(updateUsers.getSecondaryAreaId());
		users.setUserFullName(updateUsers.getUserFullName());
		users.setUserIdentity(updateUsers.getUserIdentity());
		users.setUserIdentityNo(updateUsers.getUserIdentityNo());
		users.setMobilePhone(updateUsers.getMobilePhone());
		users.setUserEmail(updateUsers.getUserEmail());
		users.setUserAddress(updateUsers.getUserAddress());
		users.setCreatedOn(dateNow);
		users.setLastModifiedOn(dateNow);
		users.setCreatedBy(users.getUserId());
		users.setLastModifiedBy(users.getUserId());

		usersRepository.save(users);

		if (updateUsers == null || users == null) {
			status = "Gagal";
			message = "Data gagal diganti";
		} else {
			status = "Sukses";
			message = "Data berhasil diganti";
		}

		mapResult.put("Status", status);
		mapResult.put("Message", message);

		return mapResult;
	}

//	code untuk validasi NIK (kecuali NIK sendiri)
	private boolean validationNIK(String userNIK, long userId) {
		boolean isExist = false;
		for (Users users : usersRepository.findAll()) {
			if (users.getUserIdentityNo().equalsIgnoreCase(userNIK) && users.getUserId() != userId) {
				isExist = true;
				break;
			}
		}
		return isExist;
	}

//	code untuk validasi email
	private boolean validationEmail(String userEmail, long userId) {
		boolean isExist = false;
		for (Users users : usersRepository.findAll()) {
			if (users.getUserEmail().equalsIgnoreCase(userEmail) && users.getUserId() != userId) {
				isExist = true;
				break;
			}
		}
		return isExist;
	}

//	code untuk mencari identity KTP
	private int getUserIdentityKTP() {
		long userIdentityKTP = 0;
		for (Code codes : codeRepository.findAll()) {
			if (codes.getCodeName().equalsIgnoreCase("ktp")) {
				userIdentityKTP = codes.getCodeId();
				break;
			}
		}
		return (int) userIdentityKTP;
	}

//	code untuk mencari identity SIM
	private int getUserIdentitySIM() {
		long userIdentitySIM = 0;
		for (Code codes : codeRepository.findAll()) {
			if (codes.getCodeName().equalsIgnoreCase("sim")) {
				userIdentitySIM = codes.getCodeId();
				break;
			}
		}
		return (int) userIdentitySIM;
	}

//	code untuk mencari identity passpor
	private int getUserIdentityPasspor() {
		long userIdentityPasspor = 0;
		for (Code codes : codeRepository.findAll()) {
			if (codes.getCodeName().equalsIgnoreCase("passpor")) {
				userIdentityPasspor = codes.getCodeId();
				break;
			}
		}
		return (int) userIdentityPasspor;
	}

}
