package com.fsm.repositories;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fsm.models.Users;

public interface UsersRepository extends JpaRepository<Users, Long> {

	public static final String FIND_ListWorkerForDispatchByPrimaryArea = "select u.* from users u   \n"
			+ "	 	JOIN user_worker_status uws on u.user_id = uws.user_id  \n"
			+ "		JOIN job_user_worker juw on u.user_id = juw.user_id  \n"
			+ "	 	where u.user_id not in (SELECT user_id FROM\n"
			+ "												(SELECT \n"
			+ "											    d.user_id, MAX (h.created_on) AS created_on \n"
			+ "												FROM dispatch as d \n"
			+ "												join trouble_ticket as t on d.ticket_id = t.ticket_id\n"
			+ "												join sla as s on t.sla_id = s.sla_id\n"
			+ "												JOIN history h ON d.order_id = h.order_id \n"
			+ "												join (select order_id,max(created_on) from history \n"
			+ "												group by order_id) y on y.max = h.created_on \n"
			+ "												WHERE \n"
			+ "													(CASE \n"
			+ "														WHEN ( :dispatchTimestamp BETWEEN \n"
			+ "															TO_TIMESTAMP(CONCAT(d.dispatch_date,' ',d.dispatch_time),'YYYY-MM-DD HH24:MI:SS') \n"
			+ "															AND TO_TIMESTAMP(CONCAT(d.dispatch_date,' ',d.dispatch_time),'YYYY-MM-DD HH24:MI:SS')+s.sla_resolution_time * interval '1 hour' + s.sla_response_time * interval '1 minute') AND (h.dispatch_status = 'Confirmed' or h.dispatch_status = 'Start' )\n"
			+ "															THEN (user_id)\n"
			+ "															END) = user_id\n"
			+ "															GROUP BY d.user_id \n"
			+ "															ORDER BY d.user_id  DESC ) AS S) \n"
			+ "		and u.role_id = 8  \n"
			+ "		and u.user_id IN (select user_id from job_user_worker where (job_id = :job_id and is_deleted = 'f'))\n"
			+ "		and (uws.status = 1 and DATE(uws.last_modified_on) = CURRENT_DATE and uws.is_deleted = 'f')  \n"
			+ "		and u.primary_area_id =  (select bb.city_id from trouble_ticket as aa  \n"
			+ "		join client_company_branch as bb on aa.branch_id = bb.branch_id  \n"
			+ "		where aa.ticket_id = :ticket_id)  \n" + "		GROUP BY u.user_id";

	public static final String FIND_ListWorkerForDispatchByBoth = "select u.* from users u   \r\n" + 
			"	 	JOIN user_worker_status uws on u.user_id = uws.user_id  \r\n" + 
			"		JOIN job_user_worker juw on u.user_id = juw.user_id  \r\n" + 
			"		where u.user_id not in (SELECT user_id FROM\r\n" + 
			"											(SELECT \r\n" + 
			"										    d.user_id, MAX (h.created_on) AS created_on \r\n" + 
			"											FROM dispatch as d \r\n" + 
			"											join trouble_ticket as t on d.ticket_id = t.ticket_id\r\n" + 
			"											join sla as s on t.sla_id = s.sla_id\r\n" + 
			"											JOIN history h ON d.order_id = h.order_id \r\n" + 
			"											join (select order_id,max(created_on) from history \r\n" + 
			"											group by order_id) y on y.max = h.created_on \r\n" + 
			"											WHERE \r\n" + 
			"												(CASE \r\n" + 
			"													WHEN ( :dispatchTimestamp BETWEEN \r\n" + 
			"														TO_TIMESTAMP(CONCAT(d.dispatch_date,' ',d.dispatch_time),'YYYY-MM-DD HH24:MI:SS') \r\n" + 
			"														AND TO_TIMESTAMP(CONCAT(d.dispatch_date,' ',d.dispatch_time),'YYYY-MM-DD HH24:MI:SS')+s.sla_resolution_time * interval '1 hour' + s.sla_response_time * interval '1 minute') AND (h.dispatch_status = 'Confirmed' or h.dispatch_status = 'Start' )\r\n" + 
			"														THEN (user_id)\r\n" + 
			"														END) = user_id\r\n" + 
			"														GROUP BY d.user_id \r\n" + 
			"														ORDER BY d.user_id  DESC ) AS S) \r\n" + 
			"		and u.role_id = 8  \r\n" + 
			"		and u.user_id IN (select user_id from job_user_worker where (job_id = :job_id and is_deleted = 'f'))\r\n" + 
			"		and (uws.status = 1 and DATE(uws.last_modified_on) = CURRENT_DATE and uws.is_deleted = 'f')  \r\n" + 
			"		and (u.primary_area_id =  (select bb.city_id from trouble_ticket as aa\r\n" + 
			"		join client_company_branch as bb on aa.branch_id = bb.branch_id\r\n" + 
			"		where aa.ticket_id = :ticket_id) OR u.secondary_area_id =  (select bb.city_id from trouble_ticket as aa\r\n" + 
			"		join client_company_branch as bb on aa.branch_id = bb.branch_id\r\n" + 
			"		where aa.ticket_id = :ticket_id)) GROUP BY u.user_id";


