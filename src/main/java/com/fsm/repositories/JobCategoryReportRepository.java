package com.fsm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;

import javax.transaction.Transactional;

import com.fsm.models.JobCategoryReport;

public interface JobCategoryReportRepository extends JpaRepository<JobCategoryReport, Long> {

	@Query(value = "select * From job_category_report join job_category ON job_category_report.job_category_id = job_category.job_category_id WHERE job_category_report.job_category_id= :job_category_id", nativeQuery = true)
	public JobCategoryReport findByJobCategoryId(@Param("job_category_id") long jobCategoryId);

	@Query(value = "select * from job_category_report where job_category_id = :jobCategoryId and is_deleted = false\n"
			+ "order by job_category_report_id limit 1", nativeQuery = true)
	JobCategoryReport findJobCategory(@Param("jobCategoryId") long jobCategoryId);

	@Query(value = "SELECT * FROM job_category_report WHERE job_category_id = :jobCategoryId AND is_deleted = false ORDER BY job_category_report_id ASC\n"
			+ "LIMIT 1", nativeQuery = true)
	JobCategoryReport getObjectJobCategoryReportByJobCategory(@Param("jobCategoryId") Long jobCategoryId);

	@Query(value = "SELECT * FROM job_category_report WHERE job_category_id = :jobCategoryId AND is_deleted = false", nativeQuery = true)
	ArrayList<JobCategoryReport> getJobCategoryReportByJobCategory(@Param("jobCategoryId") Long jobCategoryId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE job_category_report SET report_id = :report_id WHERE job_category_report_id = :job_category_report_id ", nativeQuery = true)
	void updateReport(@Param("report_id") Long reportId, @Param("job_category_report_id") Long jobCategoryReportId);

	@Modifying
	@Transactional
	@Query(value = "DELETE from job_category_report WHERE job_category_id = :job_category_id ", nativeQuery = true)
	void deleteReport(@Param("job_category_id") Long jobCategoryId);
}
