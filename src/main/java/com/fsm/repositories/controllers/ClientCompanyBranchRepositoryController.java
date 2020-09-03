package com.fsm.repositories.controllers;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.fsm.dtos.ClientCompanyBranchDTO;
import com.fsm.dtos.ClientCompanyPICDTO;
import com.fsm.models.City;
import com.fsm.models.ClientCompany;
import com.fsm.models.ClientCompanyBranch;
import com.fsm.models.ClientCompanyPIC;
import com.fsm.models.SLA;
import com.fsm.models.TroubleTicket;
import com.fsm.models.Users;
import com.fsm.repositories.ClientCompanyBranchRepository;
import com.fsm.repositories.ClientCompanyPICRepository;
import com.fsm.repositories.SLARepository;
import com.fsm.repositories.TroubleTicketRepository;

import lombok.Data;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("api")
public class ClientCompanyBranchRepositoryController {

	ModelMapper modelMapper = new ModelMapper();

	@Autowired
	ClientCompanyBranchRepository clientCompanyBranchRepository;

	@Autowired
	SLARepository slaRepository;

	@Autowired
	ClientCompanyPICRepository clientCompanyPICRepository;

	@Autowired
	TroubleTicketRepository troubleTicketRepository;

	public ClientCompanyBranchDTO convertClientCompanyBranchDTO(ClientCompanyBranch clientCompanyBranch) {
		ClientCompanyBranchDTO clientCompanyBranchDTO = modelMapper.map(clientCompanyBranch,
				ClientCompanyBranchDTO.class);
		return clientCompanyBranchDTO;
	}

	@GetMapping("/getAllClientCompanyBranch")
	public HashMap<String, Object> getAllClientCompanyBranch(@RequestParam String search, Pageable pageable) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();
		ArrayList<ClientCompanyBranch> listClientCompanyBranchEntity = (ArrayList<ClientCompanyBranch>) clientCompanyBranchRepository
				.getBranchList(search, pageable);
		Integer totalListData = clientCompanyBranchRepository.getTotalBranch(search);

		for (ClientCompanyBranch clientCompanyBranchItem : listClientCompanyBranchEntity) {
			HashMap<String, Object> data = new HashMap<>();

			ClientCompanyBranchDTO clientCompanyBranchDTO = modelMapper.map(clientCompanyBranchItem,
					ClientCompanyBranchDTO.class);

			data.put("branchId", clientCompanyBranchDTO.getBranchId());
			data.put("cityId", clientCompanyBranchDTO.getCityId());
			data.put("companyId", clientCompanyBranchDTO.getCompanyId());
			data.put("companyName", clientCompanyBranchDTO.getCompanyId().getCompanyName());
			data.put("branchName", clientCompanyBranchDTO.getBranchName());
			data.put("branchDesc", clientCompanyBranchDTO.getBranchDesc());
			data.put("branchLatitude", clientCompanyBranchDTO.getBranchLatitude());
			data.put("branchLongitude", clientCompanyBranchDTO.getBranchLongitude());
			data.put("branchAdress", clientCompanyBranchDTO.getBranchAddress());

			ArrayList<ClientCompanyPIC> listClientCompanyPICEntity = clientCompanyPICRepository
					.findIdByBranchId(clientCompanyBranchDTO.getBranchId());

			for (ClientCompanyPIC clientCompanyPICItem : listClientCompanyPICEntity) {
				ClientCompanyPICDTO clientCompanyPICDTO = modelMapper.map(clientCompanyPICItem,
						ClientCompanyPICDTO.class);

				data.put("picId", clientCompanyPICDTO.getPicId());
				data.put("picName", clientCompanyPICDTO.getPicName());
				data.put("picPhone", clientCompanyPICDTO.getPicPhone());
				data.put("picEmail", clientCompanyPICDTO.getPicEmail());
				
			}

			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);
		result.put("totalListData", totalListData);