	// Query for technician assigment on monitoring
	public static final String Find_Technician_Assignment = "SELECT u.*\n" + "FROM \"dispatch\" as d\n"
			+ "JOIN users u on u.user_id = d.user_id\n"
			+ "JOIN user_worker_status as ws on u.user_id = ws.user_id\n"
			+ "WHERE ((d.dispatch_date = CURRENT_DATE AND start_job IS NULL) and (ws.status = 1  and DATE(ws.last_modified_on) = CURRENT_DATE and ws.is_deleted = 'f')) ";

	// Query for technician on duty
	public static final String Find_Technician_OnDuty = "SELECT u.*\n" + "FROM \"dispatch\" as d\n"
			+ "JOIN users u on u.user_id = d.user_id\n"
			+ "JOIN user_worker_status as ws on u.user_id = ws.user_id\n"
			+ "WHERE ((d.dispatch_date = CURRENT_DATE AND start_job IS NOT NULL AND end_job IS NULL) and (ws.status = 2  and DATE(ws.last_modified_on) = CURRENT_DATE and ws.is_deleted = 'f'))";
	// query for technician stand by on monitoring

	public static final String Find_Technician_Standby = "SELECT u.* FROM users as u \n" + 
			"			JOIN user_worker_status as ws on ws.user_id = u.user_id \n" + 
			"			LEFT JOIN \"dispatch\" as d on d.user_id = ws.user_id \n" + 
			"			WHERE ws.status = 1 and DATE(ws.last_modified_on) = CURRENT_DATE AND ws.is_deleted = 'f' AND\n" + 
			"			(u.user_id NOT IN (SELECT s.user_id FROM ( SELECT d.user_id,h.order_id, MAX (h.created_on) AS \"created_on\"\n" + 
			"													FROM \"dispatch\" d \n" + 
			"													JOIN history h ON d.order_id = h.order_id\n" + 
			"													WHERE ((dispatch_date = CURRENT_DATE) and (h.dispatch_status = 'Finish' \n" + 
			"																	or h.dispatch_status = 'Canceled'or h.dispatch_status = 'Hold'))\n" + 
			"													GROUP BY d.user_id,h.order_id\n" + 
			"													ORDER BY d.user_id  DESC\n" + 
			"													) AS s)	\n" + 
			"			OR\n" + 
			"			(u.user_id  IN (SELECT A.user_id\n" + 
			"											FROM (SELECT d.user_id, h.order_id,MAX (h.created_on) AS \"created_on\"\n" + 
			"														FROM \"dispatch\" d \n" + 
			"														JOIN history h ON d.order_id = h.order_id\n" + 
			"														WHERE ((dispatch_date = CURRENT_DATE) and (h.dispatch_status = 'Finish' \n" + 
			"														or h.dispatch_status = 'Canceled'or h.dispatch_status = 'Hold'))\n" + 
			"														GROUP BY d.user_id,h.order_id\n" + 
			"														ORDER BY d.user_id  DESC\n" + 
			"											) AS A)	\n" + 
			"			OR\n" + 
			"			u.user_id IN (SELECT user_id FROM \"dispatch\" WHERE dispatch_date = CURRENT_DATE AND end_job IS NOT NULL)))\n" + 
			"			GROUP BY u.user_id";
	//query for search technician standby on monitoring
	public static final String Search_Technician_Standby_List = "SELECT u.* FROM users as u \n" + 
			"			JOIN user_worker_status as ws on ws.user_id = u.user_id \n" + 
			"			LEFT JOIN \"dispatch\" as d on d.user_id = ws.user_id \n" + 
			"			WHERE ws.status = 1 and DATE(ws.last_modified_on) = CURRENT_DATE AND ws.is_deleted = 'f' AND\n" + 
			"			(u.user_id NOT IN (SELECT s.user_id FROM ( SELECT d.user_id,h.order_id, MAX (h.created_on) AS \"created_on\"\n" + 
			"													FROM \"dispatch\" d \n" + 
			"													JOIN history h ON d.order_id = h.order_id\n" + 
			"													WHERE ((dispatch_date = CURRENT_DATE) and (h.dispatch_status = 'Finish' \n" + 
			"																	or h.dispatch_status = 'Canceled'or h.dispatch_status = 'Hold'))\n" + 
			"													GROUP BY d.user_id,h.order_id\n" + 
			"													ORDER BY d.user_id  DESC\n" + 
			"													) AS s)	\n" + 
			"			OR\n" + 
			"			(u.user_id  IN (SELECT A.user_id\n" + 
			"											FROM (SELECT d.user_id, h.order_id,MAX (h.created_on) AS \"created_on\"\n" + 
			"														FROM \"dispatch\" d \n" + 
			"														JOIN history h ON d.order_id = h.order_id\n" + 
			"														WHERE ((dispatch_date = CURRENT_DATE) and (h.dispatch_status = 'Finish' \n" + 
			"														or h.dispatch_status = 'Canceled'or h.dispatch_status = 'Hold'))\n" + 
			"														GROUP BY d.user_id,h.order_id\n" + 
			"														ORDER BY d.user_id  DESC\n" + 
			"											) AS A)	\n" + 
			"			OR\n" + 
			"			u.user_id IN (SELECT user_id FROM \"dispatch\" WHERE dispatch_date = CURRENT_DATE AND end_job IS NOT NULL))) AND\n" + 
			"			(LOWER(u.user_full_name) LIKE LOWER (concat('%' , :search ,'%')) OR\n" + 
			"			LOWER(u.user_email) LIKE LOWER(concat('%', :search ,'%')) OR LOWER(u.mobile_phone) LIKE LOWER(concat('%', :search ,'%')))\n" + 
			"			GROUP BY u.user_id";

