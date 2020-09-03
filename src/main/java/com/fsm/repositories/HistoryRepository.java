package com.fsm.repositories;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fsm.models.History;

public interface HistoryRepository extends JpaRepository<History, Long> {

	@Query(value = "select * from history JOIN dispatch on history.order_id = dispatch.order_id\n"
			+ "where history.created_on in (select a.createdOn from (\n"
			+ "select order_id as orderId, max(created_on) as createdOn from history\n" + "GROUP BY order_id) as a)\n"
			+ "AND history.order_id in (select a.orderId from (\n"
			+ "select order_id as orderId, max(created_on) as createdOn from history\n" + "GROUP BY order_id) as a)\n"
			+ "AND dispatch.user_id = :userId", nativeQuery = true)
	public List<History> getOrderList(@Param("userId") long userId);

	@Query(value = "SELECT * FROM history WHERE order_id = :orderId ORDER BY history_id DESC LIMIT 1", nativeQuery = true)
	public History findByOrderIdFix(@Param("orderId") long orderId);

	@Query(value = "select * from history as a\n" + "			join dispatch as b ON a.order_id = b.order_id\n"
			+ "			join trouble_ticket as c ON b.ticket_id = c.ticket_id\n"
			+ "			join (select order_id,max(created_on) from history group by order_id) d on d.max = a.created_on\n"
			+ "			WHERE a.order_id = :order_id AND a.dispatch_status = 'Hold'", nativeQuery = true)
	public History getHoldReasonDispatch(@Param("order_id") Long orderId);

	@Query(value = "SELECT * FROM history as g join \n"
			+ "(select order_id,max(created_on) from history group by order_id) as h on h.max = g.created_on \n"
			+ "WHERE g.order_id = :orderId", nativeQuery = true)
	public History getStatusByOrderId(@Param("orderId") Long orderId);

	@Query(value = "SELECT * FROM history JOIN dispatch ON (history.order_id = dispatch.order_id) WHERE dispatch_status = :dispatch_status AND worker_id = :worker_id", nativeQuery = true)
	public List<History> findByFilter(@Param("dispatch_status") String dispatchStatus,
			@Param("worker_id") long workerId);

	@Query(value = "SELECT * FROM history as a \n" + "		join dispatch as b ON a.order_id = b.order_id\n"
			+ "		join trouble_ticket as c ON b.ticket_id = c.ticket_id\n"
			+ "		join client_company_branch as d ON c.branch_id = d.branch_id \n"
			+ "		join client_company as e ON d.company_id = e.company_id\n"
			+ "		join (select order_id,max(created_on) from history group by order_id) f on f.max = a.created_on \n"
			+ "		WHERE c.ticket_status_id=13 AND c.ticket_id = :ticket_id", nativeQuery = true)
	public History getHistoryCanceledByTicketId(@Param("ticket_id") Long ticketId);

	@Query(value = "SELECT * FROM history as a\n" + "		join dispatch as b ON a.order_id = b.order_id \n"
			+ "		join trouble_ticket as c ON b.ticket_id = c.ticket_id \n"
			+ "		join client_company_branch as d ON c.branch_id = d.branch_id\n"
			+ "		join client_company as e ON d.company_id = e.company_id\n"
			+ "		join dispatch_report as f ON b.order_id = f.order_id\n"
			+ "		join (select order_id,max(created_on) from history group by order_id) g on g.max = a.created_on \n"
			+ "		WHERE (a.dispatch_status='Reported' OR  a.dispatch_status='Finish') AND ((LOWER(c.ticket_code) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(c.ticket_title) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(e.company_name) LIKE LOWER(CONCAT('%', :search  , '%'))))", nativeQuery = true)
	public ArrayList<History> getAllFinishedOrder(@Param("search") String search, Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM history as a\n" + "		join dispatch as b ON a.order_id = b.order_id \n"
			+ "		join trouble_ticket as c ON b.ticket_id = c.ticket_id \n"
			+ "		join client_company_branch as d ON c.branch_id = d.branch_id\n"
			+ "		join client_company as e ON d.company_id = e.company_id\n"
			+ "		join dispatch_report as f ON b.order_id = f.order_id\n"
			+ "		join (select order_id,max(created_on) from history group by order_id) g on g.max = a.created_on \n"
			+ "		WHERE (a.dispatch_status='Reported' OR  a.dispatch_status='Finish') AND ((LOWER(c.ticket_code) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(c.ticket_title) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(e.company_name) LIKE LOWER(CONCAT('%', :search  , '%'))))", nativeQuery = true)
	public Integer getTotalFinishedOrder(@Param("search") String search);

