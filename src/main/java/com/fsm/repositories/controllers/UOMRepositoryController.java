package com.fsm.repositories.controllers;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
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
import com.fsm.dtos.UOMDTO;
import com.fsm.models.Job;
import com.fsm.models.UOM;
import com.fsm.models.Users;
import com.fsm.repositories.JobRepository;
import com.fsm.repositories.UOMRepository;

@RestController
@RequestMapping("uom_repo")
public class UOMRepositoryController {

	ModelMapper modelMapper = new ModelMapper();

	@Autowired
	UOMRepository uomRepository;

	@Autowired
	JobRepository jobRepository;

	public UOMDTO convertToDTO(UOM uom) {
		UOMDTO uomDto = modelMapper.map(uom, UOMDTO.class);
		return uomDto;
	}

	// Get All
	@GetMapping("/listUOM")
	public Map<String, Object> getAllListWorkingTime(@RequestParam String search, Pageable pageable) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();
		ArrayList<UOM> listUOMEntity = (ArrayList<UOM>) uomRepository.getListUOM(search, pageable);
		int totalListUOM = uomRepository.getTotalListUOM(search);
		int totalListPage = (int) Math.ceil((listData.size() / 10) + 1);
		for (UOM item : listUOMEntity) {
			HashMap<String, Object> data = new HashMap<>();

			UOMDTO uomDTO = modelMapper.map(item, UOMDTO.class);

			data.put("uomId", uomDTO.getUomId());
			data.put("uomName", uomDTO.getUomName());

			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);
		result.put("totalListWorkingTime", totalListUOM);
		result.put("totalListPage", totalListPage);

		return result;
	}

	// Create a new UOM DTO Mapper
	@PostMapping("/UOM/create")
	public HashMap<String, Object> createUOM(@Valid @RequestBody UOMDTO body) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		UOM uom = new UOM();

		String uomName = body.getUomName().trim();

		if(uomRepository.checkDupUom(uomName) == null){
			uom.setUomName(uomName);
			uom.setCreatedBy(body.getCreatedBy());
			uom.setCreatedOn(dateNow);
			uom.setLastModifiedBy(body.getLastModifiedBy());
			uom.setLastModifiedOn(dateNow);
			uom.setDeleted(false);
			uomRepository.save(uom);
			body.setUomId(uom.getUomId());

			// Maping result untuk response API
			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", "UOM Berhasil Dibuat");
			showHashMap.put("Data", body);
		} else {
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", "UOM Gagal Diubah. Nama UOM : '" + uomName +"' Telah Digunakan");
		}

		return showHashMap;
	}

	// update cara dari kang satryo
	@PutMapping("/UOM/update/{id}")
	public HashMap<String, Object> updateUOM(@PathVariable(value = "id") Long id, @Valid @RequestBody UOMDTO body) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		UOM uom = uomRepository.findById(id).orElse(null);

		String uomName = body.getUomName().trim();

		if(uomRepository.checkDupUom(uomName) == null){
			uom.setUomName(uomName);
			uom.setLastModifiedBy(body.getLastModifiedBy());
			uom.setLastModifiedOn(dateNow);
			uomRepository.save(uom);
			body.setUomId(uom.getUomId());

			// Maping result untuk response API
			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", "UOM Berhasil Diubah");
			showHashMap.put("Data", body);
		} else if(uomRepository.checkDupUom(uomName) == uom){
			uom.setUomName(uomName);
			uom.setLastModifiedBy(body.getLastModifiedBy());
			uom.setLastModifiedOn(dateNow);
			uomRepository.save(uom);
			body.setUomId(uom.getUomId());

			// Maping result untuk response API
			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", "UOM Berhasil Diubah");
			showHashMap.put("Data", body);
		} else {
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", "UOM Gagal Diubah. Nama UOM : '" + uomName +"' Telah Digunakan");
		}

		return showHashMap;
	}

	@PutMapping("/UOM/delete/{uomId}")
	public HashMap<String, Object> deleteUOM(@PathVariable(value = "uomId") Long uomId,
			@Valid @RequestBody Users user) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);

		UOM uomEntity = uomRepository.findById(uomId).orElse(null);
		Job jobEntity = jobRepository.findUomById(uomId);
		if (jobEntity == null) {
			String message = "UOM Berhasil Dihapus";
			uomEntity.setDeleted(true);
			uomEntity.setLastModifiedBy(user.getUserId());
			uomEntity.setLastModifiedOn(dateNow);
			uomRepository.save(uomEntity);
			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);
		} else if (jobEntity != null) {
			String message = "UOM Tidak Bisa Dihapus, Karena Data Masih Digunakan";
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		}
		return showHashMap;
	}

}
