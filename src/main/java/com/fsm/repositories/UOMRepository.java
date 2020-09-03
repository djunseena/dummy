package com.fsm.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

import com.fsm.models.UOM;

@Repository
public interface UOMRepository extends JpaRepository<UOM, Long> {

    @Query(value = "SELECT * FROM uom WHERE LOWER(uom_name) = BTRIM(LOWER(:uomName),' ') AND is_deleted = false LIMIT 1", nativeQuery = true)
    UOM checkDupUom(@Param("uomName") String uomName);

    @Query(value = "SELECT * FROM uom WHERE is_deleted = false AND (CAST(uom_id AS VARCHAR) LIKE CONCAT('%', :search,'%') OR \n"
    + "     LOWER(uom_name) LIKE LOWER(CONCAT('%', :search,'%'))) ", nativeQuery = true )
    ArrayList<UOM> getListUOM(@Param("search") String search, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM uom WHERE is_deleted = false AND (CAST(uom_id AS VARCHAR) LIKE CONCAT('%', :search,'%') OR \n"
    + "     LOWER(uom_name) LIKE LOWER(CONCAT('%', :search,'%'))) ", nativeQuery = true )
    Integer getTotalListUOM(@Param("search") String search);

}
