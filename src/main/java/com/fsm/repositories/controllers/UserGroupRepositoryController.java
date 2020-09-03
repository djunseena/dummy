package com.fsm.repositories.controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import com.fsm.dtos.UserGroupDTO;
import com.fsm.models.UserGroup;
import com.fsm.repositories.UserGroupRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userGroup")
public class UserGroupRepositoryController {

    @Autowired
    UserGroupRepository userGroupRepository;

    @GetMapping("/list")
	public HashMap<String, Object> showListUserGroup() throws ParseException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		ArrayList<UserGroup> listUserGroupEntity = (ArrayList<UserGroup>) userGroupRepository.listUserGroup();

		for (UserGroup item : listUserGroupEntity) {
			HashMap<String, Object> data = new HashMap<>();
			UserGroupDTO userGroupDTO = modelMapper.map(item, UserGroupDTO.class);
			data.put("groupUserId", userGroupDTO.getUserGroupId());
			data.put("groupUserName", userGroupDTO.getUserGroupName());
			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;

	}
    
}