	@Query(value = "SELECT * FROM users WHERE user_email = :user or user_name = :user", nativeQuery = true)
	Users findUsersByUser(@Param("user") String user);

//	Query untuk mendapatkan id user terbaru
	@Query(value = "SELECT * FROM users ORDER BY user_id DESC Limit 1", nativeQuery = true)
	public Users getNewUsers();

	@Query(value = "SELECT * FROM users WHERE role_id = 8 AND is_deleted = false", nativeQuery = true)
	public ArrayList<Users> findAllTechnician(Pageable pageable);

	@Query(value = "SELECT * FROM users WHERE role_id = 8 AND is_deleted = false AND \n"
	+ "		(CAST(user_id AS VARCHAR) LIKE CONCAT('%', :search ,'%') OR \n"
	+ "		LOWER(user_full_name) LIKE LOWER(CONCAT('%', :search ,'%')) OR \n"
	+ "		LOWER(user_email) LIKE LOWER(CONCAT('%', :search ,'%')))", nativeQuery = true)
	public ArrayList<Users> getListWorker(@Param("search") String search, Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM users WHERE role_id = 8 AND is_deleted = false AND \n"
	+ "		(CAST(user_id AS VARCHAR) LIKE CONCAT('%', :search ,'%') OR \n"
	+ "		LOWER(user_full_name) LIKE LOWER(CONCAT('%', :search ,'%')) OR \n"
	+ "		LOWER(user_email) LIKE LOWER(CONCAT('%', :search ,'%')))", nativeQuery = true)
	Integer getTotalListWorker(@Param("search") String search);

	@Query(value = "SELECT * FROM users WHERE role_id = 2 AND is_deleted = false AND \n"
	+ "		(CAST(user_id AS VARCHAR) LIKE CONCAT('%', :search ,'%') OR \n"
	+ "		LOWER(user_full_name) LIKE LOWER(CONCAT('%', :search ,'%')) OR \n"
	+ "		LOWER(user_email) LIKE LOWER(CONCAT('%', :search ,'%')))", nativeQuery = true)
	public ArrayList<Users> getListUsers(@Param("search") String search, Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM users WHERE role_id = 2 AND is_deleted = false AND \n"
	+ "		(CAST(user_id AS VARCHAR) LIKE CONCAT('%', :search ,'%') OR \n"
	+ "		LOWER(user_full_name) LIKE LOWER(CONCAT('%', :search ,'%')) OR \n"
	+ "		LOWER(user_email) LIKE LOWER(CONCAT('%', :search ,'%')))", nativeQuery = true)
	Integer getTotalListUsers(@Param("search") String search);

	// Query untuk meng-get detail account / worker
	@Query(value = "SELECT * FROM users", nativeQuery = true)
	public List<Users> findAllData();

	// Query untuk mencari worker dengan id terbaru
	@Query(value = "SELECT * FROM users ORDER BY user_id DESC LIMIT 1", nativeQuery = true)
	public Users findNeWorker();

	@Query(value = FIND_ListWorkerForDispatchByPrimaryArea, nativeQuery = true)
	public ArrayList<Users> getAllWorkerForDispatchPrimary(@Param("dispatchTimestamp") Timestamp dispatchTimestamp,
			@Param("ticket_id") Long ticketId, @Param("job_id") Long jobId);

