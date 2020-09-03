package com.fsm.repositories.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fsm.repositories.ExportRepository;

@RestController
@RequestMapping("/export")
public class ExportRepositoryController {

	@Value("${spring.datasource.username}")
	private String userName;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${spring.datasource.url}")
	private String url;

	@Autowired
	ExportRepository exportRepo;

	Connection connection;
	Statement stmt = null;
	ResultSet rs = null;
	String query = "";
	ResultSetMetaData rsmd = null;

	public ExportRepositoryController() {
		connect();
	}

	public void connect() {
		try {
			connection = DriverManager.getConnection(url, userName, password);
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			System.out.println("ERROR connecting to database!");
			System.out.println(e.toString());
		}
	}

	@PostMapping("/ticket")
	public void getTicketList(HttpServletResponse response) throws Exception {
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=ticket.xls");
		Workbook writeWorkbook = new HSSFWorkbook();
		Sheet desSheet = writeWorkbook.createSheet("new sheet");
		try {

			query = ExportRepository.FIND_TroubleTicketList;
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			Row desRow1 = desSheet.createRow(0);
			for (int col = 0; col < columnsNumber; col++) {
				Cell newpath = desRow1.createCell(col);
				newpath.setCellValue(rsmd.getColumnLabel(col + 1));
			}
			while (rs.next()) {
				Row desRow = desSheet.createRow(rs.getRow());
				for (int col = 0; col < columnsNumber; col++) {
					Cell newpath = desRow.createCell(col);
					newpath.setCellValue(rs.getString(col + 1));
				}
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			writeWorkbook.write(out);
			ByteArrayInputStream stream = new ByteArrayInputStream(out.toByteArray());
			IOUtils.copy(stream, response.getOutputStream());
		} catch (SQLException e) {
			System.out.println("Failed to get data from database");
		}
	}

	@PostMapping("/dispatch")
	public void getDispatchList(HttpServletResponse response) throws Exception {
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=dispatch.xls");
		Workbook writeWorkbook = new HSSFWorkbook();
		Sheet desSheet = writeWorkbook.createSheet("new sheet");
		try {
			query = ExportRepository.FIND_DispatchList;
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			Row desRow1 = desSheet.createRow(0);
			for (int col = 0; col < columnsNumber; col++) {
				Cell newpath = desRow1.createCell(col);
				newpath.setCellValue(rsmd.getColumnLabel(col + 1));
			}
			while (rs.next()) {
				Row desRow = desSheet.createRow(rs.getRow());
				for (int col = 0; col < columnsNumber; col++) {
					Cell newpath = desRow.createCell(col);
					newpath.setCellValue(rs.getString(col + 1));
				}
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			writeWorkbook.write(out);
			ByteArrayInputStream stream = new ByteArrayInputStream(out.toByteArray());
			IOUtils.copy(stream, response.getOutputStream());
		} catch (SQLException e) {
			System.out.println("Failed to get data from database");
		}
	}

	@PostMapping("/users")
	public void getUsersList(HttpServletResponse response) throws Exception {
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=users.xls");
		Workbook writeWorkbook = new HSSFWorkbook();
		Sheet desSheet = writeWorkbook.createSheet("new sheet");
		try {
			query = ExportRepository.FIND_UsersList;
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			Row desRow1 = desSheet.createRow(0);
			for (int col = 0; col < columnsNumber; col++) {
				Cell newpath = desRow1.createCell(col);
				newpath.setCellValue(rsmd.getColumnLabel(col + 1));
			}
			while (rs.next()) {
				Row desRow = desSheet.createRow(rs.getRow());
				for (int col = 0; col < columnsNumber; col++) {
					Cell newpath = desRow.createCell(col);
					newpath.setCellValue(rs.getString(col + 1));
				}
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			writeWorkbook.write(out);
			ByteArrayInputStream stream = new ByteArrayInputStream(out.toByteArray());
			IOUtils.copy(stream, response.getOutputStream());
		} catch (SQLException e) {
			System.out.println("Failed to get data from database");
		}
	}

	@PostMapping("/company")
	public void getCompanyList(HttpServletResponse response) throws Exception {
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=company.xls");
		Workbook writeWorkbook = new HSSFWorkbook();
		Sheet desSheet = writeWorkbook.createSheet("new sheet");
		try {
			query = ExportRepository.FIND_CompanyList;
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			Row desRow1 = desSheet.createRow(0);
			for (int col = 0; col < columnsNumber; col++) {
				Cell newpath = desRow1.createCell(col);
				newpath.setCellValue(rsmd.getColumnLabel(col + 1));
			}
			while (rs.next()) {
				Row desRow = desSheet.createRow(rs.getRow());
				for (int col = 0; col < columnsNumber; col++) {
					Cell newpath = desRow.createCell(col);
					newpath.setCellValue(rs.getString(col + 1));
				}
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			writeWorkbook.write(out);
			ByteArrayInputStream stream = new ByteArrayInputStream(out.toByteArray());
			IOUtils.copy(stream, response.getOutputStream());
		} catch (SQLException e) {
			System.out.println("Failed to get data from database");
		}
	}

	@PostMapping("/sla")
	public void getSlaList(HttpServletResponse response) throws Exception {
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=sla.xls");
		Workbook writeWorkbook = new HSSFWorkbook();
		Sheet desSheet = writeWorkbook.createSheet("new sheet");
		try {
			query = ExportRepository.FIND_SlaList;
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			Row desRow1 = desSheet.createRow(0);
			for (int col = 0; col < columnsNumber; col++) {
				Cell newpath = desRow1.createCell(col);
				newpath.setCellValue(rsmd.getColumnLabel(col + 1));
			}
			while (rs.next()) {
				Row desRow = desSheet.createRow(rs.getRow());
				for (int col = 0; col < columnsNumber; col++) {
					Cell newpath = desRow.createCell(col);
					newpath.setCellValue(rs.getString(col + 1));
				}
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			writeWorkbook.write(out);
			ByteArrayInputStream stream = new ByteArrayInputStream(out.toByteArray());
			IOUtils.copy(stream, response.getOutputStream());
		} catch (SQLException e) {
			System.out.println("Failed to get data from database");
		}
	}

	@PostMapping("/workingTime")
	public void getWorkingTimeList(HttpServletResponse response) throws Exception {
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=WorkingTime.xls");
		Workbook writeWorkbook = new HSSFWorkbook();
		Sheet desSheet = writeWorkbook.createSheet("new sheet");
		try {
			query = ExportRepository.FIND_WorkingTimeList;
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			Row desRow1 = desSheet.createRow(0);
			for (int col = 0; col < columnsNumber; col++) {
				Cell newpath = desRow1.createCell(col);
				newpath.setCellValue(rsmd.getColumnLabel(col + 1));
			}
			while (rs.next()) {
				Row desRow = desSheet.createRow(rs.getRow());
				for (int col = 0; col < columnsNumber; col++) {
					Cell newpath = desRow.createCell(col);
					newpath.setCellValue(rs.getString(col + 1));
				}
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			writeWorkbook.write(out);
			ByteArrayInputStream stream = new ByteArrayInputStream(out.toByteArray());
			IOUtils.copy(stream, response.getOutputStream());
		} catch (SQLException e) {
			System.out.println("Failed to get data from database");
		}
	}

	@PostMapping("/jobClass")
	public void getJobClassList(HttpServletResponse response) throws Exception {
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=JobClass.xls");
		Workbook writeWorkbook = new HSSFWorkbook();
		Sheet desSheet = writeWorkbook.createSheet("new sheet");
		try {
			query = ExportRepository.FIND_JobClassList;
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			Row desRow1 = desSheet.createRow(0);
			for (int col = 0; col < columnsNumber; col++) {
				Cell newpath = desRow1.createCell(col);
				newpath.setCellValue(rsmd.getColumnLabel(col + 1));
			}
			while (rs.next()) {
				Row desRow = desSheet.createRow(rs.getRow());
				for (int col = 0; col < columnsNumber; col++) {
					Cell newpath = desRow.createCell(col);
					newpath.setCellValue(rs.getString(col + 1));
				}
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			writeWorkbook.write(out);
			ByteArrayInputStream stream = new ByteArrayInputStream(out.toByteArray());
			IOUtils.copy(stream, response.getOutputStream());
		} catch (SQLException e) {
			System.out.println("Failed to get data from database");
		}
	}

	@PostMapping("/jobCategory")
	public void getJobCategoryList(HttpServletResponse response) throws Exception {
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=JobCategory.xls");
		Workbook writeWorkbook = new HSSFWorkbook();
		Sheet desSheet = writeWorkbook.createSheet("new sheet");
		try {
			query = ExportRepository.FIND_JobCategoryList;
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			Row desRow1 = desSheet.createRow(0);
			for (int col = 0; col < columnsNumber; col++) {
				Cell newpath = desRow1.createCell(col);
				newpath.setCellValue(rsmd.getColumnLabel(col + 1));
			}
			while (rs.next()) {
				Row desRow = desSheet.createRow(rs.getRow());
				for (int col = 0; col < columnsNumber; col++) {
					Cell newpath = desRow.createCell(col);
					newpath.setCellValue(rs.getString(col + 1));
				}
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			writeWorkbook.write(out);
			ByteArrayInputStream stream = new ByteArrayInputStream(out.toByteArray());
			IOUtils.copy(stream, response.getOutputStream());
		} catch (SQLException e) {
			System.out.println("Failed to get data from database");
		}
	}

	@PostMapping("/job")
	public void getJobList(HttpServletResponse response) throws Exception {
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=Job.xls");
		Workbook writeWorkbook = new HSSFWorkbook();
		Sheet desSheet = writeWorkbook.createSheet("new sheet");
		try {
			query = ExportRepository.FIND_JobList;
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			Row desRow1 = desSheet.createRow(0);
			for (int col = 0; col < columnsNumber; col++) {
				Cell newpath = desRow1.createCell(col);
				newpath.setCellValue(rsmd.getColumnLabel(col + 1));
			}
			while (rs.next()) {
				Row desRow = desSheet.createRow(rs.getRow());
				for (int col = 0; col < columnsNumber; col++) {
					Cell newpath = desRow.createCell(col);
					newpath.setCellValue(rs.getString(col + 1));
				}
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			writeWorkbook.write(out);
			ByteArrayInputStream stream = new ByteArrayInputStream(out.toByteArray());
			IOUtils.copy(stream, response.getOutputStream());
		} catch (SQLException e) {
			System.out.println("Failed to get data from database");
		}
	}

	@PostMapping("/uom")
	public void getUomList(HttpServletResponse response) throws Exception {
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=Uom.xls");
		Workbook writeWorkbook = new HSSFWorkbook();
		Sheet desSheet = writeWorkbook.createSheet("new sheet");
		try {
			query = ExportRepository.FIND_UomList;
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			Row desRow1 = desSheet.createRow(0);
			for (int col = 0; col < columnsNumber; col++) {
				Cell newpath = desRow1.createCell(col);
				newpath.setCellValue(rsmd.getColumnLabel(col + 1));
			}
			while (rs.next()) {
				Row desRow = desSheet.createRow(rs.getRow());
				for (int col = 0; col < columnsNumber; col++) {
					Cell newpath = desRow.createCell(col);
					newpath.setCellValue(rs.getString(col + 1));
				}
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			writeWorkbook.write(out);
			ByteArrayInputStream stream = new ByteArrayInputStream(out.toByteArray());
			IOUtils.copy(stream, response.getOutputStream());
		} catch (SQLException e) {
			System.out.println("Failed to get data from database");
		}
	}

	@PostMapping("/worker")
	public void getWorkerList(HttpServletResponse response) throws Exception {
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=Worker.xls");
		Workbook writeWorkbook = new HSSFWorkbook();
		Sheet desSheet = writeWorkbook.createSheet("new sheet");
		try {
			query = ExportRepository.FIND_WorkerList;
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			Row desRow1 = desSheet.createRow(0);
			for (int col = 0; col < columnsNumber; col++) {
				Cell newpath = desRow1.createCell(col);
				newpath.setCellValue(rsmd.getColumnLabel(col + 1));
			}
			while (rs.next()) {
				Row desRow = desSheet.createRow(rs.getRow());
				for (int col = 0; col < columnsNumber; col++) {
					Cell newpath = desRow.createCell(col);
					newpath.setCellValue(rs.getString(col + 1));
				}
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			writeWorkbook.write(out);
			ByteArrayInputStream stream = new ByteArrayInputStream(out.toByteArray());
			IOUtils.copy(stream, response.getOutputStream());
		} catch (SQLException e) {
			System.out.println("Failed to get data from database");
		}
	}

	@PostMapping("/role")
	public void getRoleList(HttpServletResponse response) throws Exception {
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=Role.xls");
		Workbook writeWorkbook = new HSSFWorkbook();
		Sheet desSheet = writeWorkbook.createSheet("new sheet");
		try {
			query = ExportRepository.FIND_RoleList;
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			Row desRow1 = desSheet.createRow(0);
			for (int col = 0; col < columnsNumber; col++) {
				Cell newpath = desRow1.createCell(col);
				newpath.setCellValue(rsmd.getColumnLabel(col + 1));
			}
			while (rs.next()) {
				Row desRow = desSheet.createRow(rs.getRow());
				for (int col = 0; col < columnsNumber; col++) {
					Cell newpath = desRow.createCell(col);
					newpath.setCellValue(rs.getString(col + 1));
				}
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			writeWorkbook.write(out);
			ByteArrayInputStream stream = new ByteArrayInputStream(out.toByteArray());
			IOUtils.copy(stream, response.getOutputStream());
		} catch (SQLException e) {
			System.out.println("Failed to get data from database");
		}
	}

	@PostMapping("/customerBranch")
	public void getCustBranchList(HttpServletResponse response) throws Exception {
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=CustomerBranch.xls");
		Workbook writeWorkbook = new HSSFWorkbook();
		Sheet desSheet = writeWorkbook.createSheet("new sheet");
		try {
			query = ExportRepository.FIND_CustomerBranchList;
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			Row desRow1 = desSheet.createRow(0);
			for (int col = 0; col < columnsNumber; col++) {
				Cell newpath = desRow1.createCell(col);
				newpath.setCellValue(rsmd.getColumnLabel(col + 1));
			}
			while (rs.next()) {
				Row desRow = desSheet.createRow(rs.getRow());
				for (int col = 0; col < columnsNumber; col++) {
					Cell newpath = desRow.createCell(col);
					newpath.setCellValue(rs.getString(col + 1));
				}
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			writeWorkbook.write(out);
			ByteArrayInputStream stream = new ByteArrayInputStream(out.toByteArray());
			IOUtils.copy(stream, response.getOutputStream());
		} catch (SQLException e) {
			System.out.println("Failed to get data from database");
		}
	}

}