package com.fsm.repositories.controllers;

import java.sql.Timestamp;
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

import com.fsm.dtos.RoleDTO;
import com.fsm.models.Role;
import com.fsm.models.Users;
import com.fsm.repositories.RoleRepository;
import com.fsm.repositories.UsersRepository;

@RestController
@RequestMapping("api")
public class RoleRepositoryController {
	@Autowired
	RoleRepository roleRepository;

	@Autowired
	UsersRepository usersRepository;

	@GetMapping("/listRole")
	public Map<String, Object> getAllRole(@RequestParam String search, Pageable pageable) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();
		ArrayList<Role> listRoleEntity = (ArrayList<Role>) roleRepository.getListRole(search, pageable);
		int totalListRole = roleRepository.getTotalListRole(search);
		int totalListPage = (int) Math.ceil((listData.size() / 10) + 1);
		for (Role item : listRoleEntity) {
			HashMap<String, Object> data = new HashMap<>();

			RoleDTO roleDTO = modelMapper.map(item, RoleDTO.class);

			data.put("roleId", roleDTO.getRoleId());
			data.put("roleName", roleDTO.getRoleName());
			data.put("userGroupId", roleDTO.getUserGroupId().getUserGroupId());
			data.put("userGroupName", roleDTO.getUserGroupId().getUserGroupName());

			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);
		result.put("totalListSLA", totalListRole);
		result.put("totalListPage", totalListPage);

		return result;
	}

	@PostMapping("role/create")
	public HashMap<String, Object> createRole(@RequestBody Role newRole) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		Role role = new Role();

		String roleName = newRole.getRoleName().trim();
		Long userGroupId = newRole.getUserGroupId().getUserGroupId();

		if (roleRepository.findRoleByRole(roleName, userGroupId) == null){
			role.setRoleName(roleName);
			role.setUserGroupId(newRole.getUserGroupId());
			role.setCreatedBy(newRole.getCreatedBy());
			role.setCreatedOn(dateNow);
			role.setLastModifiedBy(newRole.getCreatedBy());
			role.setLastModifiedOn(dateNow);
			role.setDeleted(false);

			roleRepository.save(role);

			message = "Role Berhasil Dibuat";

			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);

		} else {

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", "Role Gagal Dibuat. Nama Role : '" + roleName 
			+"' dengan Group User : '"+ newRole.getUserGroupId().getUserGroupName() +"' Telah Digunakan");
		 }

	return showHashMap;

	}

	@PutMapping("role/update/{roleId}")
	public HashMap<String, Object> updateRole(@RequestBody Role newRole, @PathVariable(value = "roleId") Long roleId) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		Role role = roleRepository.findById(roleId).orElse(null);

		String roleName = newRole.getRoleName().trim();
		Long userGroupId = newRole.getUserGroupId().getUserGroupId();

		if (roleRepository.findRoleByRole(roleName, userGroupId) == null){
			role.setRoleName(roleName);
			role.setUserGroupId(newRole.getUserGroupId());
			role.setLastModifiedBy(newRole.getLastModifiedBy());
			role.setLastModifiedOn(dateNow);
			role.setDeleted(false);

			roleRepository.save(role);

			message = "Role Berhasil Diubah";

			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);

		} else if (roleRepository.findRoleByRole(roleName, userGroupId) == role){
			role.setRoleName(roleName);
			role.setUserGroupId(newRole.getUserGroupId());
			role.setLastModifiedBy(newRole.getLastModifiedBy());
			role.setLastModifiedOn(dateNow);
			role.setDeleted(false);

			roleRepository.save(role);

			message = "Role Berhasil Diubah";

			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);
		} else {

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", "Role Gagal Diubah. Nama Role : '"+ roleName 
			+"' dengan Group User : '"+ newRole.getUserGroupId().getUserGroupName() +"' Telah Digunakan");
		 }

	return showHashMap;

	}

	@PutMapping("role/delete/{roleId}")
	public HashMap<String, Object> deleteRole(@PathVariable(value = "roleId") Long roleId, @RequestBody Users user) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		Role roleEntity = roleRepository.findById(roleId).orElse(null);
		Users userEntity = usersRepository.findByRoleId(roleId);
		if (userEntity == null) {
			String message = "Role Berhasil Dihapus";
			roleEntity.setDeleted(true);
			roleEntity.setLastModifiedBy(user.getUserId());
			roleEntity.setLastModifiedOn(dateNow);
			roleRepository.save(roleEntity);
			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);
		} else if (userEntity != null) {
			String message = "Role Gagal Dihapus, Karena Data Masih Digunakan";
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		}
		return showHashMap;
	}
}
