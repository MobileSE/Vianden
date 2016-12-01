package org.vianden.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.vianden.model.Paper;

public class ConvertTool {

	/**
	 * Convert paper xls file to List<Papers>
	 * e.g. xlsPath = "~/Desktop/test.xls"
	 * @param xlsxPath path of xls
	 * @return List<Paper> list of papers
	 * */
	@SuppressWarnings("resource")
	public static List<Paper> xlsToPaperList(String xlsxPath){
		List<Paper> papers = new ArrayList<Paper>();
		
		try {
			//read from path
			FileInputStream is = new FileInputStream(new File(xlsxPath));
			HSSFWorkbook workBook = new HSSFWorkbook(is);
			HSSFSheet sheet = workBook.getSheet("Papers");
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
				paper.setPublisherByDoi();
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
	 * e.g. xlsPath = "~/Desktop/test.xls"
	 * @param papers list of papers
	 * @return true if success, else false
	 * */
	@SuppressWarnings("resource")
	public static boolean paperToXls(Set<Paper> papers, String xlsPath){
		HSSFWorkbook workBook = new HSSFWorkbook();
		HSSFSheet sheet = workBook.createSheet("Papers");
		HSSFRow row = sheet.createRow(0);
		row.createCell(0).setCellValue("Title");
		row.createCell(1).setCellValue("Authors");
		row.createCell(2).setCellValue("Year");
		row.createCell(3).setCellValue("Doi");
		row.createCell(4).setCellValue("Venue");
		row.createCell(5).setCellValue("Publisher");
		row.createCell(6).setCellValue("Abstract");
		row.createCell(7).setCellValue("Pages");
		row.createCell(8).setCellValue("Email");
		row.createCell(9).setCellValue("Keywords");
		row.createCell(10).setCellValue("PdfUrl");
		row.createCell(11).setCellValue("References");
		
		int rowNumber = 1;
		for (Paper paper : papers)
		{
			row = sheet.createRow(rowNumber);
			row.createCell(0).setCellValue(paper.getTitle());
			row.createCell(1).setCellValue(paper.getAllAuthors());
			row.createCell(2).setCellValue(paper.getYear());
			row.createCell(3).setCellValue(paper.getDoi());
			row.createCell(4).setCellValue(paper.getVenue());
			row.createCell(5).setCellValue(paper.getPublisherName());
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
			output = new FileOutputStream(xlsPath);
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
			case HSSFCell.CELL_TYPE_STRING: str = cell.getStringCellValue();
			break;
			case HSSFCell.CELL_TYPE_NUMERIC: str = String.valueOf(cell.getNumericCellValue());
			break;
			case HSSFCell.CELL_TYPE_FORMULA: str = String.valueOf(cell.getCellFormula());
			break;
			case HSSFCell.CELL_TYPE_ERROR: str = String.valueOf(cell.getErrorCellValue());
			break;
			case HSSFCell.CELL_TYPE_BOOLEAN: str = String.valueOf(cell.getBooleanCellValue());
			break;
			case HSSFCell.CELL_TYPE_BLANK: str = "";
			}
		}
		
		return str;
	}
}
