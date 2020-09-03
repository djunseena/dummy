package com.fsm.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.ArrayList;

import com.fsm.models.WorkingTime;

@Repository
public interface WorkingTimeRepository extends JpaRepository<WorkingTime, Long>{

    @Query(value = "SELECT * FROM working_time WHERE LOWER(wtime_name) = BTRIM(LOWER(:wtime_name),' ') AND wtime_start = :wtime_start AND wtime_end = :wtime_end AND is_deleted = false LIMIT 1", nativeQuery = true)
    WorkingTime findWorkingTimeByWorkingTime(@Param("wtime_name") String wtimeName, @Param("wtime_start") Time wtimeStart, @Param("wtime_end") Time wtimeEnd);
    
    @Query(value = "SELECT * FROM working_time WHERE is_deleted = false", nativeQuery = true)
    ArrayList<WorkingTime> listWorkingTime();

    @Query(value = "SELECT * FROM working_time WHERE is_deleted = false AND (CAST(wtime_id AS VARCHAR) LIKE CONCAT('%', :search,'%') OR \n"
    + "     LOWER(wtime_name) LIKE LOWER(CONCAT('%', :search,'%')) OR \n"
    + "     LOWER(wtime_desc) LIKE LOWER(CONCAT('%', :search,'%'))) ", nativeQuery = true )
    ArrayList<WorkingTime> getListWorkingTime(@Param("search") String search, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM working_time WHERE is_deleted = false AND (CAST(wtime_id AS VARCHAR) LIKE CONCAT('%', :search,'%') OR \n"
    + "     LOWER(wtime_name) LIKE LOWER(CONCAT('%', :search,'%')) OR \n"
    + "     LOWER(wtime_desc) LIKE LOWER(CONCAT('%', :search,'%'))) ", nativeQuery = true )
    Integer getTotalListWorkingTime(@Param("search") String search);
}
