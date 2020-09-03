package com.fsm.repositories.controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.ClientCompanyPICDTO;
import com.fsm.models.ClientCompanyPIC;
import com.fsm.repositories.ClientCompanyPICRepository;

@RestController
@RequestMapping("api")
public class ClientCompanyPICRepositoryController {

	ModelMapper modelMapper = new ModelMapper();

	@Autowired
	ClientCompanyPICRepository clientCompanyPICRepository;

	@GetMapping("/listPICByBranchId")
	public HashMap<String, Object> listJobCategoryByJobClass(@RequestParam Long branchId) throws ParseException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		ArrayList<ClientCompanyPIC> listClientCompanyPICEntity = (ArrayList<ClientCompanyPIC>) clientCompanyPICRepository
				.findIdByBranchId(branchId);

		for (ClientCompanyPIC item : listClientCompanyPICEntity) {
			HashMap<String, Object> data = new HashMap<>();
			ClientCompanyPICDTO clientCompanyPICDTO = modelMapper.map(item, ClientCompanyPICDTO.class);
			data.put("picId", clientCompanyPICDTO.getPicId());
			data.put("picName", clientCompanyPICDTO.getPicName());
			data.put("branchId", clientCompanyPICDTO.getBranchId().getBranchId());
			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;

	}

	@GetMapping("/listClientCompanyPIC")
	public HashMap<String, Object> getListClientCompanyPIC() throws ParseException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		ArrayList<ClientCompanyPIC> listClientCompanyPICEntity = (ArrayList<ClientCompanyPIC>) clientCompanyPICRepository
				.findAllCPics();

		for (ClientCompanyPIC item : listClientCompanyPICEntity) {
			HashMap<String, Object> data = new HashMap<>();
			ClientCompanyPICDTO clientCompanyPICDTO = modelMapper.map(item, ClientCompanyPICDTO.class);
			data.put("picId", clientCompanyPICDTO.getPicId());
			data.put("picName", clientCompanyPICDTO.getPicName());
			data.put("branchId", clientCompanyPICDTO.getBranchId().getBranchId());
			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;

	}
}