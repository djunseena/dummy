package com.fsm.repositories.controllers;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.interfaces.Minio;
import com.fsm.models.Code;
import com.fsm.models.Dispatch;
import com.fsm.models.DispatchReport;
import com.fsm.models.History;
import com.fsm.models.Users;
import com.fsm.repositories.CodeRepository;
import com.fsm.repositories.DispatchReportRepository;
import com.fsm.repositories.DispatchRepository;
import com.fsm.repositories.HistoryRepository;
import com.fsm.repositories.UsersRepository;

import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidExpiresRangeException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.XmlParserException;
import lombok.Data;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("riwayat")
public class RiwayatRepositoryController extends Minio {

	@Autowired
	private HistoryRepository historyRepository;

	@Autowired
	private CodeRepository codeRepository;

	@Autowired
	private DispatchReportRepository dispatchReportRepository;

	@Autowired
	private DispatchRepository dispatchRepository;

	@Autowired
	private UsersRepository usersRepository;

	@GetMapping("/list/status")
	public ArrayList<HashMap<String, Object>> listOfStatus() {
		ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> data = new HashMap<String, Object>();
		for (Code code : codeRepository.findByCodeByCategoryCodeId(Long.valueOf(8))) {
			data.put("key", code.getCodeId());
			data.put("codeName", code.getCodeName());
			result.add(data);
			data = new HashMap<String, Object>();
		}
		data.put("key", "");
		data.put("codeName", "All");
		result.add(data);

		return result;
	}

	@PostMapping("/list/{userId}")
	public HashMap<String, Object> getRiwayat(@PathVariable(value = "userId") Long userId,
			@Valid @RequestBody RiwayatBody riwayat) throws SQLException, InvalidKeyException, ErrorResponseException,
			IllegalArgumentException, InsufficientDataException, InternalException, InvalidBucketNameException,
			InvalidExpiresRangeException, InvalidResponseException, NoSuchAlgorithmException, XmlParserException,
			InvalidEndpointException, InvalidPortException, IOException {
		HashMap<String, Object> result = new HashMap<String, Object>();
		Users user = usersRepository.findById(userId).get();
		String urlImage = null;
		if (user.getUserImage() != null) {
			urlImage = minio().presignedGetObject(bucketName, user.getUserImage());
		}
		result.put("nama", user.getUserFullName());
		result.put("profession", user.getRoleId().getRoleName());
		result.put("total", dispatchRepository.getListDispatchByUserId(userId).size());
		result.put("rating", getTotalRating(userId));
		result.put("image", urlImage);
		result.put("data", listData(userId, riwayat));

		return result;
	}

	public ArrayList<HashMap<String, Object>> listData(@PathVariable(value = "userId") Long userId,
			@Valid @RequestBody RiwayatBody riwayat) {
		ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		ArrayList<HashMap<String, Object>> listDataOrder = new ArrayList<HashMap<String, Object>>();
		ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> data = new HashMap<String, Object>();
		ArrayList<Date> allDate = new ArrayList<Date>();

		for (History history : historyRepository.getRiwayatByUserId(userId, riwayat.startDate, riwayat.endDate,
				riwayat.keywords, riwayat.status)) {
			Date date = history.getOrderId().getDispatchDate();
			if (!allDate.contains(date)) {
				allDate.add(date);
			}

			data = convertHistoryToHashMap(history);
			listData.add(data);
			data = new HashMap<String, Object>();
		}

		for (Date date : allDate) {
			for (HashMap<String, Object> map : listData) {
				if (map.get("dispatchDate").equals(date)) {
					listDataOrder.add(map);
				}
			}
			data.put("tanggal", date);
			data.put("data", listDataOrder);
			result.add(data);
			data = new HashMap<String, Object>();
			listDataOrder = new ArrayList<HashMap<String, Object>>();
		}

		return result;
	}

	private HashMap<String, Object> convertHistoryToHashMap(History history) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		String status;
		int rating;
		DispatchReport dispatchReport = dispatchReportRepository.findByOrderId(history.getOrderId().getOrderId());
		if (dispatchReport == null) {
			rating = 0;
		} else {
			rating = dispatchReport.getDispatchReportRating();
		}

		if (history.getDispatchStatus().equalsIgnoreCase("Start")) {
			status = "On Progress";
		} else {
			status = history.getDispatchStatus();
		}

		long priorityId = history.getOrderId().getTicketId().getPriorityId();
		data.put("orderId", history.getOrderId().getOrderId());
		data.put("ticketTitle", history.getOrderId().getTicketId().getTicketTitle());
		data.put("companyName", history.getOrderId().getTicketId().getBranchId().getCompanyId().getCompanyName());
		data.put("dispatchDate", history.getOrderId().getDispatchDate());
		data.put("dispatchTime", history.getOrderId().getDispatchTime());
		data.put("priority", codeRepository.findById(priorityId).orElse(null).getCodeName());
		data.put("status", status);
		data.put("rating", rating);
		return data;
	}

	public Double getTotalRating(Long userId) {
		Double totalRating = 0.0;
		int counter = 0;
		int rating = 0;
		for (Dispatch dispatch : dispatchRepository.getListDispatchByUserId(userId)) {
			DispatchReport dispatchReport = dispatchReportRepository.findByOrderId(dispatch.getOrderId());
			if (dispatchReport != null) {
				counter++;
				rating += dispatchReport.getDispatchReportRating();
			}
		}
		totalRating = (double) Math.round((Double.valueOf(rating) / Double.valueOf(counter)) * 10) / 10;
		return totalRating;
	}

	@Data
	@NoArgsConstructor
	private static class RiwayatBody {
		private String status;
		private String keywords;
		private Date startDate;
		private Date endDate;

	}
}