	@Query(value = FIND_ListWorkerForDispatchByBoth, nativeQuery = true)
	public ArrayList<Users> getAllWorkerForDispatchByBoth(@Param("dispatchTimestamp") Timestamp dispatchTimestamp,
			@Param("ticket_id") Long ticketId, @Param("job_id") Long jobId);

	@Query(value = Find_Technician_Assignment, nativeQuery = true)
	public ArrayList<Users> findTechnicianAssignment();

	@Query(value = Find_Technician_OnDuty, nativeQuery = true)
	public ArrayList<Users> findTechnicianOnDuty();

	@Query(value = Find_Technician_Standby, nativeQuery = true)
	public ArrayList<Users> findTechnicianStandby();
	
	@Query(value = Search_Technician_Standby_List,nativeQuery = true)
	public ArrayList<Users> findTechnicianStandbyPaging (Pageable pageable, @Param("search") String search);

	@Modifying
	@Query(value = "insert into public.users "
			+ "(user_group_id, user_identity_no, user_name, primary_area_id, user_email, user_name, user_password, created_on, last_modified_on) "
			+ "values (:workerGroupId, :workerIdentityNo, :workerName, :primaryAreaId, :workerEmail, :workerUsername, :workerPassword, NOW(), NOW())", nativeQuery = true)
	@Transactional
	public void saveWorker(@Param("workerGroupId") Long workerGroupId,
			@Param("workerIdentityNo") String workerIdentityNo, @Param("workerName") String workerName,
			@Param("primaryAreaId") Long primaryAreaId, @Param("workerEmail") String workerEmail,
			@Param("workerUsername") String workerUsername, @Param("workerPassword") String workerPassword);

//	Query untuk save user baru
	@Modifying
	@Query(value = "INSERT INTO users (primary_area_id, secondary_area_id, role_id, user_name, user_password, user_address, user_address_detail, phone, mobile_phone, user_email, user_identity, user_identity_no, user_gender, user_image, created_by, created_on, last_modified_by, last_modified_on, is_deleted , user_latitude, user_longatitude, user_full_name) VALUES (:primaryAreaId, :secondaryAreaId, :roleId, :userName, :userPassword, :userAddress, :userAddressDetail, :phone, :mobilePhone, :userEmail, :userIdentity, :userIdentityNo, :userGender, null, :createdBy, NOW(), :lastModifiedBy, NOW(), 'false', null, null, :userFullName)", nativeQuery = true)
	@Transactional
	public void savingUser(@Param("primaryAreaId") long primaryAreaId, @Param("secondaryAreaId") long secondaryAreaId,
			@Param("roleId") long roleId, @Param("userName") String userName,
			@Param("userPassword") String userPassword, @Param("userAddress") String userAddress,
			@Param("userAddressDetail") String userAddressDetail, @Param("phone") String phone,
			@Param("mobilePhone") String mobilePhone, @Param("userEmail") String userEmail,
			@Param("userIdentity") int userIdentity, @Param("userIdentityNo") String userIdentityNo,
			@Param("userGender") int userGender, @Param("createdBy") long createdBy,
			@Param("lastModifiedBy") long lastModifiedBy, @Param("userFullName") String userFullName);

	@Query(value = "SELECT MAX(user_id) FROM users", nativeQuery = true)
	public long getLatestUserId();

	@Query(value = "SELECT * FROM users WHERE user_id = :userId", nativeQuery = true)
	public Users getUsersByUserId(@Param("userId") Long userId);

	@Query(value = "SELECT * FROM users WHERE user_id = :userId AND role_id = 8 AND is_deleted = false", nativeQuery = true)
	public Users getWorkerByUserId(@Param("userId") Long userId);
	
	@Query(value = "SELECT * FROM users WHERE user_id = :userId AND role_id = 2 AND is_deleted = false", nativeQuery = true)
	public Users getAdminByUserId(@Param("userId") Long userId);

	@Query(value = "SELECT * FROM users WHERE role_id = :roleId limit 1", nativeQuery = true)
	public Users findByRoleId(@Param("roleId") Long roleId);

	@Query(value = "SELECT * FROM users WHERE LOWER(user_name) = BTRIM(LOWER(:userName),' ') AND is_deleted = false LIMIT 1", nativeQuery = true)
	Users checkDupUsersName(@Param("userName") String userName);	

	@Query(value = "SELECT * FROM users WHERE LOWER(user_email) = BTRIM(LOWER(:userEmail),' ') AND is_deleted = false LIMIT 1", nativeQuery = true)
	Users checkDupUsersEmail(@Param("userEmail") String userEmail);
}