	@Query(value = "SELECT * FROM history as a\n" + "		join dispatch as b ON a.order_id = b.order_id \n"
			+ "		join trouble_ticket as c ON b.ticket_id = c.ticket_id \n"
			+ "		join client_company_branch as d ON c.branch_id = d.branch_id\n"
			+ "		join client_company as e ON d.company_id = e.company_id\n"
			+ "		join dispatch_report as f ON b.order_id = f.order_id\n"
			+ "		join (select order_id,max(created_on) from history group by order_id) g on g.max = a.created_on \n"
			+ "		WHERE f.dispatch_report_rating = :dispatch_report_rating AND (a.dispatch_status='Reported' OR a.dispatch_status='Finish') AND\n"
			+ "		((LOWER(c.ticket_code) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(c.ticket_title) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(e.company_name) LIKE LOWER(CONCAT('%', :search  , '%'))))", nativeQuery = true)
	public ArrayList<History> getAllFinishedOrderFilter(@Param("search") String search,
			@Param("dispatch_report_rating") Integer dispatchReportRating, Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM history as a\n" + "		join dispatch as b ON a.order_id = b.order_id \n"
			+ "		join trouble_ticket as c ON b.ticket_id = c.ticket_id \n"
			+ "		join client_company_branch as d ON c.branch_id = d.branch_id\n"
			+ "		join client_company as e ON d.company_id = e.company_id\n"
			+ "		join dispatch_report as f ON b.order_id = f.order_id\n"
			+ "		join (select order_id,max(created_on) from history group by order_id) g on g.max = a.created_on \n"
			+ "		WHERE f.dispatch_report_rating = :dispatch_report_rating AND (a.dispatch_status='Reported' OR a.dispatch_status='Finish') AND\n"
			+ "		((LOWER(c.ticket_code) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(c.ticket_title) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(e.company_name) LIKE LOWER(CONCAT('%', :search  , '%'))))", nativeQuery = true)
	public Integer getTotalFinishedOrderFilter(@Param("search") String search,
			@Param("dispatch_report_rating") Integer dispatchReportRating);

	@Query(value = "SELECT * FROM history as a\n" + "		join dispatch as b ON a.order_id = b.order_id \n"
			+ "		join trouble_ticket as c ON b.ticket_id = c.ticket_id \n"
			+ "		join client_company_branch as d ON c.branch_id = d.branch_id\n"
			+ "		join client_company as e ON d.company_id = e.company_id\n"
			+ "		join (select order_id,max(created_on) from history group by order_id) g on g.max = a.created_on\n"
			+ "		WHERE a.dispatch_status='Canceled' \n"
			+ "		AND ((LOWER(c.ticket_code) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(a.reason) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(c.ticket_title) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(e.company_name) LIKE LOWER(CONCAT('%', :search  , '%'))))", nativeQuery = true)
	public ArrayList<History> getAllCanceledOrder(@Param("search") String search, Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM history as a\n" + "		join dispatch as b ON a.order_id = b.order_id \n"
			+ "		join trouble_ticket as c ON b.ticket_id = c.ticket_id \n"
			+ "		join client_company_branch as d ON c.branch_id = d.branch_id\n"
			+ "		join client_company as e ON d.company_id = e.company_id\n"
			+ "		join (select order_id,max(created_on) from history group by order_id) g on g.max = a.created_on\n"
			+ "		WHERE a.dispatch_status='Canceled' \n"
			+ "		AND ((LOWER(c.ticket_code) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(a.reason) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(c.ticket_title) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(e.company_name) LIKE LOWER(CONCAT('%', :search  , '%'))))", nativeQuery = true)
	public Integer getTotalCanceledOrder(@Param("search") String search);

