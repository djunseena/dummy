package com.fsm.repositories.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.fsm.models.TroubleTicket;
import com.fsm.repositories.ClientCompanyBranchRepository;
import com.fsm.repositories.ClientCompanyPICRepository;
import com.fsm.repositories.ClientCompanyRepository;
import com.fsm.repositories.CodeRepository;
import com.fsm.repositories.JobCategoryRepository;
import com.fsm.repositories.JobClassRepository;
import com.fsm.repositories.JobRepository;
import com.fsm.repositories.SLARepository;
import com.fsm.repositories.TroubleTicketRepository;

@RestController
@RequestMapping("/import/")
public class ListTicketFromImportRepositoryController {

	@Autowired
	CodeRepository codeRepository;

	@Autowired
	JobRepository jobRepository;

	@Autowired
	JobClassRepository jobClassRepository;

	@Autowired
	JobCategoryRepository jobCategoryRepository;

	@Autowired
	ClientCompanyRepository clientCompanyRepository;

	@Autowired
	ClientCompanyBranchRepository clientCompanyBranchRepository;

	@Autowired
	ClientCompanyPICRepository clientCompanyPICRepository;

	@Autowired
	SLARepository slaRepository;

	@Autowired
	TroubleTicketRepository troubleTicketRepository;

	@PostMapping("/troubleTicket/")
	public Map<String, Object> ListDataTicket(@RequestParam("file") MultipartFile file)
			throws IllegalStateException, IOException, SQLException {
		Map<String, Object> showHashMap = new HashMap<>();
		List<Map<String, Object>> listDataValid = new ArrayList<>();
		List<Map<String, Object>> listDataNonValid = new ArrayList<>();
		POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow row;
		DataFormatter formatter = new DataFormatter();
		try {
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				int a = 0;
				row = sheet.getRow(i);
				if (row.getLastCellNum() > 11) {
					a = 1;
				}
				Map<String, Object> dataValid = new HashMap<>();
				Map<String, Object> dataNonValid = new HashMap<>();
				boolean isCompanyValid = false;
				boolean isBranchValid = false;
				boolean isPICValid = false;
				boolean isCodeTicketValid = false;
				boolean isCodePriorityValid = false;
				boolean isJobClassValid = false;
				boolean isJobCategoryValid = false;
				boolean isJobValid = false;
				boolean isDurationValid = false;
				boolean isTitleValid = false;
				boolean isDescriptionValid = false;
				boolean isDuplicate = false;
				Long branchId = null;
				Long picId = null;
				Long codeIdforTicketStatus = null;
				Long jobId = null;
				Long codeIdforPriorityStatus = null;
				Long slaId = null;
				
				String rawTitle = formatter.formatCellValue(row.getCell(a));
				a++;
				if (rawTitle == null) {
					rawTitle = "-";
				}
				else if (rawTitle.length() >= 2 && rawTitle.length() <= 125) {
					isTitleValid = true;
				}
				
				String rawCustomer = formatter.formatCellValue(row.getCell(a));
				a++;
				String rawBranch = formatter.formatCellValue(row.getCell(a));
				a++;
				Long companyId = clientCompanyRepository.findIdByName(rawCustomer);
	
				String rawPriority = formatter.formatCellValue(row.getCell(a));
				a++;
				codeIdforPriorityStatus = codeRepository.findIdByCodeNamePriorityStatus(rawPriority);
				if (codeIdforPriorityStatus == null) {
					isCodePriorityValid = false;
				} else if (codeIdforPriorityStatus != null) {
					isCodePriorityValid = true;
				}
	
				String rawPIC = formatter.formatCellValue(row.getCell(a));
				a++;
				if (companyId == null) {
					isCompanyValid = false;
				} else if (companyId != null) {
					isCompanyValid = true;
					branchId = clientCompanyBranchRepository.findIdByName(companyId, rawBranch);
					if (branchId == null) {
						isBranchValid = false;
					} else if (branchId != null) {
						slaId = slaRepository.findIdByBranchId(branchId);
						isBranchValid = true;
						picId = clientCompanyPICRepository.findIdByBranch(branchId, rawPIC);
						if (picId != null) {
							isPICValid = true;
						}
						else {
							isPICValid = false;
						}
					}
				}
	
				String rawCategory = formatter.formatCellValue(row.getCell(a));
				a++;
				codeIdforTicketStatus = codeRepository.findIdByCodeNameTicketCategory(rawCategory);
				if (codeIdforTicketStatus == null) {
					isCodeTicketValid = false;
				} else if (codeIdforTicketStatus != null) {
					isCodeTicketValid = true;
				}
	
				String rawDuration = formatter.formatCellValue(row.getCell(a));
				a++;
				if (NumberUtils.isNumber(rawDuration)) {
					isDurationValid = true;
				}
	
				String rawDescription = formatter.formatCellValue(row.getCell(a));
				a++;
				if (rawDescription == null) {
					rawDescription = "-";
				} else if (rawDescription.length() >= 2 && rawDescription.length() <= 255) {
					isDescriptionValid = true;
				}
				
				String rawJob = formatter.formatCellValue(row.getCell(a));
				a++;
				String rawJobClass = formatter.formatCellValue(row.getCell(a));
				a++;
				String rawJobCategory = formatter.formatCellValue(row.getCell(a));
				a++;
				
				Long jobClassId = jobClassRepository.findJobClassIdByName(rawJobClass);
				if (jobClassId == null) {
					isJobClassValid = false;
				} else if (jobClassId != null) {
					isJobClassValid = true;
					Long jobCategoryId = jobCategoryRepository.findJobCategoryIdByNameAndJobClassId(jobClassId, rawJobCategory);
					if (jobCategoryId == null) {
						isJobCategoryValid = false;
					} else if (jobCategoryId != null) {
						isJobCategoryValid = true;
						jobId = jobRepository.findJobIdByNameAndJobCategoryId(jobCategoryId, rawJob);
						if (jobId == null) {
							isJobValid = false;
						} else if (jobId != null) {
							isJobValid = true;
						}
					}
				}
				
				TroubleTicket ticketEntity = troubleTicketRepository.validationImport(rawTitle, rawDescription);
				if (ticketEntity != null) {
					isDuplicate = true;
				} else if (ticketEntity == null) {
					isDuplicate = false;
				}
	
				if (isDuplicate || !isDescriptionValid || !isCodePriorityValid || !isTitleValid || !isDurationValid
						|| !isCompanyValid || !isBranchValid || !isPICValid || !isCodeTicketValid || !isJobClassValid
						|| !isJobCategoryValid || !isJobValid) {
					ArrayList<String> dataTidakValid = new ArrayList<>();
					dataNonValid.put("No", i);
					dataNonValid.put("Title", rawTitle.trim());
					dataNonValid.put("Customer", rawCustomer.trim());
					dataNonValid.put("Branch", rawBranch.trim());
					if (isDuplicate) {
						dataTidakValid.add("Data Sudah Ada");
					} else {
						if (!isTitleValid) {
							dataTidakValid.add("Pengisian kolom Title tidak boleh melebihi 125 karakter");
						}
						if (!isCompanyValid) {
							dataTidakValid.add("Customer "+rawCustomer.trim() +" tidak terdaftar di tabel customer");
						}
						if (!isBranchValid) {
							dataTidakValid.add("Branch "+ rawBranch.trim() +" tidak terdaftar di tabel branch");
						}
						if (!isPICValid) {
							dataTidakValid.add("PIC "+ rawPIC.trim() +" tidak terdaftar di branch "+rawBranch);
						}
						if (!isCodeTicketValid) {
							dataTidakValid.add("Category "+ rawCategory.trim() +" tidak terdaftar di tabel code. Seharusnya: Task/Incident/Request");
						}
						if (!isDurationValid) {
							dataTidakValid.add("Pengisian kolom Duration harus berupa angka");
						}
						if (!isDescriptionValid) {
							dataTidakValid.add("Pengisian kolom Description tidak boleh melebihi 255 karakter");
						}
						if (!isJobValid) {
							dataTidakValid.add("Job "+ rawJob.trim() +" tidak terdaftar di tabel job");
						}
						if (!isJobCategoryValid) {
							dataTidakValid.add("Job Categoty "+ rawJobCategory.trim() +" tidak terdaftar di tabel job category");
						}
						if (!isJobClassValid) {
							dataTidakValid.add("Job Class "+ rawJobClass.trim() +" tidak terdaftar di tabel job class");
						}
					}
					dataNonValid.put("Informasi Data Non Valid", dataTidakValid);
					listDataNonValid.add(dataNonValid);
					showHashMap.put("Data Non Valid", listDataNonValid);
				} else {
					dataValid.put("No", i);
					dataValid.put("SLA Id", slaId);
					dataValid.put("Priority Id", codeIdforPriorityStatus);
					dataValid.put("Branch Id", branchId);
					dataValid.put("PIC Id", picId);
					dataValid.put("Code Id", codeIdforTicketStatus);
					dataValid.put("Job Id", jobId);
					dataValid.put("Title", rawTitle.trim());
					dataValid.put("Customer", rawCustomer.trim());
					dataValid.put("Branch", rawBranch.trim());
					dataValid.put("Priority", rawPriority.trim());
					dataValid.put("PIC", rawPIC.trim());
					dataValid.put("Category", rawCategory.trim());
					dataValid.put("Duration", rawDuration);
					dataValid.put("Description", rawDescription.trim());
					dataValid.put("Job", rawJob.trim());
					dataValid.put("Job Category", rawJobCategory.trim());
					dataValid.put("Job Class", rawJobClass.trim());
					listDataValid.add(dataValid);
					showHashMap.put("Data Valid", listDataValid);
				}
			}
		}catch (Exception e) {
			showHashMap.put("Status", HttpStatus.BAD_REQUEST);
			showHashMap.put("Message", e.getMessage());
		}
		wb.close();
		file.getInputStream().close();
		return showHashMap;
	}
}