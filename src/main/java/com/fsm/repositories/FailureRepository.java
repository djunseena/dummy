package com.fsm.repositories;

import java.util.ArrayList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fsm.models.Failure;

@Repository
public interface FailureRepository extends PagingAndSortingRepository<Failure, Long> {

	public static final String Get_Data_Failure = "SELECT f.* FROM failure f\n" 
			+ "JOIN diagnosis d on f.diagnosis_id = d.diagnosis_id\n" 
			+ "WHERE f.is_deleted = FALSE AND (CAST(failure_id as VARCHAR) LIKE concat('%',BTRIM(:search,' '),'%') OR\n" 
			+ "LOWER(f.failure_desc) LIKE LOWER(concat('%',BTRIM(:search,' '), '%')))";
	
	@Query(value = "SELECT * FROM failure WHERE diagnosis_id = :diagnosisId AND failure_desc ~* :filter", nativeQuery = true)
	public Slice<Failure> getFailureByDiagnosisId(@Param("diagnosisId") Long diagnosisId, Pageable pageable,
			String filter);

	@Query(value = "select * from failure where diagnosis_id = :diagnosisId and is_deleted = false\n"
			+ "order by failure_id limit 1", nativeQuery = true)
	Failure findDiagnosis(@Param("diagnosisId") Long diagnosisId);
	
	@Query(value = "SELECT * FROM failure\n" + 
			"WHERE LOWER(failure_desc) = LOWER(TRIM(:failureDesc)) AND diagnosis_id = :diagnosisId AND is_deleted = FALSE", nativeQuery = true)
	ArrayList<Failure> checkDataAlreadyExist (@Param("failureDesc") String failureDesc, @Param("diagnosisId")Long diagnosisId);
	
	@Query(value = "SELECT * FROM failure\n" + 
			"WHERE LOWER(failure_desc) = LOWER(TRIM(:failureDesc)) AND diagnosis_id = :diagnosisId AND failure_id NOT IN (:failureId) AND is_deleted = FALSE", nativeQuery = true)
	ArrayList<Failure> checkDuplicateData (@Param("failureDesc") String failureDesc, @Param("diagnosisId")Long diagnosisId, @Param("failureId") Long failureId);
	
	@Query(value = "SELECT * FROM failure WHERE is_deleted = FALSE", nativeQuery = true)
	ArrayList<Failure> findAllFailure ();
	
	@Query(value = Get_Data_Failure, nativeQuery = true)
	ArrayList<Failure> getDataFailure(@Param("search")String search, Pageable pageable);
}
