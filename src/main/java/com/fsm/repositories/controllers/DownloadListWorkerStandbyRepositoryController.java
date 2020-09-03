package com.fsm.repositories.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fsm.repositories.DownloadListWorkerStandbyRepository;
import com.fsm.repositories.UsersRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


@RestController
@RequestMapping("/monitoring")
public class DownloadListWorkerStandbyRepositoryController {

	@Value("${spring.datasource.username}")
	private String userName;
	
	@Value("${spring.datasource.password}")
	private String password;
	
	@Value("${spring.datasource.url}")
	private String url;
	
	@Autowired
	DownloadListWorkerStandbyRepository downloadListWorkerStandbyRepository;
	
	@Autowired
	UsersRepository userRepository;
	
	Connection connection;
	Statement stmt = null;
	ResultSet rs = null;
	String query = " ";
	ResultSetMetaData rsmd = null;
	
	public void connect() {
		try {
			connection = DriverManager.getConnection(url, userName, password);
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			System.out.println("ERROR connecting to database!");
			System.out.println(e.toString());
		}
	}
	
	@GetMapping("standbyList/excel/{namaFile}")
	public void getDataStandbyListToExcel (HttpServletResponse response,@PathVariable(value = "namaFile")String namaFile) throws Exception{
	response.setContentType("application/octet-stream");
	response.setHeader("Content-Disposition", "attachment; filename= " + namaFile +".xls");
	Workbook writeWorkBook = new HSSFWorkbook();
	Sheet desSheet = writeWorkBook.createSheet("new sheet");
		try {
			connect();
			query = DownloadListWorkerStandbyRepository.Download_List_Worker_Standby;
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			rsmd = rs.getMetaData();
				int columnsNumber = rsmd.getColumnCount();
				Row desRow1 = desSheet.createRow(0);
					for (int col = 0; col < columnsNumber; col++) {
						Cell newpath = desRow1.createCell(col);
						newpath.setCellValue(rsmd.getColumnLabel(col+1));
					}
					while (rs.next()) {
						Row desRow = desSheet.createRow(rs.getRow());
						for (int col = 0; col < columnsNumber; col++) {
							Cell newpath = desRow.createCell(col);
							newpath.setCellValue(rs.getString(col + 1));
						}
					}
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					writeWorkBook.write(out);
					ByteArrayInputStream stream = new ByteArrayInputStream(out.toByteArray());
					IOUtils.copy(stream, response.getOutputStream());
					
		} catch (SQLException e) {
			System.out.println("Failed to get data from database");
		}
	}
	
	@GetMapping("standbyList/csv/{namaFile}")
	public void getDataStandbyListToCSV (HttpServletResponse response,@PathVariable(value = "namaFile")String namaFile) throws Exception{
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename= " + namaFile +".csv");
		Workbook writeWorkBook = new HSSFWorkbook();
		Sheet desSheet = writeWorkBook.createSheet("new sheet");
			try {
				connect();
				query = DownloadListWorkerStandbyRepository.Download_List_Worker_Standby;
				stmt = connection.createStatement();
				rs = stmt.executeQuery(query);
				rsmd = rs.getMetaData();
					int columnsNumber = rsmd.getColumnCount();
					Row desRow1 = desSheet.createRow(0);
						for (int col = 0; col < columnsNumber; col++) {
							Cell newpath = desRow1.createCell(col);
							newpath.setCellValue(rsmd.getColumnLabel(col+1));
						}
						while (rs.next()) {
							Row desRow = desSheet.createRow(rs.getRow());
							for (int col = 0; col < columnsNumber; col++) {
								Cell newpath = desRow.createCell(col);
								newpath.setCellValue(rs.getString(col + 1));
							}
						}
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						writeWorkBook.write(out);
						ByteArrayInputStream stream = new ByteArrayInputStream(out.toByteArray());
						IOUtils.copy(stream, response.getOutputStream());
			} catch (SQLException e) {
				System.out.println("Failed to get data from database");
			}
		}
	// masih progress
	@GetMapping("standbyList/pdf/{name}")
	public void getDataStandbyListtoPDF (HttpServletResponse response,@PathVariable(value = "name")String name) throws IOException, DocumentException, Exception{
		String fileName = name + ".pdf";
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());
		document.open();
			
		try {
			connect();
			query = DownloadListWorkerStandbyRepository.Download_List_Worker_Standby;
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			rsmd = rs.getMetaData();
				int columnsNumber = rsmd.getColumnCount();
				PdfPTable table = new PdfPTable(columnsNumber);
				table.setWidthPercentage(100);
					for (int col = 0; col < columnsNumber; col++) {
						table.addCell(rsmd.getColumnLabel(col+1));
					}
					while (rs.next()) {
						for (int col = 0; col < columnsNumber; col++) {
							table.addCell(rs.getString(col + 1));
						}	
					}
			document.add(table);
			document.close();
			rs.close();
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			System.out.println("Failed to get data from database");
		}
	}
}
