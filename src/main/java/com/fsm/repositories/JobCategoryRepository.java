package com.fsm.repositories;

import java.sql.Timestamp;
import java.util.ArrayList;
import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fsm.models.JobCategory;

@Repository
public interface JobCategoryRepository extends JpaRepository<JobCategory, Long> {

	public static final String Get_Data_Job_Category ="SELECT jc.* FROM job_category as jc\n" 
		+ "JOIN job_class as jcs ON jc.job_class_id = jcs.job_class_id\n"
		+ "WHERE jc.is_deleted = FALSE AND (CAST(job_category_id as VARCHAR) LIKE concat('%',BTRIM(:search,' '),'%') OR\r\n" 
		+ "LOWER(jc.job_category_name) LIKE LOWER(concat('%',BTRIM(:search,' '),'%')) OR\r\n" 
		+ "LOWER(jc.job_category_tag) LIKE LOWER(concat('%',BTRIM(:search,' '),'%')) OR\r\n" 
		+ "LOWER(jc.job_category_desc) LIKE LOWER(concat('%',BTRIM(:search,' '),'%')))";
	
	@Modifying
	@Query(value = "UPDATE job_category SET job_class_id = :jobClassId, job_category_name = :jobCategoryName, job_category_tag = :jobCategoryTag, job_category_desc = :jobCategoryDesc, last_Modified_By = :lastModifiedBy, last_Modified_On = :lastModifiedOn WHERE job_category_id = :jobCategoryId", nativeQuery = true)
	@Transactional
	void update(@Param("jobCategoryId") Long jobCategoryId, @Param("jobClassId") Long jobClassId,
			@Param("jobCategoryName") String jobCategoryName, @Param("jobCategoryTag") String jobCategoryTag,
			@Param("jobCategoryDesc") String jobCategoryDesc, @Param("lastModifiedBy") Long lastModifiedBy,
			@Param("lastModifiedOn") Timestamp lastModifiedOn);

	@Query(value = "SELECT job_category_id FROM job_category WHERE job_class_id = :jobClassId AND LOWER(job_category_name) = LOWER(TRIM(:jobCategoryName,' ')) limit 1", nativeQuery = true)
	Long findJobCategoryIdByNameAndJobClassId(@Param("jobClassId") Long jobClassId,
			@Param("jobCategoryName") String jobCategoryName);

	@Query(value = "SELECT * FROM job_category WHERE job_class_id = :jobClassId AND is_deleted = false", nativeQuery = true)
	ArrayList<JobCategory> findIdByJobClassId(@Param("jobClassId") Long jobClassId);

	@Query(value = "SELECT * FROM job_category WHERE is_deleted = false", nativeQuery = true)
	ArrayList<JobCategory> findAllJobCategories();

	@Query(value = "select * from job_category where job_class_id = :jobClassId and is_deleted = false\n"
			+ "order by job_category_id limit 1", nativeQuery = true)
	public JobCategory checkJobClass(@Param("jobClassId") Long jobClassId);
	
	@Query(value = "SELECT * FROM job_category\n" + 
			"WHERE LOWER(job_category_name) = LOWER(TRIM(:jobCategoryName)) AND job_class_id = :jobClassId AND is_deleted = FALSE", nativeQuery = true)
	ArrayList<JobCategory> checkDataIsExist (@Param("jobCategoryName") String jobCategoryName, @Param("jobClassId") Long jobClassId);
	
	@Query(value = "SELECT * FROM job_category WHERE LOWER(job_category_name) = LOWER(TRIM(:jobCategoryName)) AND job_class_id = :jobClassId AND job_category_id NOT IN (:jobCategoryId) AND is_deleted = FALSE", nativeQuery = true)
	ArrayList<JobCategory> checkDuplicateUpdate (@Param("jobCategoryName") String jobCategoryName, @Param("jobClassId") Long jobClassId, @Param("jobCategoryId") Long jobCategoryId);
	
	@Query(value = Get_Data_Job_Category,nativeQuery = true)
	ArrayList<JobCategory> getDataJobCategory(@Param("search")String search, Pageable pageable);

	@Query(value = "SELECT MAX(job_category_id) FROM job_category", nativeQuery = true)
	public long getLatestJobCategoryId();

	@Query(value = "SELECT * FROM job_category WHERE job_category_id = :jobCategoryId", nativeQuery = true)
	public JobCategory getJobCategoryByJobCategoryId(@Param("jobCategoryId") Long jobCategoryId);
	
}