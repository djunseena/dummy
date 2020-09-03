package com.fsm.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import com.fsm.models.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

	public static final String Get_Data_Job = "SELECT * FROM job j\n" 
			+ "JOIN job_category jc ON jc.job_category_id = j.job_category_id\n"
			+ "JOIN uom  u ON u.uom_id = j.uom_id\n"
			+ "WHERE j.is_deleted = FALSE AND (CAST(job_id as VARCHAR) LIKE concat('%',BTRIM(:search,' '),'%') OR \n" 
			+ "LOWER(j.job_name) LIKE LOWER(concat('%',BTRIM(:search,' '),'%')) OR\n" 
			+ "LOWER(j.job_tag) LIKE LOWER(concat('%', BTRIM(:search,' '), '%')) OR\n" 
			+ "LOWER(j.job_desc) LIKE LOWER(concat('%',BTRIM(:search,' '), '%')))";
	

	@Query(value = "SELECT job_id FROM job WHERE job_category_id = :jobCategoryId AND LOWER(job_name) = LOWER(TRIM(:jobName,' ')) limit 1", nativeQuery = true)
	Long findJobIdByNameAndJobCategoryId(@Param("jobCategoryId") Long jobCategoryId, @Param("jobName") String jobName);

	@Query(value = "SELECT * FROM job WHERE job_category_id = :jobCategoryId", nativeQuery = true)
	ArrayList<Job> findIdByJobCategoryId(@Param("jobCategoryId") Long jobCategoryId);

	@Query(value = "SELECT * FROM job WHERE is_deleted = false", nativeQuery = true)
	ArrayList<Job> findAllJobs();

	@Query(value = "select * from job where job_category_id = :jobCategoryId and is_deleted = false\n"
			+ "order by job_id limit 1", nativeQuery = true)
	Job findJobCategory(@Param("jobCategoryId") Long jobCategoryId);

	@Query(value = "SELECT * FROM job WHERE uom_id = :uomId limit 1", nativeQuery = true)
	Job findUomById(@Param("uomId") Long uomId);
	
	@Query(value = "SELECT * FROM job\n" + 
			"WHERE LOWER(job_name) = LOWER(TRIM(:jobName))  AND job_category_id = :jobCategoryId AND is_deleted = FALSE",nativeQuery = true)
	ArrayList<Job> checkDataIsAlreadyExist(@Param("jobName")String jobName, @Param("jobCategoryId") Long jobCategoryId);
	
	@Query(value = "SELECT * FROM job WHERE LOWER(job_name) = LOWER(TRIM(:jobName)) AND job_category_id = :jobCategoryId AND job_id NOT IN (:jobId) AND is_deleted = FALSE",nativeQuery = true)
	ArrayList<Job> checkDuplicateUpdate(@Param("jobName")String jobName, @Param("jobCategoryId") Long jobCategoryId, @Param("jobId") Long jobId);

	@Query(value = Get_Data_Job , nativeQuery = true)
	ArrayList<Job> getDataJob (@Param("search") String search, Pageable pageable);
}
