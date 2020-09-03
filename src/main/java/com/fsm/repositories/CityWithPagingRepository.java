package com.fsm.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fsm.models.City;

@Repository
public interface CityWithPagingRepository extends PagingAndSortingRepository<City, Long> {

//	Code Query untuk pagination dan filter city (Start)
	@Query(value = "SELECT * FROM city JOIN province ON (city.province_id = province.province_id) WHERE city.city_name ~* :filter OR province.province_name ~* :filter", nativeQuery = true)
	public Slice<City> getFilter(@Param("filter") String filter, Pageable pagable);
//	Code Query untuk pagination dan filter city (Finish)	
}
