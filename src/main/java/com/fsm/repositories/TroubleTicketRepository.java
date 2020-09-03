package com.fsm.repositories;

import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.fsm.models.TroubleTicket;

public interface TroubleTicketRepository extends JpaRepository<TroubleTicket, Long> {

	public static final String FIND_ReportDataToday = "select count(ticket_id) as total,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 8 and ticket_date = current_date) as open,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 10 and ticket_date = current_date) as inprogress,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 11 and ticket_date = current_date) as hold,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 30 and ticket_date = current_date) as finish_reported,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 22 and ticket_date = current_date) as urgent,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 21 and ticket_date = current_date) as high,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 20 and ticket_date = current_date) as medium,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 19 and ticket_date = current_date) as low,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 31 and ticket_date = current_date) as request,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 32 and ticket_date = current_date) as task,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 33 and ticket_date = current_date) as incident from trouble_ticket WHERE ticket_date = current_date;";

	public static final String FIND_ReportDataThisWeek = "select count(ticket_id) as total,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 8 and ticket_date >= current_date + interval '1 week') as open,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 10 and ticket_date >= current_date + interval '1 week') as inprogress,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 11 and ticket_date >= current_date + interval '1 week') as hold,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 30 and ticket_date >= current_date + interval '1 week') as finish_reported,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 22 and ticket_date >= current_date + interval '1 week') as urgent,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 21 and ticket_date >= current_date + interval '1 week') as high,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 20 and ticket_date >= current_date + interval '1 week') as medium,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 19 and ticket_date >= current_date + interval '1 week') as low,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 31 and ticket_date >= current_date + interval '1 week') as request,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 32 and ticket_date >= current_date + interval '1 week') as task,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 33 and ticket_date >= current_date + interval '1 week') as incident from trouble_ticket WHERE ticket_date >= current_date + interval '1 week';";

	public static final String FIND_ReportDataThisMonth = "select count(ticket_id) as total,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 8 and ticket_date >= current_date + interval '1 month') as open,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 10 and ticket_date >= current_date + interval '1 month') as inprogress,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 11 and ticket_date >= current_date + interval '1 month') as hold,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 30 and ticket_date >= current_date + interval '1 month') as finish_reported,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 22 and ticket_date >= current_date + interval '1 month') as urgent,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 21 and ticket_date >= current_date + interval '1 month') as high,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 20 and ticket_date >= current_date + interval '1 month') as medium,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 19 and ticket_date >= current_date + interval '1 month') as low,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 31 and ticket_date >= current_date + interval '1 month') as request,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 32 and ticket_date >= current_date + interval '1 month') as task,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 33 and ticket_date >= current_date + interval '1 month') as incident from trouble_ticket WHERE ticket_date >= current_date + interval '1 month';";

	public static final String FIND_ReportDataThisYear = "select count(ticket_id) as total,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 8 and ticket_date >= current_date + interval '1 year') as open,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 10 and ticket_date >= current_date + interval '1 year') as inprogress,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 11 and ticket_date >= current_date + interval '1 year') as hold,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 30 and ticket_date >= current_date + interval '1 year') as finish_reported,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 22 and ticket_date >= current_date + interval '1 year') as urgent,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 21 and ticket_date >= current_date + interval '1 year') as high,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 20 and ticket_date >= current_date + interval '1 year') as medium,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 19 and ticket_date >= current_date + interval '1 year') as low,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 31 and ticket_date >= current_date + interval '1 year') as request,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 32 and ticket_date >= current_date + interval '1 year') as task,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 33 and ticket_date >= current_date + interval '1 year') as incident from trouble_ticket WHERE ticket_date >= current_date + interval '1 year';";

