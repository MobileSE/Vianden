package org.vianden.report;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class LatexFigureReport 
{
	private int startRowNum = 1;
	
	public String toTrendFigure(Sheet sheet, int yearColumn, int checkedColumn, String figureName, String venueType)
	{
		StringBuilder output = new StringBuilder();
		
		Map<String, Integer> trendMap = null;
		if (null != venueType)
		{
			trendMap = calculateFigureData(sheet, yearColumn, checkedColumn, venueType);
		}
		else
		{
			trendMap = calculateFigureData(sheet, yearColumn, checkedColumn, 1);
		}
		
		output.append("#! /usr/bin/env Rscript" + "\n");
		output.append("pdf(\"fig_" + figureName + ".pdf\", width=7, height=5)" + "\n");
		output.append("par(mar=c(2,2,0.2,0.2)+0.1, mgp=c(-1,1,0))" + "\n");
		
		SortedSet<String> sortedYears = new TreeSet<String>(trendMap.keySet());
		
		/*
		for (int year = 2011; year < 2016; year++)
		{
			if (! sortedYears.contains(year + ""))
			{
				sortedYears.add(year + "");
				trendMap.put(year + "", 0);
			}
		}*/
		
		String years = "";
		String values = "";
		boolean first = true;
		
		for (String year : sortedYears)
		{
			if (first)
			{			
				years = "\"" + year + "\"";
				values = "" + trendMap.get(year);
				first = false;
			}
			else
			{
				values += ", " + trendMap.get(year);
				years += ", " + "\"" + year + "\"";
			}
		}
		
		output.append("slices <- c(" + values + ")" + "\n");
		output.append("lbls <- c(" + years + ")" + "\n");
		output.append("barplot(slices, col=gray.colors(length(lbls)), names.arg=lbls)" + "\n");
		
		return output.toString();
	}
	
	public Map<String, Integer> calculateFigureData(Sheet sheet, int yearColumn, int checkedColumn, int checkedValue)
	{
		Map<String, Integer> trendMap = new HashMap<String, Integer>();
		
		for (int rowNum = startRowNum; rowNum <= sheet.getLastRowNum(); rowNum++)
		{
			Row row = sheet.getRow(rowNum);
			if (null != row)
			{
				String year = "" + (int) row.getCell(yearColumn).getNumericCellValue();
				Cell c = row.getCell(checkedColumn);
				if (null != c && !c.toString().isEmpty())
				{
					int v = (int) c.getNumericCellValue();
					
					if (v == checkedValue)
					{
						if (trendMap.containsKey(year))
						{
							trendMap.put(year, trendMap.get(year)+1);
						}
						else
						{
							trendMap.put(year, 1);
						}
					}
				}
			}
		}
		
		return trendMap;
	}
	
	public Map<String, Integer> calculateFigureData(Sheet sheet, int yearColumn, int checkedColumn, String checkedValue)
	{
		Map<String, Integer> trendMap = new HashMap<String, Integer>();
		
		for (int rowNum = startRowNum; rowNum <= sheet.getLastRowNum(); rowNum++)
		{
			Row row = sheet.getRow(rowNum);
			if (null != row)
			{
				String year = "" + (int) row.getCell(yearColumn).getNumericCellValue();
				Cell c = row.getCell(checkedColumn);
				if (null != c && !c.toString().isEmpty())
				{
					String v = c.getStringCellValue();
					
					if (checkedValue.equals(v))
					{
						if (trendMap.containsKey(year))
						{
							trendMap.put(year, trendMap.get(year)+1);
						}
						else
						{
							trendMap.put(year, 1);
						}
					}
				}
			}
		}
		
		return trendMap;
	}
	

	public int getStartRowNum() {
		return startRowNum;
	}

	public void setStartRowNum(int startRowNum) {
		this.startRowNum = startRowNum;
	}
}
