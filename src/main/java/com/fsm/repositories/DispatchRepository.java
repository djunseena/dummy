package com.fsm.repositories;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fsm.models.Dispatch;

@Repository
public interface DispatchRepository extends JpaRepository<Dispatch, Long> {

	@Query(value = "SELECT * FROM dispatch JOIN history ON (dispatch.order_id = history.order_id) WHERE dispatch_status = :dispatch_status AND worker_id = :worker_id", nativeQuery = true)
	public List<Dispatch> getDispatchByFilter(@Param("dispatch_status") String dispatchStatus,
			@Param("worker_id") long workerId);

	@Modifying
	@Query(value = "insert into public.dispatch\n "
			+ "(created_by, dispatch_date, dispatch_desc, dispatch_time, last_modified_by, ticket_id, user_id, created_on, last_modified_on) \n "
			+ "values (:created_by, :dispatch_date, :dispatch_desc, :dispatch_time, :last_modified_by, :ticket_id, :user_id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); \n"
			+ "UPDATE trouble_ticket set ticket_status_id = 9 WHERE ticket_id = :ticket_id", nativeQuery = true)
	@Transactional
	public void saveDispatch(@Param("created_by") Long createdBy, @Param("dispatch_date") Date dispatchDate,
			@Param("dispatch_desc") String dispatchDesc, @Param("dispatch_time") Time dispatchTime,
			@Param("last_modified_by") Long lastModifiedBy, @Param("ticket_id") Long ticketId,
			@Param("user_id") Long userId);

	@Query(value = "select * from dispatch\n" + "JOIN trouble_ticket on dispatch.ticket_id = trouble_ticket.ticket_id\n"
			+ "where dispatch.user_id = :userId and trouble_ticket.ticket_status_id = 9", nativeQuery = true)
	public List<Dispatch> getOrderListDispatch(@Param("userId") long userId);

	@Query(value = "SELECT * FROM dispatch as a\n" + "	RIGHT JOIN trouble_ticket as b ON a.ticket_id = b.ticket_id\n"
			+ "	LEFT JOIN users as c ON a.user_id = c.user_id\n" + "WHERE b.ticket_id = :ticketId", nativeQuery = true)
	public Dispatch findByTicketId(@Param("ticketId") Long ticketId);

	@Query(value = "SELECT * FROM dispatch as a\n" + "	RIGHT JOIN trouble_ticket as b ON a.ticket_id = b.ticket_id\n"
			+ "	LEFT JOIN users as c ON a.user_id = c.user_id\n"
			+ "	LEFT JOIN dispatch_report as d ON a.order_id = d.order_id\n"
			+ "WHERE b.ticket_id = :ticketId", nativeQuery = true)
	public Dispatch findReportByTicketId(@Param("ticketId") Long ticketId);

