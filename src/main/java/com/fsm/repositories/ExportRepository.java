package com.fsm.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fsm.models.TroubleTicket;

@Repository
public interface ExportRepository extends JpaRepository<TroubleTicket, Long> {

	public static final String FIND_TroubleTicketList = "SELECT ticket_date as \"Ticket Date\", trouble_ticket.ticket_id as \"Ticket Id\", ticket_title as \"Title\",\n"
			+ "client_company.company_name as \"Customer\", ticket_due_date as \"Due Date\", dispatch.end_job as \"Finish Date\", ticket_status_id as \"Ticket Status\",\n"
			+ "sla.sla_response_time as \"Response Time\", sla.sla_resolution_time as \"Resolution Time\", dispatch.order_id as \"Order Id\" \n"
			+ "FROM trouble_ticket \n"
			+ "join client_company_branch on trouble_ticket.branch_id = client_company_branch.branch_id\n"
			+ "join client_company on client_company_branch.company_id = client_company.company_id\n"
			+ "join dispatch on trouble_ticket.ticket_id = dispatch.ticket_id join sla on sla.sla_id = trouble_ticket.sla_id";

	public static final String FIND_DispatchList = "SELECT dispatch_date as \"Dispacth Date\", ticket_title as \"Ticket Tittle\", dispatch.ticket_id as \"Ticket Id\",\n"
			+ "dispatch.order_id as \"Order Id\", dispatch.start_job as \"Start Job\", dispatch.end_job as \"End Job\", sla.sla_resolution_time as \"Resolution Time\",\n"
			+ "dispatch_image_report.image_report_name as \"Photo\" , history.dispatch_status as \"Dispatch Status\"\n"
			+ "FROM dispatch \n" + "join trouble_ticket on dispatch.ticket_id = trouble_ticket.ticket_id \n"
			+ "join sla on trouble_ticket.sla_id = sla.sla_id \n"
			+ "join dispatch_report on dispatch.order_id = dispatch_report.order_id \n"
			+ "join dispatch_image_report on dispatch_report.dispatch_report_id = dispatch_image_report.dispatch_report_id \n"
			+ "join history on dispatch.order_id = history.order_id";

	public static final String FIND_UsersList = "SELECT user_id \"User Id\" , user_email \"Email\", user_name \"User Name\" FROM users";

	public static final String FIND_CompanyList = "SELECT company_id \"Company Id\", company_email \"Email\", company_name \"Company Name\" FROM client_company";

	public static final String FIND_SlaList = "SELECT sla.sla_id \"SLA Id\", client_company.company_name \"Customer\", sla_type.sla_type_name \"SLA Type\",\n"
			+ "working_time.wtime_name \"Working Time\" , city.city_name \"City\" \n" + "FROM sla \n"
			+ "join client_company on sla.company_id= client_company.company_id \n"
			+ "join sla_type on sla.sla_type_id = sla_type.sla_type_id \n"
			+ "join working_time on sla.wtime_id = working_time.wtime_id \n"
			+ "join city on sla.city_id = city.city_id";

	public static final String FIND_WorkingTimeList = "SELECT wtime_id \"Working Time Id\", wtime_name \"Working Time Name\", wtime_desc \"Description\" FROM working_time";

	public static final String FIND_JobClassList = "SELECT job_class_id \"Job Class Id\", job_class_name \"job Class Name\" FROM job_class";

	public static final String FIND_JobCategoryList = "SELECT job_category_id \"Job Category Id\", job_category_name \"Job Category Name\", job_category_tag \"Tag\",\n"
			+ "job_category_desc \"Description\", job_class.job_class_name \"Job Class\" \n" + "FROM job_category \n"
			+ "join job_class on job_category.job_class_id = job_class.job_class_id";

	public static final String FIND_JobList = "SELECT job_id \"Job Id\", job_name \"Job Name\", job_desc \"Description\", job_tag \"Tag\", \n"
			+ "job_category.job_category_name \"Job Category Name\", uom.uom_name \"UOM Name\", transport_fee \"Transport Fee\", incl_transport \"Include Transport\" \n"
			+ "FROM job \n" + "join job_category on job.job_category_id = job_category.job_category_id \n"
			+ "join uom on job.uom_id = uom.uom_id";

	public static final String FIND_UomList = "SELECT uom_id \"UOM Id\", uom_name \"UOM Name\" FROM uom";

	public static final String FIND_WorkerList = "SELECT worker_id \"Worker Id\", worker_name \"Worker Name\", worker_email \"Worker Email\" FROM worker";

	public static final String FIND_RoleList = "SELECT role_id \"Role Code\", role_name \"Role Name\" FROM role";

	public static final String FIND_CustomerBranchList = "SELECT client_company_branch.company_id \"Customer Id\", client_company_branch.branch_id \"Branch Id\", \n"
			+ "client_company.company_name \"Customer Name\", client_company_branch.branch_name \"Branch Name\", client_company_pic.pic_name \"PIC\" \n"
			+ "FROM client_company_branch \n"
			+ "join client_company on client_company_branch.company_id = client_company.company_id join client_company_pic on client_company_branch.branch_id = client_company_pic.branch_id";

	@Query(value = FIND_TroubleTicketList, nativeQuery = true)
	public List<Object[]> getTroubleTicketList();

	@Query(value = FIND_DispatchList, nativeQuery = true)
	public List<Object[]> getDispatchList();

	@Query(value = FIND_UsersList, nativeQuery = true)
	public List<Object[]> getUsersList();

	@Query(value = FIND_CompanyList, nativeQuery = true)
	public List<Object[]> getCompanyList();

	@Query(value = FIND_SlaList, nativeQuery = true)
	public List<Object[]> getSlaList();

	@Query(value = FIND_WorkingTimeList, nativeQuery = true)
	public List<Object[]> getWorkingTimeList();

	@Query(value = FIND_JobClassList, nativeQuery = true)
	public List<Object[]> getJobClassList();

	@Query(value = FIND_JobCategoryList, nativeQuery = true)
	public List<Object[]> getJobCategoryList();

	@Query(value = FIND_JobList, nativeQuery = true)
	public List<Object[]> getJobList();

	@Query(value = FIND_UomList, nativeQuery = true)
	public List<Object[]> getUomList();

	@Query(value = FIND_WorkerList, nativeQuery = true)
	public List<Object[]> getWorkerList();

	@Query(value = FIND_RoleList, nativeQuery = true)
	public List<Object[]> getRoleList();

	@Query(value = FIND_CustomerBranchList, nativeQuery = true)
	public List<Object[]> getCustBranchList();

}
