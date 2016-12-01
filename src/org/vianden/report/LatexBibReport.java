package org.vianden.report;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class LatexBibReport 
{
	public void toBibs(Sheet sheet, int bibIndex, int lastRowNum, int titleIndex, int authorsIndex, int venueIndex, int yearIndex)
	{
		for (int rowNum = 1; rowNum <= lastRowNum; rowNum++)
		{
			Row row = sheet.getRow(rowNum);
			if (null != row)
			{
				StringBuilder sb = new StringBuilder();
				
				String bibID = row.getCell(bibIndex).toString().trim();
				String title = row.getCell(titleIndex).toString().trim();
				String authors = row.getCell(authorsIndex).toString().trim();
				String venue = row.getCell(venueIndex).toString().trim();
				String year = row.getCell(yearIndex).toString().trim();
				
				year = (int) Double.parseDouble(year) + "";
				
				if (venue.contains("Technical Report") || venue.contains("(J)" ))
				{
					if (venue.contains("(J)"))
					{
						venue = venue.replace("(J)", "").trim();
					}
					
					sb.append("@article{" + bibID + "," + "\n");
					sb.append("title={" + title + "}," + "\n");
					sb.append("author={" + authors + "}," + "\n");
					sb.append("journal={" + venue + "}," + "\n");
					sb.append("year={" + year + "}" + "\n");
					sb.append("}" + "\n");
				}
				else
				{
					sb.append("@inproceedings{" + bibID + "," + "\n");
					sb.append("title={" + title + "}," + "\n");
					sb.append("author={" + authors + "}," + "\n");
					sb.append("booktitle={" + venue + "}," + "\n");
					sb.append("year={" + year + "}" + "\n");
					sb.append("}" + "\n");
				}
				
				System.out.println(sb.toString());
			}
		}
	}
	
	
}