	@Query(value = "SELECT * FROM dispatch as a	\n" + "		join trouble_ticket as b on a.ticket_id = b.ticket_id \n"
			+ "		join client_company_branch as c on b.branch_id = c.branch_id\n"
			+ "		join client_company as d on c.company_id = d.company_id \n"
			+ "		join users as e on a.user_id = e.user_id\n" 
			+ "		join sla as f on b.sla_id = f.sla_id  \n"
			+ "		join history as g on a.order_id = g.order_id\n"
			+ "		join (select order_id,max(created_on) from history\n"
			+ "		group by order_id) h on h.max = g.created_on\n"
			+ "		WHERE LOWER(b.ticket_code) LIKE LOWER(CONCAT('%', :search  , '%')) OR \n"
			+ "		LOWER(b.ticket_title) LIKE LOWER(CONCAT('%', :search   , '%')) OR\n"
			+ "		LOWER(e.user_full_name) LIKE LOWER(CONCAT('%', :search   , '%')) OR\n"
			+ "		LOWER(d.company_name) LIKE LOWER(CONCAT('%', :search   , '%')) OR \n"
			+ "     CAST(a.start_job AS VARCHAR) LIKE concat('%', :search ,'%') OR \n"
			+ "     CONCAT(CAST(f.sla_resolution_time AS VARCHAR),' Hours') LIKE concat('%', :search ,'%')", nativeQuery = true)
	public ArrayList<Dispatch> getListDispatch(@Param("search") String search, Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM dispatch as a	\n"
			+ "		join trouble_ticket as b on a.ticket_id = b.ticket_id \n"
			+ "		join client_company_branch as c on b.branch_id = c.branch_id\n"
			+ "		join client_company as d on c.company_id = d.company_id \n"
			+ "		join users as e on a.user_id = e.user_id\n" 
			+ "		join sla as f on b.sla_id = f.sla_id  \n"
			+ "		join history as g on a.order_id = g.order_id\n"
			+ "		join (select order_id,max(created_on) from history\n"
			+ "		group by order_id) h on h.max = g.created_on\n"
			+ "		WHERE LOWER(b.ticket_code) LIKE LOWER(CONCAT('%', :search  , '%')) OR \n"
			+ "		LOWER(b.ticket_title) LIKE LOWER(CONCAT('%', :search   , '%')) OR\n"
			+ "		LOWER(e.user_full_name) LIKE LOWER(CONCAT('%', :search   , '%')) OR\n"
			+ "		LOWER(d.company_name) LIKE LOWER(CONCAT('%', :search   , '%')) OR \n"
			+ "     CAST(a.start_job AS VARCHAR) LIKE concat('%', :search ,'%') OR \n"
			+ "     CONCAT(CAST(f.sla_resolution_time AS VARCHAR),' Hours') LIKE concat('%', :search ,'%')", nativeQuery = true)
	public Integer getTotalListDispatch(@Param("search") String search);

	@Query(value = "select * from dispatch a join history b ON a.order_id = b.order_id \n"
			+ "	  join (select order_id,max(created_on) from history group by order_id) c on c.max = b.created_on\n"
			+ "     WHERE b.dispatch_status = :dispatch_status AND (SUBSTR(to_char(c.max,'yyyy-MM-dd'), 6,2) = :month AND SUBSTR(to_char(c.max,'yyyy-MM-dd'), 1,4) = :year)", nativeQuery = true)
	public ArrayList<Dispatch> getChartDispatch(@Param("dispatch_status") String dispatchStatus,
			@Param("month") String month, @Param("year") String year);

	@Query(value = "SELECT * FROM dispatch as a\n" + "		join trouble_ticket as b on a.ticket_id = b.ticket_id \n"
			+ "		join client_company_branch as c on b.branch_id = c.branch_id\n"
			+ "		join client_company as d on c.company_id = d.company_id \n"
			+ "		join users as e on a.user_id = e.user_id\n" 
			+ "		join sla as f on b.sla_id = f.sla_id  \n"
			+ "		join history as g on a.order_id = g.order_id\n"
			+ "		join (select order_id,max(created_on) from history\n"
			+ "		group by order_id) h on h.max = g.created_on\n"
			+ "     where g.dispatch_status = :dispatch_status \n"
			+ "		AND (LOWER(b.ticket_code) LIKE LOWER(CONCAT('%', :search  , '%')) OR \n"
			+ "		LOWER(b.ticket_title) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(e.user_full_name) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(d.company_name) LIKE LOWER(CONCAT('%', :search  , '%')) OR \n"
			+ "     CAST(a.start_job AS VARCHAR) LIKE concat('%', :search ,'%') OR \n"
			+ "     CONCAT(CAST(f.sla_resolution_time AS VARCHAR),' Hours') LIKE concat('%', :search ,'%'))", nativeQuery = true)
	public ArrayList<Dispatch> getListDispatchFilter(@Param("search") String search,
			@Param("dispatch_status") String filter, Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM dispatch as a\n"
			+ "		join trouble_ticket as b on a.ticket_id = b.ticket_id \n"
			+ "		join client_company_branch as c on b.branch_id = c.branch_id\n"
			+ "		join client_company as d on c.company_id = d.company_id \n"
			+ "		join users as e on a.user_id = e.user_id\n" 
			+ "		join sla as f on b.sla_id = f.sla_id  \n"
			+ "		join history as g on a.order_id = g.order_id\n"
			+ "		join (select order_id,max(created_on) from history\n"
			+ "		group by order_id) h on h.max = g.created_on\n"
			+ "     where g.dispatch_status = :dispatch_status \n"
			+ "		AND (LOWER(b.ticket_code) LIKE LOWER(CONCAT('%', :search  , '%')) OR \n"
			+ "		LOWER(b.ticket_title) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(e.user_full_name) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(d.company_name) LIKE LOWER(CONCAT('%', :search  , '%')) OR \n"
			+ "     CAST(a.start_job AS VARCHAR) LIKE concat('%', :search ,'%') OR \n"
			+ "     CONCAT(CAST(f.sla_resolution_time AS VARCHAR),' Hours') LIKE concat('%', :search ,'%'))", nativeQuery = true)
	public Integer getTotalListDispatchFilter(@Param("search") String search, @Param("dispatch_status") String filter);

//	Query for Looking Schedule for Mobile
	@Query(value = "SELECT d.* FROM dispatch d JOIN trouble_ticket tt ON d.ticket_id = tt.ticket_id WHERE tt.ticket_status_id = 9 AND dispatch_date = :dispatchDate ORDER BY d.dispatch_time ASC", nativeQuery = true)
	public Slice<Dispatch> getQuerySchedule(@Param("dispatchDate") Date dispatchDate, Pageable pageable);

//	Query to get All Order By user
	@Query(value = "select * from dispatch where user_id = :userId", nativeQuery = true)
	public ArrayList<Dispatch> getListDispatchByUserId(@Param("userId") Long userId);

