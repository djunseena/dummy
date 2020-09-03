package com.fsm.repositories.controllers;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fsm.dtos.ClientCompanyDTO;
import com.fsm.dtos.ClientCompanyPICDTO;
import com.fsm.models.City;
import com.fsm.models.ClientCompany;
import com.fsm.models.ClientCompanyBranch;
import com.fsm.models.ClientCompanyPIC;
import com.fsm.models.Users;
import com.fsm.repositories.ClientCompanyBranchRepository;
import com.fsm.repositories.ClientCompanyPICRepository;
import com.fsm.repositories.ClientCompanyRepository;
import com.fsm.repositories.CodeRepository;

import lombok.Data;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("api")
public class ClientCompanyRepositoryController {

	ModelMapper modelMapper = new ModelMapper();

	@Autowired
	ClientCompanyRepository clientCompanyRepository;

	@Autowired
	ClientCompanyBranchRepository clientCompanyBranchRepository;

	@Autowired
	ClientCompanyPICRepository clientCompanyPICRepository;

	@Autowired
	CodeRepository codeRepository;

	public ClientCompanyDTO convertClientCompanyToDTO(ClientCompany clientCompany) {
		ClientCompanyDTO clientCompanyDto = modelMapper.map(clientCompany, ClientCompanyDTO.class);
		return clientCompanyDto;
	}

	public ClientCompanyPICDTO convertClientCompanyPICToDTO(ClientCompanyPIC clientCompanyPIC) {
		ClientCompanyPICDTO clientCompanyPICDto = modelMapper.map(clientCompanyPIC, ClientCompanyPICDTO.class);
		return clientCompanyPICDto;
	}

	@Data
	@NoArgsConstructor
	private static class clientCompanyAndPIC {
		private City city;
		private String companyName;
		private String companyEmail;
		private String companyAddress1;
		private String companyAddress2;
		private String companyZipCode;
		private String companyPhone;
		private String picName;
		private String picPhone;
		private String picEmail;
		private String picDescription;
		private Long userId;
	}

	@GetMapping("clientCompany")
	public Map<String, Object> getAllClientCompany(@RequestParam String search, Pageable pageable) {
		Map<String, Object> result = new HashMap<>();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		ArrayList<ClientCompany> listClientCompanyEntity = (ArrayList<ClientCompany>) clientCompanyRepository
				.getSearchByName(search, pageable);
		int totalListCustomer = clientCompanyRepository.getTotalByName(search);
		int totalListCustomerPage = (int) Math.ceil((totalListCustomer / 10) + 1);

		for (ClientCompany item : listClientCompanyEntity) {
			HashMap<String, Object> detailCustomer = new HashMap<String, Object>();
			ClientCompanyDTO companyDTO = convertClientCompanyToDTO(item);

			detailCustomer.put("companyId", companyDTO.getCompanyId());
			detailCustomer.put("companyName", companyDTO.getCompanyName());
			detailCustomer.put("companyAddress1", companyDTO.getCompanyAddress1());
			detailCustomer.put("companyAddress2", companyDTO.getCompanyAddress2());
			detailCustomer.put("companyEmail", companyDTO.getCompanyEmail());
			detailCustomer.put("companyZipCode", companyDTO.getCompanyZipCode());
			detailCustomer.put("companyPhone", companyDTO.getCompanyPhone());
			detailCustomer.put("cityName", companyDTO.getCityId().getCityName());
			detailCustomer.put("cityId", companyDTO.getCityId().getCityId());

			ClientCompanyPIC clientCompanyPICEntity = clientCompanyPICRepository.getByCompanyId(item.getCompanyId());
			ArrayList<ClientCompanyPIC> listClientCompanyPICEntity = (ArrayList<ClientCompanyPIC>) clientCompanyPICRepository
					.getAllPICByCompanyId(item.getCompanyId());
			if (clientCompanyPICEntity != null) {
				for (ClientCompanyPIC itemPIC : listClientCompanyPICEntity) {
					ClientCompanyPICDTO picDTO = convertClientCompanyPICToDTO(itemPIC);

					Long picType = (long) picDTO.getPicType();

					if (codeRepository.findById(picType).orElse(null).getCodeId() == (long) 43) {
						detailCustomer.put("picId", picDTO.getPicId());
						detailCustomer.put("picName", picDTO.getPicName());
						detailCustomer.put("picDesc", picDTO.getPicDesc());
						detailCustomer.put("picPhone", picDTO.getPicPhone());
						detailCustomer.put("picEmail", picDTO.getPicEmail());
						detailCustomer.put("picTypeName", codeRepository.findById(picType).orElse(null).getCodeName());
						detailCustomer.put("branchId", picDTO.getBranchId().getBranchId());
						detailCustomer.put("branchName", picDTO.getBranchId().getBranchName());
						detailCustomer.put("branchDesc", picDTO.getBranchId().getBranchDesc());
						detailCustomer.put("branchAddress", picDTO.getBranchId().getBranchAddress());
						detailCustomer.put("branchLatitude", picDTO.getBranchId().getBranchLatitude());
						detailCustomer.put("branchLongitude", picDTO.getBranchId().getBranchLongitude());
						detailCustomer.put("cityNamePic", picDTO.getBranchId().getCityId().getCityName());

					} 
				}
			} else {
				detailCustomer.put("picName", "-");
				detailCustomer.put("picDesc", "-");
				detailCustomer.put("picPhone", "-");
				detailCustomer.put("picEmail", "-");
				detailCustomer.put("picTypeName", "-");
				detailCustomer.put("branchId", "-");
				detailCustomer.put("branchName", "-");
				detailCustomer.put("branchDesc", "-");
				detailCustomer.put("branchAddress", "-");
				detailCustomer.put("branchLatitude", "-");
				detailCustomer.put("branchLongitude", "-");
				detailCustomer.put("cityNamePic", "-");
			}
			listData.add(detailCustomer);
		}

		result.put("Status", HttpStatus.OK);
		result.put("totalListCustomer", totalListCustomer);
		result.put("totalListCustomerPage", totalListCustomerPage != 0 ? totalListCustomerPage : 1);
		result.put("Data", listData);

		return result;
	}