	public static final String FIND_ReportDatalast2Month = "select count(ticket_id) as total,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 8 and ticket_date >= current_date - interval '2 month') as open,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 10 and ticket_date >= current_date - interval '2 month') as inprogress,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 11 and ticket_date >= current_date - interval '2 month') as hold,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 30 and ticket_date >= current_date - interval '2 month') as finish_reported,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 22 and ticket_date >= current_date - interval '2 month') as urgent,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 21 and ticket_date >= current_date - interval '2 month') as high,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 20 and ticket_date >= current_date - interval '2 month') as medium,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 19 and ticket_date >= current_date - interval '2 month') as low,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 31 and ticket_date >= current_date - interval '2 month') as request,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 32 and ticket_date >= current_date - interval '2 month') as task,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 33 and ticket_date >= current_date - interval '2 month') as incident from trouble_ticket WHERE ticket_date >= current_date - interval '2 month';";

	public static final String FIND_ReportDatalast6Month = "select count(ticket_id) as total,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 8 and ticket_date >= current_date - interval '6 month') as open,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 10 and ticket_date >= current_date - interval '6 month') as inprogress,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 11 and ticket_date >= current_date - interval '6 month') as hold,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 30 and ticket_date >= current_date - interval '6 month') as finish_reported,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 22 and ticket_date >= current_date - interval '6 month') as urgent,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 21 and ticket_date >= current_date - interval '6 month') as high,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 20 and ticket_date >= current_date - interval '6 month') as medium,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 19 and ticket_date >= current_date - interval '6 month') as low,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 31 and ticket_date >= current_date - interval '6 month') as request,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 32 and ticket_date >= current_date - interval '6 month') as task,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 33 and ticket_date >= current_date - interval '6 month') as incident from trouble_ticket WHERE ticket_date >= current_date - interval '6 month';";

	public static final String FIND_ReportDataCustom = "select count(ticket_id) as total,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 8 and (ticket_date >= :start_date and ticket_date <= :end_date)) as open,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 10 and (ticket_date >= :start_date and ticket_date <= :end_date)) as inprogress,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 11 and (ticket_date >= :start_date and ticket_date <= :end_date)) as hold,\n"
			+ "(select count(ticket_id) from trouble_ticket where ticket_status_id = 30 and (ticket_date >= :start_date and ticket_date <= :end_date)) as finish_reported,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 22 and (ticket_date >= :start_date and ticket_date <= :end_date)) as urgent, \n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 21 and (ticket_date >= :start_date and ticket_date <= :end_date)) as high,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 20 and (ticket_date >= :start_date and ticket_date <= :end_date)) as medium,\n"
			+ "(select count(ticket_id) from trouble_ticket as a join sla as b ON a.sla_id = b.sla_id where b.priority_id = 19 and (ticket_date >= :start_date and ticket_date <= :end_date)) as low,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 31 and (ticket_date >= :start_date and ticket_date <= :end_date)) as request,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 32 and (ticket_date >= :start_date and ticket_date <= :end_date)) as task,\n"
			+ "(select count(ticket_id) from trouble_ticket where category_id = 33 and (ticket_date >= :start_date and ticket_date <= :end_date)) as incident from trouble_ticket WHERE ticket_date >= :start_date and ticket_date <= :end_date";

	public static final String FIND_CountRowTicketByDate = "SELECT count(*)\n" + "FROM trouble_ticket \n"
			+ "WHERE ticket_date =: CURRENT_DATE";

