package com.fsm.repositories;

import java.util.ArrayList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fsm.models.Diagnosis;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

	public static final String Get_Data_Diagnosis = "SELECT d.* FROM diagnosis d\n" 
			+ "JOIN job_category jc on d.job_category_id = jc.job_category_id\n" 
			+ "WHERE d.is_deleted = FALSE AND(CAST(diagnosis_id as VARCHAR) LIKE concat('%',BTRIM(:search,' '),'%') OR\n" 
			+ "LOWER(d.diagnosis_desc) LIKE LOWER(concat('%',BTRIM(:search,' '),'%')))";
	
	@Query(value = "SELECT * FROM \"diagnosis\" where diagnosis_desc ~* :filter and job_category_id = :jobCategoryId", nativeQuery = true)
	public Slice<Diagnosis> getFilterByJobCategoryId(@Param("jobCategoryId") Long jobCategoryId,
			@Param("filter") String filter, Pageable pageable);

	@Query(value = "select * from diagnosis where job_category_id = :jobCategoryId and is_deleted = false\n"
			+ "order by diagnosis_id limit 1", nativeQuery = true)
	Diagnosis findJobCategory(@Param("jobCategoryId") Long jobCategoryId);

	@Query(value = "SELECT * FROM diagnosis\n" + 
			"WHERE LOWER(diagnosis_desc) = LOWER(TRIM(:diagnosisDesc)) AND job_category_id = :jobCategoryId AND is_deleted = FALSE", nativeQuery = true)
	ArrayList<Diagnosis> checkData (@Param("diagnosisDesc") String diagnosisDesc, @Param("jobCategoryId") Long jobCategoryId);
	
	@Query(value = "SELECT * FROM diagnosis\n" + 
			"WHERE LOWER(diagnosis_desc) = LOWER(TRIM(:diagnosisDesc)) AND job_category_id = :jobCategoryId AND diagnosis_id NOT IN (:diagnosisId) AND is_deleted = FALSE", nativeQuery = true)
	ArrayList<Diagnosis> checkDuplicateData (@Param("diagnosisDesc") String diagnosisDesc, @Param("jobCategoryId") Long jobCategoryId,@Param("diagnosisId") Long diagnosisId);
	
	@Query(value = "SELECT * FROM diagnosis WHERE is_deleted = FALSE", nativeQuery = true)
	ArrayList<Diagnosis> findAllDiagnosis();
	
	@Query(value = Get_Data_Diagnosis,nativeQuery = true)
	ArrayList<Diagnosis> getDataDiagnosis(@Param("search")String search,Pageable pageable);
	
}
