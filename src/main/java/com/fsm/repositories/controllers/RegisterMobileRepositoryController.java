package com.fsm.repositories.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.CodeDTO;
import com.fsm.dtos.UsersDTO;
import com.fsm.models.Role;
import com.fsm.models.Code;
import com.fsm.models.UserWorkerStatus;
import com.fsm.models.Users;
import com.fsm.repositories.CodeRepository;
import com.fsm.repositories.RoleRepository;
import com.fsm.repositories.UserWorkerStatusRepository;
import com.fsm.repositories.UsersRepository;
import com.fsm.utility.HashUtil.SHA_256;

@RestController
@RequestMapping("register")
public class RegisterMobileRepositoryController {

	ModelMapper modelMapper = new ModelMapper();

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private UserWorkerStatusRepository userWorkerStatusRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private CodeRepository codeRepository;

	@Autowired
	private UserIdentityService userIdentityService;

//	Convert User Entity model To DTO
	public UsersDTO convertToDTO(Users users) {
		UsersDTO usersDTO = modelMapper.map(users, UsersDTO.class);
		return usersDTO;
	}

//	Convert User DTO Model To Entity
	public Users convertToEntity(UsersDTO usersDto) {
		Users users = modelMapper.map(usersDto, Users.class);
		return users;
	}

//	Convert Code(UserIdentity) Model To DTO
	public CodeDTO convertCodeEntityToDTO(Code code) {
		CodeDTO codeDto = modelMapper.map(code, CodeDTO.class);
		return codeDto;
	}

//	Code untuk Register User(start)
	@PostMapping("/mobile")
	public HashMap<String, Object> mobileRegister(@Valid @RequestBody Users users) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		boolean isUsernameExist = false;
		boolean isNIKExist = false;
		boolean isEmailExist = false;
		String regex = "^[A-Z0-9._%+-]+@[A-Z0-9-]+\\.[A-Z]{2,6}$";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(users.getUserEmail());

		if (!matcher.matches()) {
			showHashMap.put("Message", "Registrasi Gagal");
			showHashMap.put("Detail", "Alamat e-mail tidak valid, silahkan masukan lagi");
			return showHashMap;
		}

		isEmailExist = validationEmail(users.getUserEmail());
		if (isEmailExist) {
			showHashMap.put("Message", "Registrasi Gagal");
			showHashMap.put("Detail", "E-mail sudah terdaftar, silahkan masukan lagi");
			return showHashMap;
		}

		if (!users.getPhone().matches("\\d+")) {
			showHashMap.put("Message", "Registrasi Gagal");
			showHashMap.put("Detail", "Nomor telepon harus berupa angka, silahkan masukan lagi");
			return showHashMap;
		}

		if (!users.getMobilePhone().matches("\\d+")) {
			showHashMap.put("Message", "Registrasi Gagal");
			showHashMap.put("Detail", "Nomor handphone harus berupa angka, silahkan masukan lagi");
			return showHashMap;
		}

		isUsernameExist = validationUsername(users.getUserName());
		if (isUsernameExist) {
			showHashMap.put("Message", "Registrasi Gagal");
			showHashMap.put("Detail", "Username sudah tersedia, silahkan masukan lagi");
			return showHashMap;
		}

		isNIKExist = validationNIK(users.getUserIdentityNo());
		if (isNIKExist) {
			showHashMap.put("Message", "Registrasi Gagal");
			showHashMap.put("Detail", "Nomor user identity sudah tersedia, silahkan masukan lagi");
			return showHashMap;
		}
		
//		Validasi length username
		if (users.getUserName().length() < 3) {
			showHashMap.put("Message", "Registrasi Gagal");
			showHashMap.put("Detail", "Username minimal 3 karakter, silahkan masukan lagi");
			return showHashMap;
		}

//		Validasi length KTP
		if (users.getUserIdentity() == getUserIdentityKTP()) {
			if (users.getUserIdentityNo().length() != 16 || !users.getUserIdentityNo().matches("\\d+")) {
				showHashMap.put("Message", "Registrasi Gagal");
				showHashMap.put("Detail", "Nomor KTP harus 16 digit dan hanya angka saja, silahkan masukan lagi");
				return showHashMap;
			}
		}
//		Validasi length SIM
		else if (users.getUserIdentity() == getUserIdentitySIM()) {
			if (users.getUserIdentityNo().length() != 12 || !users.getUserIdentityNo().matches("\\d+")) {
				showHashMap.put("Message", "Registrasi Gagal");
				showHashMap.put("Detail", "Nomor SIM harus 12 digit dan hanya angka saja, silahkan masukan lagi");
				return showHashMap;
			}
		}
//		Validasi length Passpor
		else if (users.getUserIdentity() == getUserIdentityPasspor()) {
			if (!users.getUserIdentityNo().matches("[a-zA-Z0-9]+")) {
				showHashMap.put("Message", "Registrasi Gagal");
				showHashMap.put("Detail", "Tidak boleh terdapat spesial karakter, silahkan masukan lagi");
				return showHashMap;
			}	
			else if (users.getUserIdentityNo().length() < 7 ) {
				showHashMap.put("Message", "Registrasi Gagal");
				showHashMap.put("Detail","Nomor paspor minimal 7 digit, silahkan masukan lagi");
				return showHashMap;			
			}
			else if (users.getUserIdentityNo().length() > 9) {
				showHashMap.put("Message", "Registrasi Gagal");
				showHashMap.put("Detail", "Nomor paspor maksimal 9 digit, silahkan input lagi");
				return showHashMap;	
			}				
		}