	@Query(value = "SELECT * FROM trouble_ticket as a\n"
			+ "		join client_company_branch as d ON a.branch_id = d.branch_id \n"
			+ "		join client_company as e ON d.company_id = e.company_id\n"
			+ "		join sla as f ON a.sla_id = f.sla_id\n"
			+ "		LEFT JOIN dispatch as g ON a.ticket_id = g.ticket_id\n"
			+ "		LEFT JOIN users as h ON g.user_id = h.user_id \n"
			+ "		WHERE a.is_deleted = false AND (LOWER(a.ticket_code) LIKE LOWER(CONCAT('%', :search , '%')) OR \n"
			+ "		LOWER(a.ticket_title) LIKE LOWER(CONCAT('%', :search , '%')) OR \n"
			+ "		LOWER(h.user_full_name) LIKE LOWER(CONCAT('%', :search , '%')) OR\n"
			+ "		LOWER(e.company_name) LIKE LOWER(CONCAT('%', :search , '%')) OR \n"
			+ "     CAST(g.start_job AS VARCHAR) LIKE concat('%', :search ,'%') OR\n"
			+ "     CONCAT(CAST(f.sla_resolution_time AS VARCHAR),' Hours') LIKE concat('%', :search ,'%'))", nativeQuery = true)
	public ArrayList<TroubleTicket> getListTicket(@Param("search") String search, Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM trouble_ticket as a\n"
			+ "		join client_company_branch as d ON a.branch_id = d.branch_id \n"
			+ "		join client_company as e ON d.company_id = e.company_id\n"
			+ "		join sla as f ON a.sla_id = f.sla_id\n"
			+ "		LEFT JOIN dispatch as g ON a.ticket_id = g.ticket_id\n"
			+ "		LEFT JOIN users as h ON g.user_id = h.user_id \n"
			+ "		WHERE a.is_deleted = false AND (LOWER(a.ticket_code) LIKE LOWER(CONCAT('%', :search , '%')) OR \n"
			+ "		LOWER(a.ticket_title) LIKE LOWER(CONCAT('%', :search , '%')) OR \n"
			+ "		LOWER(h.user_full_name) LIKE LOWER(CONCAT('%', :search , '%')) OR\n"
			+ "		LOWER(e.company_name) LIKE LOWER(CONCAT('%', :search , '%')) OR \n"
			+ "     CAST(g.start_job AS VARCHAR) LIKE concat('%', :search ,'%') OR\n"
			+ "     CONCAT(CAST(f.sla_resolution_time AS VARCHAR),' Hours') LIKE concat('%', :search ,'%'))", nativeQuery = true)
	public Integer getTotalListTicket(@Param("search") String search);

	@Query(value = "SELECT * FROM trouble_ticket as a\n"
			+ "		join client_company_branch as d ON a.branch_id = d.branch_id \n"
			+ "		join client_company as e ON d.company_id = e.company_id\n"
			+ "		join sla as f ON a.sla_id = f.sla_id\n"
			+ "		LEFT JOIN dispatch as g ON a.ticket_id = g.ticket_id\n"
			+ "		LEFT JOIN users as h ON g.user_id = h.user_id \n"
			+ "		WHERE a.ticket_status_id = :ticket_status_id AND a.is_deleted = false"
			+ "		AND (LOWER(a.ticket_code) LIKE LOWER(CONCAT('%', :search , '%')) OR \n"
			+ "		LOWER(a.ticket_title) LIKE LOWER(CONCAT('%', :search , '%')) OR \n"
			+ "		LOWER(h.user_full_name) LIKE LOWER(CONCAT('%', :search , '%')) OR\n"
			+ "		LOWER(e.company_name) LIKE LOWER(CONCAT('%', :search , '%')) OR \n"
			+ "     CAST(g.start_job AS VARCHAR) LIKE concat('%', :search ,'%') OR\n"
			+ "     CONCAT(CAST(f.sla_resolution_time AS VARCHAR),' Hours') LIKE concat('%', :search ,'%'))", nativeQuery = true)
	public ArrayList<TroubleTicket> getListTicketFilter(@Param("ticket_status_id") Long ticketStatusId,
			@Param("search") String search, Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM trouble_ticket as a\n"
			+ "		join client_company_branch as d ON a.branch_id = d.branch_id \n"
			+ "		join client_company as e ON d.company_id = e.company_id\n"
			+ "		join sla as f ON a.sla_id = f.sla_id\n"
			+ "		LEFT JOIN dispatch as g ON a.ticket_id = g.ticket_id\n"
			+ "		LEFT JOIN users as h ON g.user_id = h.user_id \n"
			+ "		WHERE a.ticket_status_id = :ticket_status_id AND a.is_deleted = false"
			+ "		AND (LOWER(a.ticket_code) LIKE LOWER(CONCAT('%', :search , '%')) OR \n"
			+ "		LOWER(a.ticket_title) LIKE LOWER(CONCAT('%', :search , '%')) OR \n"
			+ "		LOWER(h.user_full_name) LIKE LOWER(CONCAT('%', :search , '%')) OR\n"
			+ "		LOWER(e.company_name) LIKE LOWER(CONCAT('%', :search , '%')) OR \n"
			+ "     CAST(g.start_job AS VARCHAR) LIKE concat('%', :search ,'%') OR\n"
			+ "     CONCAT(CAST(f.sla_resolution_time AS VARCHAR),' Hours') LIKE concat('%', :search ,'%'))", nativeQuery = true)
	public Integer getTotalListTicketFilter(@Param("ticket_status_id") Long ticketStatusId,
			@Param("search") String search);

