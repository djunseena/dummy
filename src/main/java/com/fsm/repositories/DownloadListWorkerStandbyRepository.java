package com.fsm.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fsm.models.Users;

public interface DownloadListWorkerStandbyRepository extends JpaRepository<Users, Long>{

	public static final String Download_List_Worker_Standby = "SELECT u.user_id as \"Worker ID\", user_full_name as \"Worker Name\", user_email as \"Email\", mobile_phone as \"Mobile Phone\" \n" + 
			"FROM users as u \n" + 
			"						JOIN user_worker_status as ws on ws.user_id = u.user_id \n" + 
			"						LEFT JOIN \"dispatch\" as d on d.user_id = ws.user_id  \n" + 
			"						WHERE ws.status = 1 and DATE(ws.last_modified_on) = CURRENT_DATE AND ws.is_deleted = 'f' AND \n" + 
			"						(u.user_id NOT IN (SELECT s.user_id FROM ( SELECT d.user_id,h.order_id, MAX (h.created_on) AS \"created_on\"\n" + 
			"																FROM \"dispatch\" d\n" + 
			"																JOIN history h ON d.order_id = h.order_id\n" + 
			"																WHERE ((dispatch_date = CURRENT_DATE) and (h.dispatch_status = 'Finish' \n" + 
			"																				or h.dispatch_status = 'Canceled'or h.dispatch_status = 'Hold')) \n" + 
			"																GROUP BY d.user_id,h.order_id\n" + 
			"																ORDER BY d.user_id  DESC\n" + 
			"																) AS s) \n" + 
			"						OR\n" + 
			"						(u.user_id  IN (SELECT A.user_id\n" + 
			"														FROM (SELECT d.user_id, h.order_id,MAX (h.created_on) AS \"created_on\" \n" + 
			"																	FROM \"dispatch\" d \n" + 
			"																	JOIN history h ON d.order_id = h.order_id\n" + 
			"																	WHERE ((dispatch_date = CURRENT_DATE) and (h.dispatch_status = 'Finish' \n" + 
			"																	or h.dispatch_status = 'Canceled'or h.dispatch_status = 'Hold')) \n" + 
			"																	GROUP BY d.user_id,h.order_id \n" + 
			"																	ORDER BY d.user_id  DESC \n" + 
			"														) AS A)	 \n" + 
			"						OR \n" + 
			"						u.user_id IN (SELECT user_id FROM \"dispatch\" WHERE dispatch_date = CURRENT_DATE AND end_job IS NOT NULL))) \n" + 
			"						GROUP BY u.user_id";
	@Query(value = Download_List_Worker_Standby, nativeQuery = true)
	ArrayList<Users> valueTableData();
}
