package com.fsm.repositories.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fsm.repositories.TroubleTicketRepository;

@RestController
@RequestMapping("/submit")
public class AddTicketFromImportRepositoryController {

	@Value("${spring.datasource.username}")
	private String userName;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${spring.datasource.url}")
	private String url;

	@Autowired
	TroubleTicketRepository troubleticketRepository;

	@PostMapping("/troubleTicket/{userId}")
	public HashMap<String, Object> submitTicketToDatabase(@PathVariable(value = "userId") Long userId,
			@RequestBody List<Map<String, Object>> body) throws IllegalStateException, IOException, SQLException {
		HashMap<String, Object> showHashMap = new HashMap<String, Object>();

		try {
			Connection con = (Connection) DriverManager.getConnection(url, userName, password);
			con.setAutoCommit(false);
			PreparedStatement pstm = null;

			int indexNo = 1;
			for (Map<String, Object> item : body) {
				Object codeId = new Object();
				Object branchId = new Object();
				Object jobId = new Object();
				Object picId = new Object();
				Object title = new Object();
				Object duration = new Object();
				Object description = new Object();
				Object priorityId = new Object();
				Object slaId = new Object();
				String ticketCode = null;
				String code = null;
				codeId = item.get("Code Id");
				branchId = item.get("Branch Id");
				jobId = item.get("Job Id");
				picId = item.get("PIC Id");
				title = item.get("Title");
				duration = item.get("Duration");
				description = item.get("Description");
				priorityId = item.get("Priority Id");
				slaId = item.get("SLA Id");

				LocalDateTime localNow = LocalDateTime.now();
				Timestamp dateNow = Timestamp.valueOf(localNow);
				SimpleDateFormat formatter = new SimpleDateFormat("ddMMyy");
				SimpleDateFormat dateFormats = new SimpleDateFormat("dd/MM/yyyy");
				String strDate = formatter.format(dateNow);
				String stringDate = dateFormats.format(dateNow);
				Date date = dateFormats.parse(stringDate);
				String lastCode = troubleticketRepository.getLastTicketCode(date);

				if (lastCode == null) {
					String newIndex = String.valueOf(indexNo);
					if (newIndex.length() == 1) {
						code = "000";
						ticketCode = "IN" + strDate + code + indexNo;
					} else if (newIndex.length() == 2) {
						code = "00";
						ticketCode = "IN" + strDate + code + indexNo;
					} else if (newIndex.length() == 3) {
						code = "0";
						ticketCode = "IN" + strDate + code + indexNo;
					} else if (newIndex.length() == 4) {
						ticketCode = "IN" + strDate + indexNo;
					}

				} else if (lastCode != null) {
					String lastIndex = StringUtils.right(lastCode, 4);
					int indexSecond = Integer.parseInt(lastIndex);
					indexSecond = indexSecond + indexNo;
					String newIndex = String.valueOf(indexSecond);
					if (newIndex.length() == 1) {
						code = "000";
						ticketCode = "IN" + strDate + code + indexSecond;
					} else if (newIndex.length() == 2) {
						code = "00";
						ticketCode = "IN" + strDate + code + indexSecond;
					} else if (newIndex.length() == 3) {
						code = "0";
						ticketCode = "IN" + strDate + code + indexSecond;
					} else if (newIndex.length() == 4) {
						ticketCode = "IN" + strDate + indexSecond;
					}
				}

				String sql = "INSERT INTO trouble_ticket VALUES(default, 8, '" + codeId + "','" + branchId + "', '"
						+ slaId + "' ,'" + jobId + "','" + picId + "', '" + title + "',CURRENT_DATE, LOCALTIME(0),'"
						+ description + "', null, '" + duration + "','" + userId + "', '" + dateNow + "', '" + userId
						+ "', '" + dateNow + "', 'f', '" + ticketCode + "',null,null,'" + priorityId + "')";
				String sql2 = "UPDATE trouble_ticket set ticket_due_date = (SELECT DATE(TO_TIMESTAMP(CONCAT(ticket_date,' ',ticket_time),'YYYY-MM-DD HH24:MI:SS') + \"time\"(to_timestamp(ticket_duration_time*3600)))) WHERE ticket_due_date IS NULL";
				pstm = (PreparedStatement) con.prepareStatement(sql);
				pstm.execute();
				pstm = (PreparedStatement) con.prepareStatement(sql2);
				pstm.execute();
				indexNo++;
			}

			con.commit();
			pstm.close();
			con.close();

			String message = "Tiket Berhasil Dibuat";
			showHashMap.put("Status", HttpStatus.OK);
			showHashMap.put("Message", message);

		} catch (Exception e) {
			String message = "Tiket Gagal Dibuat";
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", message);
		}
		return showHashMap;
	}
}