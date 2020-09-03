package com.fsm.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fsm.models.DispatchReport;

@Repository
public interface DispatchReportRepository extends JpaRepository<DispatchReport, Long> {

	@Query(value = "SELECT * FROM dispatch_report WHERE order_id = :OrderId", nativeQuery = true)
	List<DispatchReport> findListByOrderId(@Param("OrderId") Long OrderId);

	@Query(value = "SELECT * FROM dispatch_report WHERE order_id = :OrderId", nativeQuery = true)
	DispatchReport findByOrderId(@Param("OrderId") Long OrderId);

	@Query(value = "SELECT * FROM dispatch_report as a RIGHT JOIN dispatch as b ON a.order_id = b.order_id WHERE a.order_id = :OrderId", nativeQuery = true)
	DispatchReport getByOrderId(@Param("OrderId") Long OrderId);
	
	@Query(value = "SELECT * FROM dispatch_report\n" + 
			"WHERE LOWER(dispatch_report_diagnostic) LIKE LOWER(concat('%', :search, '%')) OR\n" + 
			"	LOWER(dispatch_report_action) LIKE LOWER(concat('%',:search ,'%')) OR \n" + 
			"	LOWER(dispatch_report_reported_failure) LIKE LOWER(concat('%',:search,'%'))",nativeQuery = true)
	DispatchReport checkingData (@Param("search") String search);
}