	@Query(value = "SELECT date_part('hour', AVG(end_job - start_job))+ \n"
			+ "(date_part('minute', AVG(end_job - start_job))/100) FROM dispatch a \n"
			+ "JOIN trouble_ticket b ON a.ticket_id = b.ticket_id \n"
			+ "WHERE b.category_id = :categoryId AND a.last_modified_on >= :date AND (end_job IS NOT NULL AND start_job IS NOT NULL)", nativeQuery = true)
	BigDecimal averageSolvingTime(@Param("categoryId") Long categoryId, @Param("date") Date date);

	@Query(value = "SELECT date_part('hour', AVG(end_job - start_job))+ \n"
			+ "(date_part('minute', AVG(end_job - start_job))/100) FROM dispatch a \n"
			+ "JOIN trouble_ticket b ON a.ticket_id = b.ticket_id \n"
			+ "WHERE b.category_id = :categoryId AND (a.last_modified_on >= :start_date and a.last_modified_on <= :end_date) AND \n"
			+ "(end_job IS NOT NULL AND start_job IS NOT NULL)", nativeQuery = true)
	BigDecimal averageSolvingTimeCustom(@Param("categoryId") Long categoryId,
			@Param("start_date") Date startDate, @Param("end_date") Date endDate);

	@Query(value = "SELECT date_part('minute', AVG(c.created_on - a.created_on))+ \n"
			+ "(date_part('second', AVG(c.created_on - a.created_on))/100) FROM dispatch a \n" 
			+ "JOIN trouble_ticket b ON a.ticket_id = b.ticket_id \n"
			+ "JOIN history c ON a.order_id = c.order_id \n"
			+ "WHERE b.priority_id = :priorityId AND a.last_modified_on >= :date AND c.dispatch_status = 'Start'", nativeQuery = true)
	BigDecimal averageResponseTime(@Param("priorityId") Long priorityId, @Param("date") Date date);

	@Query(value = "SELECT date_part('minute', AVG(c.created_on - a.created_on))+ \n"
			+ "(date_part('second', AVG(c.created_on - a.created_on))/100) FROM dispatch a \n" 
			+ "JOIN trouble_ticket b ON a.ticket_id = b.ticket_id \n"
			+ "JOIN history c ON a.order_id = c.order_id \n"
			+ "WHERE b.priority_id = :priorityId AND (a.last_modified_on >= :start_date and a.last_modified_on <= :end_date) \n"
			+ "AND c.dispatch_status = 'Start'", nativeQuery = true)
	BigDecimal averageResponseTimeCustom(@Param("priorityId") Long priorityId,
			@Param("start_date") Date startDate, @Param("end_date") Date endDate);
}
