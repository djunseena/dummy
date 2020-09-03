package com.fsm.repositories;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fsm.models.TroubleTicket;

public class ExcelFileExporterRepository {
	public static ByteArrayInputStream listEcxelFile(List<TroubleTicket> ticketings) {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("TroubleTicket");

			Row row = sheet.createRow(0);
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			// Creating header
			Cell cell = row.createCell(0);
			cell.setCellValue("Ticket ID");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(1);
			cell.setCellValue("Ticket Status ID");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(2);
			cell.setCellValue("Category ID");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(3);
			cell.setCellValue("Branch ID");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(4);
			cell.setCellValue("SLA ID");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(5);
			cell.setCellValue("Job ID");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(6);
			cell.setCellValue("PIC ID");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(7);
			cell.setCellValue("Ticket Tittle");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(8);
			cell.setCellValue("Ticket Date");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(9);
			cell.setCellValue("Ticket Time");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(10);
			cell.setCellValue("Ticket Description");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(11);
			cell.setCellValue("Ticket Due Date");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(12);
			cell.setCellValue("Ticket Duration Time");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(13);
			cell.setCellValue("Ticket Code");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(14);
			cell.setCellValue("Created By");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(15);
			cell.setCellValue("Created On");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(16);
			cell.setCellValue("Last Modified By");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(17);
			cell.setCellValue("Last Modified On");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(18);
			cell.setCellValue("Is Deleted");
			cell.setCellStyle(headerCellStyle);

			for (int i = 0; i < ticketings.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				dataRow.createCell(0).setCellValue(ticketings.get(i).toString());
				dataRow.createCell(1).setCellValue(ticketings.get(i).toString());
				dataRow.createCell(2).setCellValue(ticketings.get(i).toString());
				dataRow.createCell(3).setCellValue(ticketings.get(i).toString());
				dataRow.createCell(4).setCellValue(ticketings.get(i).toString());
				dataRow.createCell(5).setCellValue(ticketings.get(i).toString());
				dataRow.createCell(6).setCellValue(ticketings.get(i).toString());
				dataRow.createCell(7).setCellValue(ticketings.get(i).toString());
				dataRow.createCell(8).setCellValue(ticketings.get(i).toString());
				dataRow.createCell(9).setCellValue(ticketings.get(i).toString());
				dataRow.createCell(10).setCellValue(ticketings.get(i).toString());
				dataRow.createCell(11).setCellValue(ticketings.get(i).toString());
				dataRow.createCell(12).setCellValue(ticketings.get(i).toString());
				dataRow.createCell(13).setCellValue(ticketings.get(i).toString());
				dataRow.createCell(14).setCellValue(ticketings.get(i).toString());
				dataRow.createCell(15).setCellValue(ticketings.get(i).toString());
				dataRow.createCell(16).setCellValue(ticketings.get(i).toString());
				dataRow.createCell(17).setCellValue(ticketings.get(i).toString());
				dataRow.createCell(18).setCellValue(ticketings.get(i).toString());
			}

			// Making size of column auto resize to fit with data
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			sheet.autoSizeColumn(7);
			sheet.autoSizeColumn(8);
			sheet.autoSizeColumn(9);
			sheet.autoSizeColumn(10);
			sheet.autoSizeColumn(11);
			sheet.autoSizeColumn(12);
			sheet.autoSizeColumn(13);
			sheet.autoSizeColumn(14);
			sheet.autoSizeColumn(15);
			sheet.autoSizeColumn(16);
			sheet.autoSizeColumn(17);
			sheet.autoSizeColumn(18);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
