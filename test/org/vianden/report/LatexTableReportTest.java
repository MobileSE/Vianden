package org.vianden.report;

import java.io.File;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class LatexTableReportTest {

	public static void main(String[] args) throws EncryptedDocumentException, InvalidFormatException, IOException
	{
		String xlsxPath = "res/primary_publications.xlsx";
		
		LatexTableReport report = new LatexTableReport();
		Workbook wb = WorkbookFactory.create(new File(xlsxPath));
		Sheet sheet = wb.getSheetAt(0);
		
		String tableCtx = report.toSingleColumnTable(sheet, 11, 0, new int[] {15, 16, 17}, true, "Type of testing", new String[] {"White", "Black", "Grey"}, "type_of_testing", 0.6, true);
		
		System.out.println(tableCtx);
	}

}
