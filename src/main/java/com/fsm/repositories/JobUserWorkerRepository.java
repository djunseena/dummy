package com.fsm.repositories;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fsm.models.JobUserWorker;

@Repository
public interface JobUserWorkerRepository extends JpaRepository<JobUserWorker, Long> {

	@Query(value = "SELECT * FROM job_user_worker WHERE user_id = :userId AND is_deleted = false", nativeQuery = true)
	ArrayList<JobUserWorker> getJobUserWorkerByUserId(@Param("userId") Long userId);

	@Query(value = "SELECT * FROM job_user_worker WHERE user_id = :userId AND is_deleted = false ORDER BY job_user_worker_id ASC\n"
			+ "LIMIT 1", nativeQuery = true)
	JobUserWorker getObjectJobUserWorkerByUserId(@Param("userId") Long userId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE job_user_worker SET job_id = :job_id WHERE job_user_worker_id = :job_user_worker_id ", nativeQuery = true)
	void updateJob(@Param("job_id") Long jobId, @Param("job_user_worker_id") Long jobUserWorkerId);

	@Modifying
	@Transactional
	@Query(value = "DELETE from job_user_worker WHERE user_id = :user_id ", nativeQuery = true)
	void deleteJob(@Param("user_id") Long userId);

	@Query(value = "select * from job_user_worker where job_id = :jobId and is_deleted = false\n"
			+ "order by job_user_worker_id LIMIT 1", nativeQuery = true)
	JobUserWorker findUserWorkersStatusbyJobId(@Param("jobId") Long jobId);
}