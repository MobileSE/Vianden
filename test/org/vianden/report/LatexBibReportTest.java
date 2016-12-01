package org.vianden.report;

import java.io.File;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class LatexBibReportTest {

	public static void main(String[] args) throws Exception
	{
		String xlsxPath = "/Users/li.li/Project/gitbitbucket/publications/RepackageRep/resources/Android_App_Repackaging.xlsx";
		
		Workbook wb = WorkbookFactory.create(new File(xlsxPath));
		Sheet sheet = wb.getSheetAt(0);
		
		LatexBibReport lbr = new LatexBibReport();
		lbr.toBibs(sheet, 0, 41, 2, 13, 5, 3);
	}

}
