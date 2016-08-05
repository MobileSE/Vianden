package org.vianden;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.vianden.model.Paper;

public class SearchEngineTest {
	public static void main(String[] args) throws Exception {
		SearchEngine se = new SearchEngine();
		// We can delete some papers in which title does not contain the
		// keywords.
		List<Paper> papers = se.search(1950);

		//int count = 0;
		System.out.println(papers.size());
		createSheet_T(papers);
//		for (Paper p : papers)
//		 {
//			 p = se.refine(p);
//		 }
//
//		 createSheet(papers);
	}

	@SuppressWarnings("resource")
	public static void createSheet(List<Paper> papers) {
		HSSFWorkbook workBook = new HSSFWorkbook();
		HSSFSheet sheet = workBook.createSheet("papers");
		HSSFRow row = sheet.createRow(0);
		row.createCell(0).setCellValue("Title");
		row.createCell(1).setCellValue("authors");
		row.createCell(2).setCellValue("year");
		row.createCell(3).setCellValue("doi");
		row.createCell(4).setCellValue("venue");
		row.createCell(5).setCellValue("publisher");
		row.createCell(6).setCellValue("abstract");
		row.createCell(7).setCellValue("pages");
		row.createCell(8).setCellValue("email");
		row.createCell(9).setCellValue("keywords");
		row.createCell(10).setCellValue("pdfUrl");
//		row.createCell(11).setCellValue("references");
		Iterator<Paper> it = papers.iterator();
		int rowNumber = 1;
		while (it.hasNext()) {
			row = sheet.createRow(rowNumber);
			row.createCell(0).setCellValue(it.next().getTitle());
			row.createCell(1).setCellValue(it.next().getAllAuthorsName());
			row.createCell(2).setCellValue(it.next().getYear());
			row.createCell(3).setCellValue(it.next().getDoi());
			row.createCell(4).setCellValue(it.next().getVenue());
			row.createCell(5).setCellValue(it.next().getPublisher());
			row.createCell(6).setCellValue(it.next().getAbstract());
			row.createCell(7).setCellValue(it.next().getPages());
			row.createCell(8).setCellValue(it.next().getEmail());
			row.createCell(9).setCellValue(it.next().getKeywords());
			row.createCell(10).setCellValue(it.next().getPdfUrl());
//			row.createCell(11).setCellValue(it.next().getReferences());
			rowNumber ++;
		}
		
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(System.getProperty("user.dir")
					+ "/res/papers_C_A.xls");
			workBook.write(output);
			output.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
					output = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("resource")
	public static void createSheet_T(List<Paper> papers) {
		HSSFWorkbook workBook = new HSSFWorkbook();
		HSSFSheet sheet = workBook.createSheet("papers");
		HSSFRow row = sheet.createRow(0);
		row.createCell(0).setCellValue("Title");
		Iterator<Paper> it = papers.iterator();
		int rowNumber = 1;
		while (it.hasNext()) {
			row = sheet.createRow(rowNumber);
			row.createCell(0).setCellValue(it.next().getTitle());
			rowNumber ++;
		}
		
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(System.getProperty("user.dir")
					+ "/res/papers_C_A_Title.xls");
			workBook.write(output);
			output.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
					output = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
