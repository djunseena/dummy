package com.fsm.repositories.controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import com.fsm.dtos.SLATypeDTO;
import com.fsm.models.SLAType;
import com.fsm.repositories.SLATypeRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("slaType")
public class SLATypeRepositoryController {
    
    @Autowired
    SLATypeRepository slaTypeRepository;
    
    @GetMapping("listSLAType")
	public HashMap<String, Object> showListSLAType() throws ParseException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		ArrayList<SLAType> listSLATypeEntity = (ArrayList<SLAType>) slaTypeRepository.getAllTypes();

		for (SLAType item : listSLATypeEntity) {
			HashMap<String, Object> data = new HashMap<>();
			SLATypeDTO slaTypeDTO = modelMapper.map(item, SLATypeDTO.class);
			data.put("slaTypeId", slaTypeDTO.getSlaTypeId());
			data.put("slaTypeName", slaTypeDTO.getSlaTypeName());
			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;

	}
}