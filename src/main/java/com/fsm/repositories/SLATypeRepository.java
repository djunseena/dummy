package com.fsm.repositories;

import java.util.ArrayList;

import com.fsm.models.SLAType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SLATypeRepository extends JpaRepository<SLAType, Long>{

    @Query(value = "SELECT * FROM sla_type WHERE is_deleted = false", nativeQuery = true)
    ArrayList<SLAType> getAllTypes();
    
}