	@Query(value = "SELECT * FROM trouble_ticket as a\n"
			+ "		join client_company_branch as b ON a.branch_id = b.branch_id\n"
			+ "		join client_company as c ON b.company_id = c.company_id\n"
			+ "		join dispatch as d ON a.ticket_id = d.ticket_id\n"
			+ "		join history as e ON d.order_id = e.order_id \n"
			+ "		join (select order_id,max(created_on) from history group by order_id) f on f.max = e.created_on WHERE a.ticket_status_id=13 \n"
			+ "		AND (LOWER(a.ticket_code) LIKE LOWER(CONCAT('%', :search  , '%')) OR \n"
			+ "		LOWER(a.ticket_title) LIKE LOWER(CONCAT('%', :search , '%')) OR \n"
			+ "		LOWER(c.company_name) LIKE LOWER(CONCAT('%', :search , '%')) OR \n"
			+ "		LOWER(e.reason) LIKE LOWER(CONCAT('%', :search , '%')))", nativeQuery = true)
	public ArrayList<TroubleTicket> getAllTroubleTicketCancel(@Param("search") String search, Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM trouble_ticket as a\n"
			+ "		join client_company_branch as b ON a.branch_id = b.branch_id\n"
			+ "		join client_company as c ON b.company_id = c.company_id\n"
			+ "		join dispatch as d ON a.ticket_id = d.ticket_id\n"
			+ "		join history as e ON d.order_id = e.order_id \n"
			+ "		join (select order_id,max(created_on) from history group by order_id) f on f.max = e.created_on WHERE a.ticket_status_id=13 \n"
			+ "		AND (LOWER(a.ticket_code) LIKE LOWER(CONCAT('%', :search  , '%')) OR \n"
			+ "		LOWER(a.ticket_title) LIKE LOWER(CONCAT('%', :search , '%')) OR \n"
			+ "		LOWER(c.company_name) LIKE LOWER(CONCAT('%', :search , '%')) OR \n"
			+ "		LOWER(e.reason) LIKE LOWER(CONCAT('%', :search , '%')))", nativeQuery = true)
	public Integer getTotalTroubleTicketCancel(@Param("search") String search);

