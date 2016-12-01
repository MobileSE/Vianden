package org.vianden.report;
import java.io.File;

import mudam.util.CommonUtils;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


public class ToRepackageTables {

	public static void main(String[] args) throws Exception
	{
		String xlsxPath = "/Users/li.li/Project/gitbitbucket/publications/RepackageRep/resources/Android_App_Repackaging.xlsx";
		String outputDir = "/Users/li.li/Project/gitbitbucket/publications/RepackageRep/resources";
		
		LatexTableReport report = new LatexTableReport();
		Workbook wb = WorkbookFactory.create(new File(xlsxPath));
		Sheet sheet = wb.getSheetAt(0);
		
		//for testing categories
		String table_name = "full_list_papers";
		String caption = "Full List of Collected and Examined Papers. (J) stands for journal and (W) stands for workshop papers";
		int[] columns = {3, 2, 5};
		String[] headers = {"Year", "Title", "Venue"};
		
		String tableCtx = report.toSimpleListTable(sheet, 1, 0, 41, columns, caption, headers, table_name, 1);
		CommonUtils.writeResultToFile(outputDir + File.separator + "tab_" + table_name + ".tex", tableCtx);

	}

}