	@Query(value = "select * from history as a\n" + "		join dispatch as b ON a.order_id = b.order_id\n"
			+ "		join trouble_ticket as c ON b.ticket_id = c.ticket_id\n"
			+ "		WHERE a.order_id = :order_id", nativeQuery = true)
	public ArrayList<History> getHistoryDispatch(@Param("order_id") Long orderId, Pageable pageable);

	@Query(value = "SELECT * FROM history as a\n" + "		join dispatch as b ON a.order_id = b.order_id \n"
			+ "		join trouble_ticket as c ON b.ticket_id = c.ticket_id\n"
			+ "		join client_company_branch as d ON c.branch_id = d.branch_id \n"
			+ "		join client_company as e ON d.company_id = e.company_id \n"
			+ "		join users as f ON b.user_id = f.user_id\n"
			+ "		left join dispatch_report as g ON b.order_id = g.order_id\n"
			+ "		left join dispatch_image_report as h ON g.dispatch_report_id = h.dispatch_report_id\n"
			+ "		join client_company_pic as i ON c.pic_id = i.pic_id\n"
			+ "		join (select order_id,max(created_on) from history group by order_id) j on j.max = a.created_on \n"
			+ "		WHERE b.order_id = :order_id AND (dispatch_status='Reported' OR dispatch_status='Finish') ", nativeQuery = true)
	public History getDetailFinishedOrder(@Param("order_id") Long orderId);

	@Query(value = "SELECT * FROM history as a\n" + "		join dispatch as b ON a.order_id = b.order_id \n"
			+ "		join trouble_ticket as c ON b.ticket_id = c.ticket_id\n"
			+ "		join client_company_branch as d ON c.branch_id = d.branch_id \n"
			+ "		join client_company as e ON d.company_id = e.company_id \n"
			+ "		join users as f ON b.user_id = f.user_id\n"
			+ "		join (select order_id,max(created_on) from history group by order_id) g on g.max = a.created_on \n"
			+ "		WHERE dispatch_status='Canceled' AND b.order_id = :order_id ", nativeQuery = true)
	public History getDetailCanceledOrder(@Param("order_id") Long orderId);

	@Modifying
	@Query(value = "insert into public.history\n"
			+ "			(created_by, created_on, dispatch_status, dispatch_action, order_id)\n"
			+ "			values ((select created_by FROM public.dispatch WHERE order_id = (select max(order_id) FROM public.dispatch)), CURRENT_TIMESTAMP, 'Confirmed', 39, (select max(order_id) FROM public.dispatch))", nativeQuery = true)
	@Transactional
	public void saveHistoryDispatch();

//	Query for looking at Riwayat List
	@Query(value = "SELECT h.* FROM history as h\r\n" + 
			"join dispatch as o on h.order_id = o.order_id\r\n" + 
			"join trouble_ticket as tt on tt.ticket_id = o.ticket_id\r\n" + 
			"where o.user_id = :userId AND (o.dispatch_date >= :startDate\r\n" + 
			"AND o.dispatch_date <=  :endDate)\r\n" + 
			"AND (tt.ticket_code ~* :keywords OR tt.ticket_title ~* :keywords)\r\n" + 
			"AND h.dispatch_status ~* :status\r\n" + 
			"AND h.created_on in (select a.createdOn from (\r\n" + 
			"select order_id as orderId, max(created_on) as createdOn from history GROUP BY order_id) as a)\r\n" + 
			"AND h.order_id in (select a.orderId from (\r\n" + 
			"select order_id as orderId, max(created_on) as createdOn from history GROUP BY order_id) as a)\r\n" + 
			"ORDER BY o.dispatch_date desc", nativeQuery = true)
	public ArrayList<History> getRiwayatByUserId(@Param("userId") Long userId, @Param("startDate") Date startDate,
			@Param("endDate") Date endDate, @Param("keywords") String keywords, @Param("status") String status);
}
