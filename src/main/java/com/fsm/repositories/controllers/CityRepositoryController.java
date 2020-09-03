package com.fsm.repositories.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.dtos.CityDTO;
import com.fsm.models.City;
import com.fsm.repositories.CityRepository;
import com.fsm.repositories.CityWithPagingRepository;

@RestController
@RequestMapping("cityRepo")
public class CityRepositoryController {

	ModelMapper modelMapper = new ModelMapper();

	@Autowired
	CityRepository cityRepository;

	public CityDTO convertToDTO(City city) {
		CityDTO cityDto = modelMapper.map(city, CityDTO.class);
		return cityDto;
	}

	private City convertToEntity(CityDTO cityDto) {
		City city = modelMapper.map(cityDto, City.class);
		return city;
	}

//Get All User
	@CrossOrigin(allowCredentials = "true")
	@GetMapping("/City/all")
	public HashMap<String, Object> getAllCity() {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();
		List<CityDTO> listCity = new ArrayList<CityDTO>();
		for (City tempCity : cityRepository.findAll()) {
			CityDTO cityDTO = convertToDTO(tempCity);
			if (cityDTO.isDeleted() == false) {
				listCity.add(cityDTO);
			}
		}

		String message;
		if (listCity.isEmpty()) {
			message = "Data Kosong";
		} else {
			message = "Data City";
		}
		showHashMap.put("Message", message);
		showHashMap.put("Total", listCity.size());
		showHashMap.put("Data", listCity);

		return showHashMap;
	}

	@PutMapping("/City/update/{id}")
	public HashMap<String, Object> update(@PathVariable(value = "id") int id, @Valid @RequestBody CityDTO cityDTO) {
		HashMap<String, Object> process = new HashMap<String, Object>();
		City tempCity = cityRepository.findById(id).orElse(null);

		cityDTO.setCityId(tempCity.getCityId());
		if (cityDTO.getCityName() == null) {
			cityDTO.setCityName(tempCity.getCityName());
		}

		tempCity = convertToEntity(cityDTO);

		cityRepository.save(tempCity);
		process.put("Message", "Success Updated Data");
		process.put("Data", tempCity);
		return process;
	}

//	Code untuk get city dengan filter dan pagination(start)
	@Autowired
	private CityService service;

	@GetMapping("/cityFilter")
	public HashMap<String, Object> getCityByFilter(@RequestParam(value = "pageNo") Integer pageNo,
			@RequestParam(value = "pageSize") Integer pageSize, @RequestParam(value = "filter") String filter) {

		HashMap<String, Object> mapResult = new HashMap<String, Object>();
		ArrayList<CityDTO> listCityDto = new ArrayList<CityDTO>();

		for (City city : service.getCityServiceByFilter(pageNo, pageSize, filter)) {
			CityDTO cityDto = convertToDTO(city);
			listCityDto.add(cityDto);
		}
		
		int total = service.getTotalCityServiceByFilter(pageNo, 250, filter);
		
		String message;
		if (listCityDto.isEmpty()) {
			message = "Data is Empty";
		} else {
			message = "Show Data By Filter";
		}

		mapResult.put("Message", message);
		mapResult.put("TotalItems", total);
		mapResult.put("Data", listCityDto);

		return mapResult;
	}

	@Service
	public class CityService {

		@Autowired
		private CityWithPagingRepository cityWithPagingRepository;

//		Code For get data city
		public List<City> getCityServiceByFilter(Integer pageNo, Integer pageSize, String filter) {
			Pageable paging = PageRequest.of(pageNo, pageSize);

			Slice<City> pagedResult = cityWithPagingRepository.getFilter(filter, paging);

			List<City> listCity = pagedResult.getContent();
						
			return listCity;
		}
		
//		Code For get total data city
		public int getTotalCityServiceByFilter(Integer pageNo, Integer pageSize, String filter) {
			Pageable paging = PageRequest.of(pageNo, pageSize);

			Slice<City> pagedResult = cityWithPagingRepository.getFilter(filter, paging);

			int totalCity = pagedResult.getNumberOfElements();
						
			return totalCity;
		}
	}
//	Code untuk get city dengan filter dan pagination(finish)
}
