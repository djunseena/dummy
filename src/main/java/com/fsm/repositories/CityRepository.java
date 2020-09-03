package com.fsm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fsm.models.City;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {

}
