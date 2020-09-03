package com.fsm.repositories;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fsm.models.JobClass;

@Repository
public interface JobClassRepository extends JpaRepository<JobClass, Long> {

	public static final String Get_Data_Job_Class = "SELECT * FROM job_class\n" 
			+ "WHERE is_deleted = FALSE AND (CAST(job_class_id as VARCHAR) LIKE concat('%',BTRIM(:search,' '),'%') OR\n" 
			+ "LOWER(job_class_name) LIKE LOWER(concat('%', BTRIM(:search,' '),'%')))";
	
	@Query(value = "SELECT * FROM job_class", nativeQuery = true)
	public List<JobClass> getCities();

	@Modifying
	@Query(value = "UPDATE job_class SET job_Class_Name =:jobClassName, last_Modified_By = :lastModifiedBy, last_Modified_On = :lastModifiedOn WHERE job_class_id = :jobClassId", nativeQuery = true)
	@Transactional
	void update(@Param("jobClassId") Long jobClassId, @Param("jobClassName") String jobClassName,
			@Param("lastModifiedBy") Long lastModifiedBy, @Param("lastModifiedOn") Timestamp lastModifiedOn);

	@Query(value = "SELECT job_class_id FROM job_class WHERE LOWER(job_class_name) = LOWER(TRIM(:jobClassName,' ')) limit 1", nativeQuery = true)
	Long findJobClassIdByName(@Param("jobClassName") String jobClassName);

	@Query(value = "SELECT * FROM job_class WHERE is_deleted = false", nativeQuery = true)
	ArrayList<JobClass> findAllJobClasses();
	
	@Query(value = "SELECT * FROM job_class\n" + 
			"WHERE LOWER(job_class_name) = LOWER(TRIM(:jobClassName)) AND is_deleted = FALSE", nativeQuery = true)
	ArrayList<JobClass> checkJobClassName (@Param("jobClassName") String jobClassName);
	
	@Query(value = "SELECT * FROM job_class\n" + 
			"WHERE LOWER(job_class_name) = LOWER(TRIM(:jobClassName)) AND job_class_id NOT IN (:jobClassId) AND is_deleted = FALSE", nativeQuery = true)
	ArrayList<JobClass> checkDuplicateData (@Param("jobClassName") String jobClassName, @Param("jobClassId") Long jobClassId);

	@Query(value = Get_Data_Job_Class , nativeQuery = true)
	ArrayList<JobClass> getDataJobClass(@Param("search") String search, Pageable pageable);
}
