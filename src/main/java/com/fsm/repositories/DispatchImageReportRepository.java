package com.fsm.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fsm.models.DispatchImageReport;

@Repository
public interface DispatchImageReportRepository extends JpaRepository<DispatchImageReport, Long> {

	@Query(value = "SELECT * FROM dispatch_image_report WHERE dispatch_report_id = :dispatchReportId", nativeQuery = true)
	List<DispatchImageReport> findListByDispatchReportId(@Param("dispatchReportId") Long dispatchReportId);

	@Query(value = "SELECT * FROM dispatch_image_report WHERE dispatch_report_id = :dispatchReportId", nativeQuery = true)
	DispatchImageReport findByDispatchReportId(@Param("dispatchReportId") Long dispatchReportId);

	@Query(value = "SELECT * FROM dispatch_image_report WHERE dispatch_report_id = :dispatchReportId AND image_report_type = 37", nativeQuery = true)
	DispatchImageReport getSignatureOfDispatchImageReportByDispatchReportId(
			@Param("dispatchReportId") Long dispatchReportId);

	@Query(value = "SELECT * FROM dispatch_image_report WHERE dispatch_report_id = :dispatchReportId AND image_report_type = 38", nativeQuery = true)
	List<DispatchImageReport> getImageReportOfDispatchImageReportByDispatchReportId(
			@Param("dispatchReportId") Long dispatchReportId);

}