	@Query(value = "SELECT * FROM trouble_ticket as a\n"
			+ "		join client_company_branch as b ON a.branch_id = b.branch_id\n"
			+ "		join client_company as c ON b.company_id = c.company_id\n"
			+ "		join dispatch as d ON a.ticket_id = d.ticket_id\n"
			+ "		join history as e ON d.order_id = e.order_id \n"
			+ "		join (select order_id,max(created_on) from history group by order_id) f on f.max = e.created_on \n"
			+ "		LEFT join dispatch_report as g on d.order_id = g.order_id \n"
			+ "		join users as h ON d.user_id = h.user_id\n"
			+ "		WHERE (a.ticket_status_id=12 OR a.ticket_status_id=30)\n"
			+ "		AND (LOWER(a.ticket_code) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(a.ticket_title) LIKE LOWER(CONCAT('%', :search , '%')) OR\n"
			+ "		LOWER(c.company_name) LIKE LOWER(CONCAT('%', :search , '%')) OR\n"
			+ "		LOWER(h.user_full_name) LIKE LOWER(CONCAT('%', :search , '%')) OR\n"
			+ "     CAST(d.start_job AS VARCHAR) LIKE CONCAT('%', :search,'%') OR\n"
			+ "     CAST(d.end_job AS VARCHAR) LIKE CONCAT('%', :search,'%'))", nativeQuery = true)
	public ArrayList<TroubleTicket> getAllTroubleTicketFinish(@Param("search") String search, Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM trouble_ticket as a\n"
			+ "		join client_company_branch as b ON a.branch_id = b.branch_id\n"
			+ "		join client_company as c ON b.company_id = c.company_id\n"
			+ "		join dispatch as d ON a.ticket_id = d.ticket_id\n"
			+ "		join history as e ON d.order_id = e.order_id \n"
			+ "		join (select order_id,max(created_on) from history group by order_id) f on f.max = e.created_on \n"
			+ "		LEFT join dispatch_report as g on d.order_id = g.order_id \n"
			+ "		join users as h ON d.user_id = h.user_id\n"
			+ "		WHERE (a.ticket_status_id=12 OR a.ticket_status_id=30)\n"
			+ "		AND (LOWER(a.ticket_code) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(a.ticket_title) LIKE LOWER(CONCAT('%', :search , '%')) OR\n"
			+ "		LOWER(c.company_name) LIKE LOWER(CONCAT('%', :search , '%')) OR\n"
			+ "		LOWER(h.user_full_name) LIKE LOWER(CONCAT('%', :search , '%')) OR\n"
			+ "     CAST(d.start_job AS VARCHAR) LIKE CONCAT('%', :search,'%') OR\n"
			+ "     CAST(d.end_job AS VARCHAR) LIKE CONCAT('%', :search,'%'))", nativeQuery = true)
	public Integer getTotalTroubleTicketFinish(@Param("search") String search);

	@Query(value = "SELECT * FROM trouble_ticket as a\n"
			+ "		join client_company_branch as b ON a.branch_id = b.branch_id\n"
			+ "		join client_company as c ON b.company_id = c.company_id\n"
			+ "		join dispatch as d ON a.ticket_id = d.ticket_id\n"
			+ "		join history as e ON d.order_id = e.order_id \n"
			+ "		join (select order_id,max(created_on) from history group by order_id) f on f.max = e.created_on \n"
			+ "		LEFT join dispatch_report as g on d.order_id = g.order_id \n"
			+ "		join users as h ON d.user_id = h.user_id\n"
			+ "		WHERE (a.ticket_status_id=12 OR a.ticket_status_id=30) AND g.dispatch_report_rating = :dispatch_report_rating\n"
			+ "		AND (LOWER(a.ticket_code) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(a.ticket_title) LIKE LOWER(CONCAT('%', :search , '%')) OR\n"
			+ "		LOWER(c.company_name) LIKE LOWER(CONCAT('%', :search , '%')) OR\n"
			+ "		LOWER(h.user_full_name) LIKE LOWER(CONCAT('%', :search , '%')) OR\n"
			+ "     CAST(d.start_job AS VARCHAR) LIKE CONCAT('%', :search,'%') OR\n"
			+ "     CAST(d.end_job AS VARCHAR) LIKE CONCAT('%', :search,'%'))", nativeQuery = true)
	public ArrayList<TroubleTicket> getAllTroubleTicketFinishFilter(@Param("dispatch_report_rating") Integer filter,
			@Param("search") String search, Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM trouble_ticket as a\n"
			+ "		join client_company_branch as b ON a.branch_id = b.branch_id\n"
			+ "		join client_company as c ON b.company_id = c.company_id\n"
			+ "		join dispatch as d ON a.ticket_id = d.ticket_id\n"
			+ "		join history as e ON d.order_id = e.order_id \n"
			+ "		join (select order_id,max(created_on) from history group by order_id) f on f.max = e.created_on \n"
			+ "		LEFT join dispatch_report as g on d.order_id = g.order_id \n"
			+ "		join users as h ON d.user_id = h.user_id\n"
			+ "		WHERE (a.ticket_status_id=12 OR a.ticket_status_id=30) AND g.dispatch_report_rating = :dispatch_report_rating\n"
			+ "		AND (LOWER(a.ticket_code) LIKE LOWER(CONCAT('%', :search  , '%')) OR\n"
			+ "		LOWER(a.ticket_title) LIKE LOWER(CONCAT('%', :search , '%')) OR\n"
			+ "		LOWER(c.company_name) LIKE LOWER(CONCAT('%', :search , '%')) OR\n"
			+ "		LOWER(h.user_full_name) LIKE LOWER(CONCAT('%', :search , '%')) OR\n"
			+ "     CAST(d.start_job AS VARCHAR) LIKE CONCAT('%', :search,'%') OR\n"
			+ "     CAST(d.end_job AS VARCHAR) LIKE CONCAT('%', :search,'%'))", nativeQuery = true)
	public Integer getTotalTroubleTicketFinishFilter(@Param("dispatch_report_rating") Integer filter,
			@Param("search") String search);