		usersRepository.savingUser(users.getPrimaryAreaId().getCityId(), users.getSecondaryAreaId().getCityId(),
				getRoleId(), users.getUserName(), SHA_256.digestAsHex(users.getUserPassword()), users.getUserAddress(),
				users.getUserAddressDetail(), users.getPhone(), users.getMobilePhone(), users.getUserEmail(),
				users.getUserIdentity(), users.getUserIdentityNo(), users.getUserGender(), users.getCreatedBy(),
				users.getLastModifiedBy(), users.getUserFullName());

		// Code untuk insert ke table user_worker_status
		Users newUser = usersRepository.getNewUsers();

		UserWorkerStatus userWorkerStatus = new UserWorkerStatus();

		userWorkerStatus.setUserId(newUser);
		userWorkerStatus.setStatus(3);
		userWorkerStatus.setCreatedBy(newUser.getUserId());
		userWorkerStatus.setLastModifiedBy(newUser.getUserId());

		userWorkerStatusRepository.save(userWorkerStatus);

		showHashMap.put("Message", "Registrasi berhasil");
		showHashMap.put("Data", users);

		return showHashMap;
	}
//	Code untuk Register User(finish)

//	code untuk validasi username
	private boolean validationUsername(String userName) {
		boolean isExist = false;
		for (Users users : usersRepository.findAll()) {
			if (users.getUserName().equalsIgnoreCase(userName)) {
				isExist = true;
				break;
			}
		}
		return isExist;
	}

//	code untuk validasi Identity No
	private boolean validationNIK(String userNIK) {
		boolean isExist = false;
		for (Users users : usersRepository.findAll()) {
			if (users.getUserIdentityNo().equalsIgnoreCase(userNIK)) {
				isExist = true;
				break;
			}
		}
		return isExist;
	}

//	code untuk validasi email
	private boolean validationEmail(String userEmail) {
		boolean isExist = false;
		for (Users users : usersRepository.findAll()) {
			if (users.getUserEmail().equalsIgnoreCase(userEmail)) {
				isExist = true;
				break;
			}
		}
		return isExist;
	}

//	code untuk mencari role id
	private long getRoleId() {
		Long roleId = (long) 0;
		for (Role role : roleRepository.findAll()) {
			if (role.getRoleName().equalsIgnoreCase("teknisi")) {
				roleId = role.getRoleId();
				break;
			}
		}
		return roleId;
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

//	code for LOV User Identity (Start)
	@GetMapping("/mobile/userIdentity")
	public HashMap<String, Object> getLOVUserIdentity(@RequestParam(value = "pageNo") Integer pageNo,
			@RequestParam(value = "pageSize") Integer pageSize,
			@RequestParam(value = "userIdentityFilter") String userIdentity) {
		HashMap<String, Object> mapResult = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> listMapCode = new ArrayList<HashMap<String, Object>>();
		ArrayList<CodeDTO> listCodeDto = new ArrayList<CodeDTO>();

		for (Code code : userIdentityService.getUserIdentity(pageNo, pageSize, userIdentity)) {

			HashMap<String, Object> mapTemp = new HashMap<String, Object>();

			CodeDTO codeDto = convertCodeEntityToDTO(code);
			mapTemp.put("codeId", codeDto.getCodeId());
			mapTemp.put("codeName", codeDto.getCodeName());
			listMapCode.add(mapTemp);
		}

//		Code for get total data user Identity
		for (Code codes : codeRepository.findByCodeUserIdentity()) {
			CodeDTO codeDto = convertCodeEntityToDTO(codes);
			listCodeDto.add(codeDto);
		}

		mapResult.put("message", "Semua data identitas user");
		mapResult.put("total", listCodeDto.size());
		mapResult.put("data", listMapCode);
		return mapResult;
	}

	@Service
	public class UserIdentityService {

//		Code for get data user Identity
		public List<Code> getUserIdentity(Integer pageNo, Integer pageSize, String userIdentity) {
			Pageable paging = PageRequest.of(pageNo, pageSize);

			Slice<Code> pagedResult = codeRepository.getUserIdentityWithFilter(userIdentity, paging);

			List<Code> listUserIdentity = pagedResult.getContent();
			return listUserIdentity;
		}
	}
//	code for LOV User Identity (Finish)

}