		return result;
	}

	@Data
	@NoArgsConstructor
	private static class ClientCompanyBranchPIC {
		private City cityId;
		private ClientCompany companyId;
		private String branchName;
		private BigDecimal branchLatitude;
		private BigDecimal branchLongitude;
		private String branchAddress;
		private String branchDesc;
		private long createdBy;
		private long lastModifiedBy;
		private String picName;
		private String picEmail;
		private String picPhone;
		private String picDesc;
	}

	@PostMapping("/createClientCompanyBranch")
	public HashMap<String, Object> createClientCompanyBranch(@RequestBody ClientCompanyBranchPIC newBranch) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		ClientCompanyBranch clientCompanyBranch = new ClientCompanyBranch();

		String branchName = newBranch.getBranchName().trim();
		String picName = newBranch.getPicName().trim();
		String picEmail = newBranch.getPicEmail().trim();

		if(clientCompanyBranchRepository.checkDupClientCompanyBranch(branchName, picName) == null) {
		clientCompanyBranch.setCityId(newBranch.getCityId());
		clientCompanyBranch.setCompanyId(newBranch.getCompanyId());
		clientCompanyBranch.setBranchName(branchName);
		clientCompanyBranch.setBranchLatitude(newBranch.getBranchLatitude());
		clientCompanyBranch.setBranchLongitude(newBranch.getBranchLongitude());
		clientCompanyBranch.setBranchAddress(newBranch.getBranchAddress().trim());
		clientCompanyBranch.setBranchDesc(newBranch.getBranchDesc().trim());
		clientCompanyBranch.setCreatedBy(newBranch.getCreatedBy());
		clientCompanyBranch.setCreatedOn(dateNow);
		clientCompanyBranch.setLastModifiedBy(newBranch.getLastModifiedBy());
		clientCompanyBranch.setLastModifiedOn(dateNow);
		clientCompanyBranch.setDeleted(false);

		clientCompanyBranchRepository.save(clientCompanyBranch);

		ClientCompanyBranch lastAddedBranch = clientCompanyBranchRepository.findByLastAdded();
		ClientCompanyPIC clientCompanyPIC = new ClientCompanyPIC();

		clientCompanyPIC.setBranchId(lastAddedBranch);
		clientCompanyPIC.setCompanyId(lastAddedBranch.getCompanyId());
		clientCompanyPIC.setPicName(picName);
		clientCompanyPIC.setPicEmail(picEmail);
		clientCompanyPIC.setPicPhone(newBranch.getPicPhone().trim());
		clientCompanyPIC.setPicDesc(newBranch.getPicDesc().trim());
		clientCompanyPIC.setPicType((int) 44);
		clientCompanyPIC.setCreatedBy(lastAddedBranch.getCreatedBy());
		clientCompanyPIC.setCreatedOn(dateNow);
		clientCompanyPIC.setLastModifiedBy(lastAddedBranch.getLastModifiedBy());
		clientCompanyPIC.setLastModifiedOn(dateNow);
		clientCompanyPIC.setDeleted(false);

		clientCompanyPICRepository.save(clientCompanyPIC);

		message = "Customer Branch Berhasil Dibuat";

		showHashMap.put("Status", HttpStatus.OK);
		showHashMap.put("Message", message);
		} else {
			message = "Customer Branch Gagal Dibuat, Nama Client Company Branch : '"+ branchName +"' dengan Nama PIC : '"+ picName +"'Sudah Terdaftar ";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		}
		return showHashMap;
	}

	@PutMapping("/updateClientCompanyBranch/{branchId}")
	public HashMap<String, Object> updateBranch(@PathVariable(value = "branchId") Long branchId,
			@RequestBody ClientCompanyBranchPIC updatedBranch) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		ClientCompanyBranch clientCompanyBranch = clientCompanyBranchRepository.findById(branchId).orElse(null);
		ClientCompanyPIC clientCompanyPIC = clientCompanyPICRepository.getPICByBranchId(branchId);

		String branchName = updatedBranch.getBranchName().trim();
		String picName = updatedBranch.getPicName().trim();

		if(clientCompanyBranchRepository.checkDupClientCompanyBranch(branchName, picName) == null ){
			clientCompanyBranch.setCityId(updatedBranch.getCityId());
			clientCompanyBranch.setCompanyId(updatedBranch.getCompanyId());
			clientCompanyBranch.setBranchName(branchName);
			clientCompanyBranch.setBranchLatitude(updatedBranch.getBranchLatitude());
			clientCompanyBranch.setBranchLongitude(updatedBranch.getBranchLongitude());
			clientCompanyBranch.setBranchAddress(updatedBranch.getBranchAddress().trim());
			clientCompanyBranch.setBranchDesc(updatedBranch.getBranchDesc().trim());
			clientCompanyBranch.setLastModifiedBy(updatedBranch.getLastModifiedBy());
			clientCompanyBranch.setLastModifiedOn(dateNow);

			clientCompanyBranchRepository.save(clientCompanyBranch);

			clientCompanyPIC.setPicName(picName);
			clientCompanyPIC.setPicEmail(updatedBranch.getPicEmail().trim());
			clientCompanyPIC.setPicPhone(updatedBranch.getPicPhone().trim());
			clientCompanyPIC.setPicDesc(updatedBranch.getPicDesc().trim());
			clientCompanyPIC.setLastModifiedBy(updatedBranch.getLastModifiedBy());
			clientCompanyPIC.setLastModifiedOn(dateNow);

			clientCompanyPICRepository.save(clientCompanyPIC);

			message = "Customer Branch Berhasil Diubah";

			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);
		} else if(clientCompanyBranchRepository.checkDupClientCompanyBranch(branchName, picName) == clientCompanyBranch ){
			clientCompanyBranch.setCityId(updatedBranch.getCityId());
			clientCompanyBranch.setCompanyId(updatedBranch.getCompanyId());
			clientCompanyBranch.setBranchName(branchName);
			clientCompanyBranch.setBranchLatitude(updatedBranch.getBranchLatitude());
			clientCompanyBranch.setBranchLongitude(updatedBranch.getBranchLongitude());
			clientCompanyBranch.setBranchAddress(updatedBranch.getBranchAddress().trim());
			clientCompanyBranch.setBranchDesc(updatedBranch.getBranchDesc().trim());
			clientCompanyBranch.setLastModifiedBy(updatedBranch.getLastModifiedBy());
			clientCompanyBranch.setLastModifiedOn(dateNow);

			clientCompanyBranchRepository.save(clientCompanyBranch);

			clientCompanyPIC.setPicName(picName);
			clientCompanyPIC.setPicEmail(updatedBranch.getPicEmail().trim());
			clientCompanyPIC.setPicPhone(updatedBranch.getPicPhone().trim());
			clientCompanyPIC.setPicDesc(updatedBranch.getPicDesc().trim());
			clientCompanyPIC.setLastModifiedBy(updatedBranch.getLastModifiedBy());
			clientCompanyPIC.setLastModifiedOn(dateNow);

			clientCompanyPICRepository.save(clientCompanyPIC);

			message = "Customer Branch Berhasil Diubah";

			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);
		} else if (clientCompanyBranchRepository.checkDupClientCompanyBranch(branchName, picName) != null){
			message = "Customer Branch Gagal Diubah, Nama Client Company Branch : '"+ branchName +"' dengan Nama PIC : '"+ picName +"'Sudah Terdaftar ";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		}
		return showHashMap;
	}

	@GetMapping("/listBranchByCompanyId")
	public HashMap<String, Object> listJobCategoryByJobClass(@RequestParam Long companyId) throws ParseException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		ArrayList<ClientCompanyBranch> listClientCompanyBranchEntity = (ArrayList<ClientCompanyBranch>) clientCompanyBranchRepository
				.getByCompanyId(companyId);

		for (ClientCompanyBranch item : listClientCompanyBranchEntity) {
			HashMap<String, Object> data = new HashMap<>();
			ClientCompanyBranchDTO clientCompanyBranchDTO = modelMapper.map(item, ClientCompanyBranchDTO.class);
			data.put("branchId", clientCompanyBranchDTO.getBranchId());
			data.put("branchName", clientCompanyBranchDTO.getBranchName());
			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;
	}

	@GetMapping("companyBranch/{companyId}")
	public HashMap<String, Object> showListBranchByCompany(@PathVariable(value = "companyId") Long companyId) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> data = new ArrayList<>();

		ArrayList<ClientCompanyBranch> listBranch = clientCompanyBranchRepository.getByCompanyId(companyId);
		for (ClientCompanyBranch item : listBranch) {
			HashMap<String, Object> dataBranch = new HashMap<String, Object>();
			dataBranch.put("Branch Id", item.getBranchId());
			dataBranch.put("Branch Name", item.getBranchName());
			SLA sla = slaRepository.getByBranchId(item.getBranchId());
			if (sla == null) {
				data.add(dataBranch);
			}
		}

		showHashMap.put("Status", HttpStatus.OK);
		showHashMap.put("Company Id", companyId);
		showHashMap.put("Branch", data);
		return showHashMap;
	}

	@PutMapping("deleteClientCompanyBranch/{branchId}")
	public HashMap<String, Object> deleteBranch(@PathVariable(value = "branchId") Long branchId,
			@RequestBody Users user) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		ClientCompanyBranch clientCompanyBranchEntity = clientCompanyBranchRepository.findById(branchId).orElse(null);
		Long userId = user.getUserId();
		String message = "";

		SLA existingsSLA = slaRepository.getByBranchId(branchId);
		TroubleTicket existingTroubleTicket = troubleTicketRepository.getTroubleTicketByBranchId(branchId);

		if (existingsSLA == null && existingTroubleTicket == null) {
			clientCompanyBranchEntity.setLastModifiedBy(userId);
			clientCompanyBranchEntity.setLastModifiedOn(dateNow);
			clientCompanyBranchEntity.setDeleted(true);
			clientCompanyBranchRepository.save(clientCompanyBranchEntity);

			ClientCompanyPIC clientCompanyPIC = clientCompanyPICRepository.getPICByBranchId(branchId);

			clientCompanyPIC.setLastModifiedBy(userId);
			clientCompanyPIC.setLastModifiedOn(dateNow);
			clientCompanyPIC.setDeleted(true);

			clientCompanyPICRepository.save(clientCompanyPIC);

			message = "Customer Branch Berhasil Dihapus";

			showHashMap.put("Status", HttpStatus.OK);
		} else {
			message = "Customer Branch Tidak Bisa Dihapus, Karena Masih Digunakan";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
		}

		showHashMap.put("Message", message);
		return showHashMap;
	}

	@GetMapping("/listClientCompanyBranch")
	public HashMap<String, Object> getListClientCompanyBranch() throws ParseException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		ArrayList<ClientCompanyBranch> listClientCompanyBranchEntity = (ArrayList<ClientCompanyBranch>) clientCompanyBranchRepository
				.findAllBranchs();

		for (ClientCompanyBranch item : listClientCompanyBranchEntity) {
			HashMap<String, Object> data = new HashMap<>();
			ClientCompanyBranchDTO clientCompanyBranchDTO = modelMapper.map(item, ClientCompanyBranchDTO.class);
			data.put("branchId", clientCompanyBranchDTO.getBranchId());
			data.put("branchName", clientCompanyBranchDTO.getBranchName());
			data.put("companyId", clientCompanyBranchDTO.getCompanyId().getCompanyId());
			listData.add(data);
		}
		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;

	}

}