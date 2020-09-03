package com.fsm.repositories.controllers;

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
import com.fsm.dtos.SLADTO;
import com.fsm.models.SLA;
import com.fsm.models.TroubleTicket;
import com.fsm.models.Users;
import com.fsm.repositories.CodeRepository;
import com.fsm.repositories.SLARepository;
import com.fsm.repositories.TroubleTicketRepository;

@RestController
@RequestMapping("SLA")
public class SLARepositoryController {

	@Autowired
	SLARepository slaRepository;

	@Autowired
	CodeRepository codeRepository;

	@Autowired
	TroubleTicketRepository troubleTikcetRepository;

	@GetMapping("/listSLA")
	public Map<String, Object> getAllList(@RequestParam String search, Pageable pageable) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();
		ArrayList<SLA> listSLANameEntity = (ArrayList<SLA>) slaRepository.getListSLA(search, pageable);
		int totalListSLA = slaRepository.getTotalListSLA(search);
		int totalListPage = (int) Math.ceil((listData.size() / 10) + 1);
		for (SLA SLAitem : listSLANameEntity) {
			HashMap<String, Object> data = new HashMap<>();

			SLADTO slaDTO = modelMapper.map(SLAitem, SLADTO.class);

			data.put("slaId", slaDTO.getSlaId());
			data.put("companyId", slaDTO.getBranchId().getCompanyId().getCompanyId());
			data.put("companyName", slaDTO.getBranchId().getCompanyId().getCompanyName());
			data.put("branchId", slaDTO.getBranchId().getBranchId());
			data.put("branchName", slaDTO.getBranchId().getBranchName());
			data.put("slaTypeId", slaDTO.getSlaTypeId().getSlaTypeId());
			data.put("slaTypeName", slaDTO.getSlaTypeId().getSlaTypeName());
			data.put("wTimeId", slaDTO.getWTimeId().getWTimeId());
			data.put("wTimeName", slaDTO.getWTimeId().getWTimeName());
			data.put("slaResolutionTime", slaDTO.getSlaResolutionTime());
			data.put("slaResponseTime", slaDTO.getSlaResponseTime());
			data.put("includeWeekend", slaDTO.isIncludeWeekend());
			data.put("cityName", slaDTO.getBranchId().getCityId().getCityName());

			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);
		result.put("totalListSLA", totalListSLA);
		result.put("totalListPage", totalListPage);

