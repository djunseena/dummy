package com.fsm.repositories.controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.CodeDTO;
import com.fsm.models.Code;
import com.fsm.repositories.CodeRepository;

@RestController
@RequestMapping("/code")
public class CodeRepositoryController {

	@Autowired
	CodeRepository codeRepository;


	@CrossOrigin(allowCredentials = "true")
	@GetMapping("/userIdentity")
	public HashMap<String, Object> showListUserIdentity() throws ParseException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		ArrayList<Code> listCodeEntity = (ArrayList<Code>) codeRepository.findByCodeUserIdentity();

		for (Code item : listCodeEntity) {
			HashMap<String, Object> data = new HashMap<>();
			CodeDTO codeDTO = modelMapper.map(item, CodeDTO.class);
			data.put("codeId", codeDTO.getCodeId());
			data.put("codeName", codeDTO.getCodeName());
			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;

	}

	@GetMapping("/priority")
	public HashMap<String, Object> showListPriority() throws ParseException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		ArrayList<Code> listCodeEntity = (ArrayList<Code>) codeRepository.findByCodePriority();

		for (Code item : listCodeEntity) {
			HashMap<String, Object> data = new HashMap<>();
			CodeDTO codeDTO = modelMapper.map(item, CodeDTO.class);
			data.put("codeId", codeDTO.getCodeId());
			data.put("codeName", codeDTO.getCodeName());
			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;

	}

	@GetMapping("/report")
	public HashMap<String, Object> showListReport() throws ParseException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		ArrayList<Code> listCodeEntity = (ArrayList<Code>) codeRepository.findByCodeReport();

		for (Code item : listCodeEntity) {
			HashMap<String, Object> data = new HashMap<>();
			CodeDTO codeDTO = modelMapper.map(item, CodeDTO.class);
			data.put("codeId", codeDTO.getCodeId());
			data.put("codeName", codeDTO.getCodeName());
			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;

	}

	@GetMapping("/category")
	public HashMap<String, Object> showListCategory() throws ParseException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		ArrayList<Code> listCodeEntity = (ArrayList<Code>) codeRepository.findByCodeCategory();

		for (Code item : listCodeEntity) {
			HashMap<String, Object> data = new HashMap<>();
			CodeDTO codeDTO = modelMapper.map(item, CodeDTO.class);
			data.put("codeId", codeDTO.getCodeId());
			data.put("codeName", codeDTO.getCodeName());
			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;

	}
}
