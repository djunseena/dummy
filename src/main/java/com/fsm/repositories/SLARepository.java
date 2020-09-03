package com.fsm.repositories;

import java.util.ArrayList;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fsm.models.SLA;

public interface SLARepository extends JpaRepository<SLA, Long> {

	public static final String FIND_ListSLAIncludeTypeName = "SELECT * from sla as a \n"
			+ "		join sla_type as b ON a.sla_type_id = b.sla_type_id WHERE a.is_deleted = false";

	@Query(value = FIND_ListSLAIncludeTypeName, nativeQuery = true)
	public ArrayList<SLA> getAllListSLAIncludeTypeName();

	@Query(value = "SELECT * FROM sla WHERE branch_id = :branchId limit 1", nativeQuery = true)
	SLA getByBranchId(@Param("branchId") Long branchId);

	@Query(value = "SELECT sla_id FROM sla WHERE branch_id = :branchId limit 1", nativeQuery = true)
	Long findIdByBranchId(@Param("branchId") Long branchId);

	@Query(value = "SELECT * FROM sla WHERE is_deleted = false", nativeQuery = true)
	public ArrayList<SLA> getAllSLAList();

	@Query(value = "SELECT * FROM sla a JOIN sla_type b ON a.sla_type_id = b.sla_type_id \n"
	+ " 	JOIN working_time c ON a.wtime_id = c.wtime_id \n"
	+ " 	JOIN client_company_branch d ON a.branch_id = d.branch_id \n"
	+ " 	JOIN client_company e ON d.company_id = e.company_id \n"
	+ " 	JOIN city f ON d.city_id = f.city_id \n"
	+ " 	WHERE a.is_deleted = false AND (CAST(sla_id AS VARCHAR) LIKE CONCAT('%', :search ,'%') OR \n"
	+ " 	LOWER(e.company_name) LIKE LOWER(CONCAT('%', :search ,'%')) OR \n"
	+ " 	LOWER(d.branch_name) LIKE LOWER(CONCAT('%', :search ,'%')) OR \n"
	+ " 	LOWER(b.sla_type_name) LIKE LOWER(CONCAT('%', :search ,'%')) OR \n"
	+ " 	LOWER(f.city_name) LIKE LOWER(CONCAT('%', :search ,'%')))", nativeQuery = true)
	public ArrayList<SLA> getListSLA(@Param("search") String search, Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM sla a JOIN sla_type b ON a.sla_type_id = b.sla_type_id \n"
	+ " 	JOIN working_time c ON a.wtime_id = c.wtime_id \n"
	+ " 	JOIN client_company_branch d ON a.branch_id = d.branch_id \n"
	+ " 	JOIN client_company e ON d.company_id = e.company_id \n"
	+ " 	JOIN city f ON d.city_id = f.city_id \n"
	+ " 	WHERE a.is_deleted = false AND (CAST(sla_id AS VARCHAR) LIKE CONCAT('%', :search ,'%') OR \n"
	+ " 	LOWER(e.company_name) LIKE LOWER(CONCAT('%', :search ,'%')) OR \n"
	+ " 	LOWER(d.branch_name) LIKE LOWER(CONCAT('%', :search ,'%')) OR \n"
	+ " 	LOWER(b.sla_type_name) LIKE LOWER(CONCAT('%', :search ,'%')) OR \n"
	+ " 	LOWER(f.city_name) LIKE LOWER(CONCAT('%', :search ,'%')))", nativeQuery = true)
	Integer getTotalListSLA(@Param("search") String search);

	@Query(value = "SELECT * FROM sla WHERE wtime_id = :wTimeId limit 1", nativeQuery = true)
	SLA findByWorkingTimeId(@Param("wTimeId") Long wTimeId);

	@Query(value = "SELECT * FROM sla WHERE sla_type_id = :slaTypeId AND wtime_id = :wTimeId AND \n"
	+ " sla_response_time = :slaResponseTime AND sla_resolution_time = :slaResolutionTime AND is_include_weekend = :includeWeekend \n"
	+ " AND branch_id = :branchId AND is_deleted = false LIMIT 1", nativeQuery = true)
	SLA checkDupSLA(@Param("slaTypeId") Long slaTypeId, @Param("wTimeId") Long wTimeId, @Param("slaResponseTime") Integer slaResponseTime,
	@Param("slaResolutionTime") Integer slaResolutionTime, @Param("includeWeekend") Boolean includeWeekend, @Param("branchId") Long branchId);
}