		return result;
	}

	@PostMapping("create")
	public HashMap<String, Object> createSLA(@RequestBody SLA newSLA) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		SLA sla = new SLA();

		Long wTimeId = newSLA.getWTimeId().getWTimeId();
		Long slaTypeId = newSLA.getSlaTypeId().getSlaTypeId();
		int slaResponseTime = newSLA.getSlaResponseTime();
		int slaResolutionTime = newSLA.getSlaResolutionTime();
		boolean includeWeekend = newSLA.isIncludeWeekend();
		Long branchId = newSLA.getBranchId().getBranchId();

		if(slaRepository.checkDupSLA(slaTypeId, wTimeId, slaResponseTime, slaResolutionTime, includeWeekend, branchId)== null) {
			sla.setSlaTypeId(newSLA.getSlaTypeId());
			sla.setWTimeId(newSLA.getWTimeId());
			sla.setSlaResolutionTime(slaResolutionTime);
			sla.setSlaResponseTime(slaResponseTime);
			sla.setIncludeWeekend(includeWeekend);
			sla.setBranchId(newSLA.getBranchId());
			sla.setCreatedBy(newSLA.getCreatedBy());
			sla.setCreatedOn(dateNow);
			sla.setLastModifiedBy(newSLA.getLastModifiedBy());
			sla.setLastModifiedOn(dateNow);
			sla.setDeleted(false);

			slaRepository.save(sla);
			message = "SLA Berhasil Dibuat";

			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);
		} else {
			message = "SLA Gagal Dibuat, SLA Sudah Terdaftar ";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		}
		return showHashMap;
	}

	@PutMapping("update/{id}")
	public HashMap<String, Object> updateSLA(@PathVariable("id") Long id, @RequestBody SLA updateSLA) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		String message = "";
		SLA sla = slaRepository.findById(id).orElse(null);

		Long wTimeId = updateSLA.getWTimeId().getWTimeId();
		Long slaTypeId = updateSLA.getSlaTypeId().getSlaTypeId();
		int slaResponseTime = updateSLA.getSlaResponseTime();
		int slaResolutionTime = updateSLA.getSlaResolutionTime();
		boolean includeWeekend = updateSLA.isIncludeWeekend();
		Long branchId = updateSLA.getBranchId().getBranchId();

		if(slaRepository.checkDupSLA(slaTypeId, wTimeId, slaResponseTime, slaResolutionTime, includeWeekend, branchId)== null) {
			sla.setSlaTypeId(updateSLA.getSlaTypeId());
			sla.setWTimeId(updateSLA.getWTimeId());
			sla.setSlaResolutionTime(slaResolutionTime);
			sla.setSlaResponseTime(slaResponseTime);
			sla.setIncludeWeekend(updateSLA.isIncludeWeekend());
			sla.setBranchId(updateSLA.getBranchId());
			sla.setLastModifiedBy(updateSLA.getLastModifiedBy());
			sla.setLastModifiedOn(dateNow);

			slaRepository.save(sla);
			message = "SLA Berhasil Diubah";

			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);
		} if(slaRepository.checkDupSLA(slaTypeId, wTimeId, slaResponseTime, slaResolutionTime, includeWeekend, branchId)== sla) {
			sla.setSlaTypeId(updateSLA.getSlaTypeId());
			sla.setWTimeId(updateSLA.getWTimeId());
			sla.setSlaResolutionTime(slaResolutionTime);
			sla.setSlaResponseTime(slaResponseTime);
			sla.setIncludeWeekend(updateSLA.isIncludeWeekend());
			sla.setBranchId(updateSLA.getBranchId());
			sla.setLastModifiedBy(updateSLA.getLastModifiedBy());
			sla.setLastModifiedOn(dateNow);

			slaRepository.save(sla);
			message = "SLA Berhasil Diubah";

			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);
		} else {
			message = "SLA Gagal Diubah, SLA Sudah Terdaftar ";

			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		}
		return showHashMap;
	}

	@GetMapping("listSlaName")
	public HashMap<String, Object> showListSlaName() throws ParseException {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();
		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

		ArrayList<SLA> listSLANameEntity = (ArrayList<SLA>) slaRepository.getAllListSLAIncludeTypeName();

		for (SLA item : listSLANameEntity) {
			HashMap<String, Object> data = new HashMap<>();
			SLADTO slaDTO = modelMapper.map(item, SLADTO.class);
			data.put("sla", slaDTO.getSlaId());
			data.put("slaName", slaDTO.getSlaTypeId().getSlaTypeName() + " | " + slaDTO.getSlaResponseTime()
					+ " min (resp) | " + slaDTO.getSlaResolutionTime() + " hr (resl)");
			listData.add(data);
		}

		result.put("Status", 200);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;

	}

	@PutMapping("delete/{SLAId}")
	public HashMap<String, Object> deleteSLA(@PathVariable(value = "SLAId") Long SLAId, @RequestBody Users user) {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		LocalDateTime localNow = LocalDateTime.now();
		Timestamp dateNow = Timestamp.valueOf(localNow);
		SLA SLAEntity = slaRepository.findById(SLAId).orElse(null);
		TroubleTicket ticketEntity = troubleTikcetRepository.findBySLAId(SLAId);
		if (ticketEntity == null) {
			String message = "SLA Berhasil Dihapus";
			SLAEntity.setDeleted(true);
			SLAEntity.setLastModifiedBy(user.getUserId());
			SLAEntity.setLastModifiedOn(dateNow);
			slaRepository.save(SLAEntity);
			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);
		} else if (ticketEntity != null) {
			String message = "SLA Gagal Dihapus, Karena Data Masih Digunakan";
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		}
		return showHashMap;
	}

	@GetMapping("/branchSLA/{branchId}")
	public Map<String, Object> getSLAByBranchId(@PathVariable(value = "branchId") long branchId) {
		Map<String, Object> result = new HashMap<>();
		ModelMapper modelMapper = new ModelMapper();

		SLA slaEntity = slaRepository.getByBranchId(branchId);

		HashMap<String, Object> data = new HashMap<String, Object>();
		SLADTO slaDTO = modelMapper.map(slaEntity, SLADTO.class);
		data.put("SLA", slaDTO.getSlaTypeId().getSlaTypeName() + " | " + slaDTO.getSlaResponseTime() + " | "
				+ slaDTO.getSlaResolutionTime());
		data.put("slaId", slaDTO.getSlaTypeId().getSlaTypeId());

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Successful");
		result.put("Data", data);

		return result;
	}

	@GetMapping("/listSLAWithBranchId")
	public Map<String, Object> getSLAWithBranchId() {
		HashMap<String, Object> result = new HashMap<String, Object>();

		ModelMapper modelMapper = new ModelMapper();

		ArrayList<HashMap<String, Object>> listData = new ArrayList<>();
		ArrayList<SLA> listSLANameEntity = (ArrayList<SLA>) slaRepository.getAllSLAList();

		for (SLA SLAitem : listSLANameEntity) {
			HashMap<String, Object> data = new HashMap<>();

			SLADTO slaDTO = modelMapper.map(SLAitem, SLADTO.class);

			data.put("SLA", slaDTO.getSlaTypeId().getSlaTypeName() + " | " + slaDTO.getSlaResponseTime() + " | "
					+ slaDTO.getSlaResolutionTime());
			data.put("slaId", slaDTO.getSlaId());
			data.put("branchId", slaDTO.getBranchId().getBranchId());

			listData.add(data);
		}

		result.put("Status", HttpStatus.OK);
		result.put("Message", "Berhasil");
		result.put("Data", listData);

		return result;
	}
}
