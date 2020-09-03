package com.fsm.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fsm.models.UserWorkerStatus;

@Repository
public interface UserWorkerStatusRepository extends JpaRepository<UserWorkerStatus, Long>{
	//query untuk pie chart technician avaibility
	public static final String Count_Technician_Avaibility = "select uSW.*\n" 
		+ "from user_worker_status as uSW\n" 
		+ "Join users as u on uSW.user_id = u.user_id \n" 
		+ "where ((uSW.status = 1 or uSW.status = 2) and (DATE(uSW.last_modified_on) = CURRENT_DATE))\n" 
		+ "and uSW.is_deleted = 'f'";
	//query untuk pie chart technician off
	public static final String Count_Technician_Off = "select uSW.* \n" 
			+ "from user_worker_status as uSW \n" 
			+ "Join users as u on uSW.user_id = u.user_id \n" 
			+ "where uSW.status = 3 and uSW.is_deleted = 'f'";
		
	public static final String Search_User_Worker_Status_ById = "SELECT ws.*\n" + 
			"FROM user_worker_status as ws\n" + 
			"JOIN users as u on ws.user_id = u.user_id\n" + 
			"where ws.user_id = :user_id";

	@Query(value = Search_User_Worker_Status_ById, nativeQuery = true)
	public UserWorkerStatus searchByUserId(@Param("user_id") Long userId);

	@Query(value = "SELECT * FROM user_worker_status WHERE user_id = :workerId", nativeQuery = true)
	public UserWorkerStatus findByWorkerId(@Param("workerId") long workerId);

	@Query(value = Count_Technician_Off, nativeQuery = true)
	public ArrayList<UserWorkerStatus> countDataTechnicianOff();

	@Query(value = Count_Technician_Avaibility, nativeQuery = true)
	public ArrayList<UserWorkerStatus> countDataTechnicianAvailable();
	
}
