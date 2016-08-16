package org.vianden.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.vianden.model.Paper;

public class ConvertTool {

	/**
	 * Convert paper xls file to List<Papers>
	 * @param xlsxPath path of xls
	 * @return List<Paper> list of papers
	 * */
	@SuppressWarnings("resource")
	public static List<Paper> XlsToPaperList(String xlsxPath, String xlsName){
		List<Paper> papers = new ArrayList<Paper>();
		
		try {
			//read from path
			FileInputStream is = new FileInputStream(new File(xlsxPath + xlsName));
			HSSFWorkbook workBook = new HSSFWorkbook(is);
			HSSFSheet sheet = workBook.getSheet("papers");
			int rowLength = sheet.getPhysicalNumberOfRows();
			
			//read papers from xls
			for(int i=1; i<rowLength; ++i){
				HSSFRow row = sheet.getRow(i);
				Paper paper = new Paper();
				paper.setTitle(readCell(row.getCell(0)));
				paper.setAllAuthors(readCell(row.getCell(1)));
				paper.setYear(readCell(row.getCell(2)));
				paper.setDoi(readCell(row.getCell(3)));
				paper.setVenue(readCell(row.getCell(4)));
				
				float pubInt = Float.valueOf(readCell(row.getCell(5)));
				paper.setPublisher((int)pubInt);
				paper.setAbstract(readCell(row.getCell(6)));
				paper.setPages(readCell(row.getCell(7)));
				paper.setEmail(readCell(row.getCell(8)));
				paper.setKeywords(readCell(row.getCell(9)));
				paper.setPdfUrl(readCell(row.getCell(10)));
				paper.setAllReference(readCell(row.getCell(11)));
				
				papers.add(paper);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return papers;
	}
	
	/**
	 * Convert List<Papers> to xls file
	 * @param papers list of papers
	 * @param xlsName the name of xls file
	 * @return true if success, else false
	 * */
	@SuppressWarnings("resource")
	public static boolean PaperListToXls(List<Paper> papers, String xlsPath, String xlsName){
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
		row.createCell(11).setCellValue("references");
		Iterator<Paper> it = papers.iterator();
		int rowNumber = 1;
		while (it.hasNext()) {
			Paper paper = it.next();
			row = sheet.createRow(rowNumber);
			row.createCell(0).setCellValue(paper.getTitle());
			row.createCell(1).setCellValue(paper.getAllAuthors());
			row.createCell(2).setCellValue(paper.getYear());
			row.createCell(3).setCellValue(paper.getDoi());
			row.createCell(4).setCellValue(paper.getVenue());
			row.createCell(5).setCellValue(paper.getPublisher());
			row.createCell(6).setCellValue(paper.getAbstract());
			row.createCell(7).setCellValue(paper.getPages());
			row.createCell(8).setCellValue(paper.getEmail());
			row.createCell(9).setCellValue(paper.getKeywords());
			row.createCell(10).setCellValue(paper.getPdfUrl());
			row.createCell(11).setCellValue(paper.getAllReference());
			rowNumber ++;
		}
		
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(xlsPath + xlsName);
			workBook.write(output);
			output.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
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
		
		return true;
	}
	
	/**
	 * read xls cell according to its type
	 * @return string ,cell value
	 * */
	private static String readCell(HSSFCell cell){
		String str = "";
		
		if(cell!=null){
			switch(cell.getCellType()){
			case XSSFCell.CELL_TYPE_STRING:str = cell.getStringCellValue();
			break;
			case XSSFCell.CELL_TYPE_NUMERIC:str = String.valueOf(cell.getNumericCellValue());
			break;
			case XSSFCell.CELL_TYPE_FORMULA:str = String.valueOf(cell.getCellFormula());
			break;
			case XSSFCell.CELL_TYPE_ERROR:str = String.valueOf(cell.getErrorCellValue());
			break;
			case XSSFCell.CELL_TYPE_BOOLEAN:str = String.valueOf(cell.getBooleanCellValue());
			break;
			case XSSFCell.CELL_TYPE_BLANK:str = "";
			}
		}
		
		return str;
	}
}