	@Query(value = FIND_ReportDataToday, nativeQuery = true)
	public List<Object[]> getLoadDataWorkOrderToday();

	@Query(value = FIND_ReportDataThisWeek, nativeQuery = true)
	public List<Object[]> getLoadDataWorkOrderThisWeek();

	@Query(value = FIND_ReportDataThisMonth, nativeQuery = true)
	public List<Object[]> getLoadDataWorkOrderThisMonth();

	@Query(value = FIND_ReportDataThisYear, nativeQuery = true)
	public List<Object[]> getLoadDataWorkOrderThisYear();

	@Query(value = FIND_ReportDatalast2Month, nativeQuery = true)
	public List<Object[]> getLoadDataWorkOrderLast2Month();

	@Query(value = FIND_ReportDatalast6Month, nativeQuery = true)
	public List<Object[]> getLoadDataWorkOrderLast6Month();

	@Query(value = FIND_ReportDataCustom, nativeQuery = true)
	public List<Object> getLoadDataWorkOrderCustom(@Param("start_date") java.util.Date start_date,
			@Param("end_date") java.util.Date end_date);

	@Query(value = FIND_CountRowTicketByDate, nativeQuery = true)
	public long getRowCountByDate();

	@Modifying
	@Query(value = "INSERT INTO public.trouble_ticket \n"
			+ "	(ticket_status_id, category_id, branch_id, sla_id, job_id, pic_id, ticket_title, ticket_date, ticket_time, ticket_description, ticket_duration_time, created_by, last_modified_by, ticket_due_date, is_deleted, ticket_code, created_on, last_modified_on, file_name, file_path, priority_id) \n"
			+ " 	VALUES(:ticketStatusId, :categoryId, :branchId, :slaId, :jobId, :picId, :ticketTitle, CURRENT_DATE, CURRENT_TIME, :ticketDescription, :ticketDurationTime, :createdBy, :lastModifiedBy, null, false,(SELECT CONCAT(:ticketCode, (SELECT TRIM((SELECT lpad((SELECT to_char((SELECT count(m) FROM trouble_ticket m WHERE m.ticket_date= CURRENT_DATE)+1,'0000')), 5, '0')),' ')))) ,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, :fileName, :filePath, :priorityId)", nativeQuery = true)
	@Transactional
	public void saveTicket(@Param("ticketStatusId") Long ticketStatusId, @Param("categoryId") long categoryId,
			@Param("branchId") Long BranchId, @Param("slaId") Long slaId, @Param("jobId") Long jobId,
			@Param("picId") Long picId, @Param("ticketTitle") String ticketTitle,
			@Param("ticketDescription") String ticketDescription,
			@Param("ticketDurationTime") BigInteger ticketDurationTime, @Param("createdBy") long createdBy,
			@Param("lastModifiedBy") long lastModifiedBy, @Param("ticketCode") String ticketCode,
			@Param("fileName") String fileName, @Param("filePath") String filePath,
			@Param("priorityId") long priorityId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE trouble_ticket set ticket_due_date = (SELECT DATE(TO_TIMESTAMP(CONCAT(ticket_date,' ',ticket_time),'YYYY-MM-DD HH24:MI:SS') + \"time\"(to_timestamp(ticket_duration_time*3600))))\n"
			+ "WHERE ticket_due_date IS NULL", nativeQuery = true)
	public int getTicketDueDate();

	@Query(value = "select COUNT(*) from trouble_ticket \n"
			+ "		WHERE ticket_status_id = :ticket_status_id AND last_modified_on >= :date", nativeQuery = true)
	Integer getTotalStatus(@Param("ticket_status_id") Long ticketStatusId,
			@Param("date") Date date);

	@Query(value = "select COUNT(*) from trouble_ticket  \n"
			+ "		WHERE ticket_status_id = :ticket_status_id AND (last_modified_on >= :start_date and last_modified_on <= :end_date)", nativeQuery = true)
	Integer getTotalStatusCustom(@Param("ticket_status_id") Long ticketStatusId,
			@Param("start_date") Date startDate, @Param("end_date") Date endDate);

	@Query(value = "select * from trouble_ticket\n"
			+ "where ticket_status_id = 8 AND is_deleted=false", nativeQuery = true)
	public ArrayList<TroubleTicket> getTicketListOpen(Pageable pageable);

	@Query(value = "select * from trouble_ticket \n"
			+ "		WHERE ticket_status_id = :ticket_status_id AND SUBSTR(to_char(created_on,'yyyy-MM-dd'), 6,2) = :month", nativeQuery = true)
	public ArrayList<TroubleTicket> getChartTicket(@Param("ticket_status_id") Long ticketStatusId,
			@Param("month") String month);

	@Query(value = "select COUNT(*) from trouble_ticket\n"
			+ "where ticket_status_id = 8 AND is_deleted=false", nativeQuery = true)
	public Integer getTotalTicketListOpen();

	@Query(value = "SELECT * FROM trouble_ticket WHERE is_deleted = false AND branch_id = :branchId LIMIT 1", nativeQuery = true)
	TroubleTicket getTroubleTicketByBranchId(@Param("branchId") Long branchId);

	@Query(value = "select * from trouble_ticket as tt where job_id = :jobId and is_deleted = false\n"
			+ "order by ticket_id LIMIT 1", nativeQuery = true)
	public TroubleTicket findJobIdOnTroubleTicket(@Param("jobId") Long jobId);

	@Query(value = "select * from trouble_ticket where sla_id = :slaId limit 1", nativeQuery = true)
	public TroubleTicket findBySLAId(@Param("slaId") Long slaId);

	@Query(value = "SELECT ticket_code FROM trouble_ticket WHERE ticket_date = :date ORDER BY ticket_code DESC LIMIT 1", nativeQuery = true)
	public String getLastTicketCode(@Param("date") java.util.Date date);

	@Query(value = "select * from trouble_ticket where ticket_title = :ticketTitle AND ticket_description = :ticketDescription AND ticket_date = CURRENT_DATE limit 1", nativeQuery = true)
	public TroubleTicket validationImport(@Param("ticketTitle") String ticketTitle,
			@Param("ticketDescription") String ticketDescription);

	@Query(value = "SELECT COUNT(*) FROM trouble_ticket WHERE is_deleted = false AND priority_id = :priorityId AND last_modified_on >= :date", nativeQuery = true)
	Integer ticketBaseOnPriority(@Param("priorityId") Long priorityId, @Param("date") Date date);

	@Query(value = "SELECT COUNT(*) FROM trouble_ticket WHERE is_deleted = false AND priority_id = :priorityId AND (last_modified_on >= :start_date and last_modified_on <= :end_date)", nativeQuery = true)
	Integer ticketBaseOnPriorityCustom(@Param("priorityId") Long priorityId,
			@Param("start_date") Date startDate, @Param("end_date") Date endDate);

	@Query(value = "SELECT COUNT(*) FROM trouble_ticket WHERE is_deleted = false AND \n"
			+	"ticket_status_id = :ticketStatusId AND (last_modified_on >= :start_date and last_modified_on <= :end_date)", nativeQuery = true)
	Integer numberOfTicket(@Param("ticketStatusId") Long ticketStatusId,
	@Param("start_date") Date startDate, @Param("end_date") Date endDate);
}