	@GetMapping("clientCompany/detail/{companyId}")
	public HashMap<String, Object> showDetailClientCompany(@PathVariable(value = "companyId") Long companyId) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> data = new ArrayList<>();

		ArrayList<ClientCompanyBranch> listBranch = clientCompanyBranchRepository.getByCompanyId(companyId);
		for (ClientCompanyBranch item : listBranch) {
			String picBranch = clientCompanyPICRepository.getByBranchId(item.getBranchId());
			HashMap<String, Object> detailCustomer = new HashMap<String, Object>();
			detailCustomer.put("Branch", item.getBranchName());
			detailCustomer.put("PICBranch", picBranch);
			data.add(detailCustomer);
		}

		showHashMap.put("Status", HttpStatus.OK);
		showHashMap.put("Data", data);
		return showHashMap;
	}

	@PostMapping("clientCompany/create")
	public HashMap<String, Object> createClientCompany(@RequestBody clientCompanyAndPIC body) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		BigDecimal longitude = new BigDecimal("4.5");
		BigDecimal latitude = new BigDecimal("4.5");
		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);

		String companyName = body.getCompanyName().trim();
		String companyEmail = body.getCompanyEmail().trim();

		if (clientCompanyRepository.checkDupClientCompany(companyName) == null
				&& clientCompanyRepository.checkDupEmailClientCompany(companyEmail) == null) {
			ClientCompany clientCompanyEntity = new ClientCompany();
			clientCompanyEntity.setCompanyName(companyName);
			clientCompanyEntity.setCompanyEmail(companyEmail);
			clientCompanyEntity.setCompanyAddress1(body.getCompanyAddress1().trim());
			clientCompanyEntity.setCompanyAddress2(body.getCompanyAddress2().trim());
			clientCompanyEntity.setCityId(body.getCity());
			clientCompanyEntity.setCompanyZipCode(body.getCompanyZipCode().trim());
			clientCompanyEntity.setCompanyPhone(body.getCompanyPhone().trim());
			clientCompanyEntity.setCreatedBy(body.getUserId());
			clientCompanyEntity.setCreatedOn(dateNow);
			clientCompanyEntity.setLastModifiedBy(body.getUserId());
			clientCompanyEntity.setLastModifiedOn(dateNow);
			clientCompanyEntity.setDeleted(false);
			clientCompanyRepository.save(clientCompanyEntity);
			ClientCompanyDTO clientCompanyDTO = convertClientCompanyToDTO(clientCompanyEntity);
			ClientCompany companyIdLastAdded = clientCompanyRepository.findIdByLastAdded();

			ClientCompanyBranch clientCompanyBranch = new ClientCompanyBranch();
			clientCompanyBranch.setBranchName(companyName);
			clientCompanyBranch.setCityId(body.getCity());
			clientCompanyBranch.setCompanyId(companyIdLastAdded);
			clientCompanyBranch.setBranchAddress(body.getCompanyAddress1().trim());
			clientCompanyBranch.setBranchDesc("-");
			clientCompanyBranch.setBranchLatitude(latitude);
			clientCompanyBranch.setBranchLongitude(longitude);
			clientCompanyBranch.setCreatedBy(body.getUserId());
			clientCompanyBranch.setCreatedOn(dateNow);
			clientCompanyBranch.setLastModifiedBy(body.getUserId());
			clientCompanyBranch.setLastModifiedOn(dateNow);
			clientCompanyBranch.setDeleted(false);
			clientCompanyBranchRepository.save(clientCompanyBranch);
			ClientCompanyBranch branchLastAdded = clientCompanyBranchRepository.findByLastAdded();

			ClientCompanyPIC clientCompanyPICEntity = new ClientCompanyPIC();
			clientCompanyPICEntity.setCompanyId(companyIdLastAdded);
			clientCompanyPICEntity.setBranchId(branchLastAdded);
			clientCompanyPICEntity.setPicName(body.getPicName().trim());
			clientCompanyPICEntity.setPicEmail(body.getPicEmail().trim());
			clientCompanyPICEntity.setPicPhone(body.getPicPhone().trim());
			clientCompanyPICEntity.setPicDesc(body.getPicDescription().trim());
			clientCompanyPICEntity.setPicType(43);
			clientCompanyPICEntity.setCreatedBy(body.getUserId());
			clientCompanyPICEntity.setCreatedOn(dateNow);
			clientCompanyPICEntity.setLastModifiedBy(body.getUserId());
			clientCompanyPICEntity.setLastModifiedOn(dateNow);
			clientCompanyPICEntity.setDeleted(false);
			clientCompanyPICRepository.save(clientCompanyPICEntity);
			ClientCompanyPICDTO clientCompanyPICDTO = convertClientCompanyPICToDTO(clientCompanyPICEntity);
			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", "Client Company Berhasil Dibuat");
			showHashMap.put("Data Customer", clientCompanyDTO);
			showHashMap.put("Data PIC", clientCompanyPICDTO);
		} else if (clientCompanyRepository.checkDupClientCompany(companyName) == null
				&& clientCompanyRepository.checkDupEmailClientCompany(companyEmail) != null) {
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", "Client Company Gagal Dibuat, Email : '" +
			clientCompanyRepository.checkDupEmailClientCompany(companyEmail).getCompanyEmail()
			+"' Sudah Terdaftar Dengan Data Client Company Lain");
		} else if (clientCompanyRepository.checkDupClientCompany(companyName) != null
			&& clientCompanyRepository.checkDupEmailClientCompany(companyEmail) == null) {
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", "Client Company Gagal Dibuat, Nama Client Company : '" +
			clientCompanyRepository.checkDupClientCompany(companyName).getCompanyName()
			+"' Sudah Terdaftar");
		} else {
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", "Client Company Gagal Dibuat, Nama Client Company : '" +
			clientCompanyRepository.checkDupClientCompany(companyName).getCompanyName()
			+"' dan Email : '"+ clientCompanyRepository.checkDupEmailClientCompany(companyEmail).getCompanyEmail()+
			"' Sudah Terdaftar");
		}
		return showHashMap;
	}

	@PutMapping("clientCompany/update/{companyId}")
	public HashMap<String, Object> createClientCompany(@PathVariable(value = "companyId") Long companyId,
			@RequestBody clientCompanyAndPIC body) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		BigDecimal longitude = new BigDecimal("4.5");
		BigDecimal latitude = new BigDecimal("4.5");
		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);

		String companyName = body.getCompanyName().trim();
		String companyEmail = body.getCompanyEmail().trim();

		if (clientCompanyRepository.checkDupClientCompany(companyName) == null
				&& clientCompanyRepository.checkDupEmailClientCompany(companyEmail) == null) {
			ClientCompany clientCompanyEntity = clientCompanyRepository.findById(companyId).orElse(null);
			clientCompanyEntity.setCompanyName(companyName);
			clientCompanyEntity.setCompanyEmail(companyEmail);
			clientCompanyEntity.setCompanyAddress1(body.getCompanyAddress1().trim());
			clientCompanyEntity.setCompanyAddress2(body.getCompanyAddress2().trim());
			clientCompanyEntity.setCityId(body.getCity());
			clientCompanyEntity.setCompanyZipCode(body.getCompanyZipCode().trim());
			clientCompanyEntity.setCompanyPhone(body.getCompanyPhone().trim());
			clientCompanyEntity.setLastModifiedBy(body.getUserId());
			clientCompanyEntity.setLastModifiedOn(dateNow);
			clientCompanyRepository.save(clientCompanyEntity);
			ClientCompanyDTO clientCompanyDTO = convertClientCompanyToDTO(clientCompanyEntity);
			
			ClientCompanyBranch clientCompanyBranch = clientCompanyBranchRepository.findByCompanyId(companyId);
			clientCompanyBranch.setBranchName(companyName);
			clientCompanyBranch.setCityId(body.getCity());
			clientCompanyBranch.setCompanyId(clientCompanyEntity);
			clientCompanyBranch.setBranchAddress(body.getCompanyAddress1().trim());
			clientCompanyBranch.setBranchLatitude(latitude);
			clientCompanyBranch.setBranchLongitude(longitude);
			clientCompanyBranch.setLastModifiedBy(body.getUserId());
			clientCompanyBranch.setLastModifiedOn(dateNow);
			clientCompanyBranch.setDeleted(false);
			clientCompanyBranchRepository.save(clientCompanyBranch);

			ClientCompanyPIC clientCompanyPICEntity = clientCompanyPICRepository.getByCompanyId(companyId);
			clientCompanyPICEntity.setCompanyId(clientCompanyEntity);
			clientCompanyPICEntity.setPicName(body.getPicName().trim());
			clientCompanyPICEntity.setPicEmail(body.getPicEmail().trim());
			clientCompanyPICEntity.setPicPhone(body.getPicPhone().trim());
			clientCompanyPICEntity.setPicDesc(body.getPicDescription().trim());
			clientCompanyPICEntity.setLastModifiedBy(body.getUserId());
			clientCompanyPICEntity.setLastModifiedOn(dateNow);
			clientCompanyPICRepository.save(clientCompanyPICEntity);
			ClientCompanyPICDTO clientCompanyPICDTO = convertClientCompanyPICToDTO(clientCompanyPICEntity);
			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Data Customer", clientCompanyDTO);
			showHashMap.put("Data PIC", clientCompanyPICDTO);
		} else if (clientCompanyRepository.checkDupClientCompany(companyName) == null
				&& clientCompanyRepository.checkDupEmailClientCompany(companyEmail) != null) {
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", "Client Company Gagal Diubah, Email : '" +
			clientCompanyRepository.checkDupEmailClientCompany(companyEmail).getCompanyEmail()
			+"' Sudah Terdaftar Dengan Data Client Company Lain");
		} else if (clientCompanyRepository.checkDupClientCompany(companyName) != null
			&& clientCompanyRepository.checkDupEmailClientCompany(companyEmail) == null) {
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", "Client Company Gagal Diubah, Nama Client Company : '" +
			clientCompanyRepository.checkDupClientCompany(companyName).getCompanyName()
			+"' Sudah Terdaftar");
		} else {
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", "Client Company Gagal Diubah, Nama Client Company : '" +
			clientCompanyRepository.checkDupClientCompany(companyName).getCompanyName()
			+"' dan Email : '"+ clientCompanyRepository.checkDupEmailClientCompany(companyEmail).getCompanyEmail()+
			"' Sudah Terdaftar");
		}
		return showHashMap;
	}

	@PutMapping("clientCompany/delete/{companyId}")
	public HashMap<String, Object> deleteClientCompany(@PathVariable(value = "companyId") Long companyId,
			@RequestBody Users user) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		ClientCompany clientCompanyEntity = clientCompanyRepository.findById(companyId).orElse(null);
		ClientCompanyPIC clientCompanyPICEntity = clientCompanyPICRepository.getPICByCompanyId(companyId);
		ClientCompanyBranch clientCompanyBranchEntity = clientCompanyBranchRepository
				.getClientCompanyBranchByCompanyId(companyId);
		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);

		if (clientCompanyPICEntity == null && clientCompanyBranchEntity == null) {
			String message = "Client Company Berhasil Dihapus";
			clientCompanyEntity.setDeleted(true);
			clientCompanyEntity.setLastModifiedBy(user.getUserId());
			clientCompanyEntity.setLastModifiedOn(dateNow);
			clientCompanyRepository.save(clientCompanyEntity);
			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);
		} else {
			String message = "Client Company Gagal Dihapus, Karena Data Masih Digunakan";
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		}
		return showHashMap;
	}

	@GetMapping("listClientCompany")
	public HashMap<String, Object> showListUserGroup() throws ParseException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		ArrayList<ClientCompany> listClientCompanyEntity = (ArrayList<ClientCompany>) clientCompanyRepository.getAll();

		for (ClientCompany item : listClientCompanyEntity) {
			HashMap<String, Object> data = new HashMap<>();
			ClientCompanyDTO clientCompanyDTO = modelMapper.map(item, ClientCompanyDTO.class);
			data.put("companyId", clientCompanyDTO.getCompanyId());
			data.put("companyName", clientCompanyDTO.getCompanyName());
			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;

	}
}
