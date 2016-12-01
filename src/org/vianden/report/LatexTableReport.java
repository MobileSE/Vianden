package org.vianden.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class LatexTableReport 
{
	private int startRowNum = 1;
	
	/**
	 * Generating latex source code for the given columns in a single column table manner.
	 * 
	 * Workbook wb = WorkbookFactory.create(new File(xlsxPath));
		Sheet sheet = wb.getSheetAt(0);
	 * 
	 * @param sheet	The targeted Sheet, which can be obtained from Workbook object (e.g., workbook.getSheetAt(0)).
	 * @param toolNameIndex	The index of the tool name column.
	 * @param bibIndex The index of the bib text column.
	 * @param columns An integer array presenting the interested columns, where each column (in xlsx) will eventually present as a column in the final latex table.
	 * @param removeEmptyRows If true, we do not consider such publications that have no checked value (e.g., 1) on the given columns. 
	 * @param caption The caption of the latex table
	 * @param headers The header of the latex table, the number and sequence should corresponding to the xlsx columns.
	 * @param tableName The name of the latex table, which will be used for labeling latex tables.
	 * @param lineWidthRate Ranging from 0 to 1, describing the line width rate (e.g., 0.8\linewidth) of the final latex table.
	 * @param usingDoubleColumn	Whether the final latex table will be presented in two columns (if it applys) or not.
	 * @return
	 */
	public String toSingleColumnTable(Sheet sheet, int toolNameIndex, int bibIndex, int[] columns, boolean removeEmptyRows, String caption, String[] headers, String tableName, double lineWidthRate, boolean usingDoubleColumn)
	{
		StringBuilder output = new StringBuilder();
		
		output.append("\\begin{table" + (usingDoubleColumn? "*" : "") + "}[!h]" + "\n");
		output.append("\\centering" + "\n");
		output.append("\\caption{" + caption + "}" + "\n");
		output.append("\\label{tab:" + tableName + "}" + "\n");
		
		String cn = "l";
		for (int i = 0; i < columns.length; i++)
		{
			cn += "l";
		}
		
		output.append("\\resizebox{" + lineWidthRate + "\\linewidth}{!}{" + "\n");
		output.append("\\begin{tabular} { " + cn + " }" + "\n");
		output.append("\\hline" + "\n");
		
		StringBuilder header = new StringBuilder();
		header.append("Tool" + "\n");
		for (int i = 0; i < columns.length; i++)
		{
			header.append(" & " + headers[i]);
		}
		output.append(header.toString() + "\\\\" + " \n");
		
		output.append("\\hline" + "\n");
		
		List<String> tableLines = constructTableLines(sheet, toolNameIndex, bibIndex, columns, removeEmptyRows);
		for (int i = 0; i < tableLines.size(); i++)
		{
			if (i % 2 == 0)
			{
				output.append("\\rowcolor{lightgray}    ");
			}
			
			output.append(tableLines.get(i) + "\\\\" + "\n");
		}
		
		output.append("\\hline" + "\n");
		
		int[] counts = computeCountInfo(sheet, columns);
		
		StringBuilder count = new StringBuilder();
		count.append("Count");
		for (int i = 0; i < counts.length; i++)
		{
			count.append(" & " + counts[i]);
		}
		count.append(" \\\\ " + "\n");
		
		output.append(count.toString());
		
		output.append("\\hline" + "\n");
		output.append("\\end{tabular}" + "\n");
		output.append("}" + "\n");
		output.append("\\end{table" + (usingDoubleColumn? "*" : "") + "}" + "\n");
		
		System.out.println(output.toString());
		return output.toString();
	}
	
	public String toSimpleListTable(Sheet sheet, int toolNameIndex, int bibIndex, int lastRowNum, int[] columns, String caption, String[] headers, String tableName, double lineWidthRate)
	{
		StringBuilder output = new StringBuilder();
		
		output.append("\\begin{table*}[!h]" + "\n");
		output.append("\\centering" + "\n");
		output.append("\\caption{" + caption + "}" + "\n");
		output.append("\\label{tab:" + tableName + "}" + "\n");
		
		String cn = "l";
		for (int i = 0; i < columns.length; i++)
		{
			cn += "l";
		}
		
		output.append("\\resizebox{" + lineWidthRate + "\\linewidth}{!}{" + "\n");
		output.append("\\begin{tabular} { " + cn + " }" + "\n");
		output.append("\\hline" + "\n");
		
		StringBuilder header = new StringBuilder();
		header.append("Tool" + "\n");
		for (int i = 0; i < columns.length; i++)
		{
			header.append(" & " + headers[i]);
		}
		output.append(header.toString() + "\\\\" + " \n");
		
		output.append("\\hline" + "\n");
		
		for (int rowNum = startRowNum; rowNum <= lastRowNum; rowNum++)
		{
			Row row = sheet.getRow(rowNum);
			if (null != row)
			{
				StringBuilder sb = new StringBuilder();
				
				String toolName = row.getCell(toolNameIndex).toString();
				String bibID = row.getCell(bibIndex).toString();
				
				sb.append(toolName + "~\\cite{" + bibID + "}");
				
				for (int i = 0; i < columns.length; i++)
				{
					String str = row.getCell(columns[i]).toString();
				
					if (3 == columns[i])
					{
						str = (int) Double.parseDouble(str) + "";
					}
					
					str = str.replace("&", "\\&");
					
					sb.append("&" + str);
				}
				
				output.append(sb.toString() + "\\\\" + " \n");
			}
		}
		
		output.append("\\hline" + "\n");
		output.append("\\end{tabular}" + "\n");
		output.append("}" + "\n");
		output.append("\\end{table*}" + "\n");
		
		System.out.println(output.toString());
		return output.toString();
	}
	
	public List<String> constructTableLines(Sheet sheet, int toolNameIndex, int bibIndex, int[] columns, boolean removeEmptyRows)
	{
		List<String> tableLines = new ArrayList<String>();
		
		for (int rowNum = startRowNum; rowNum <= sheet.getLastRowNum(); rowNum++)
		{
			Row row = sheet.getRow(rowNum);
			if (null != row)
			{
				if (removeEmptyRows && isEmpty(sheet, rowNum, columns))
				{
					continue;
				}
				
				StringBuilder sb = new StringBuilder();
				
				String toolName = row.getCell(toolNameIndex).toString();
				String bibID = row.getCell(bibIndex).toString();
				
				sb.append(toolName + "~\\cite{" + bibID + "}");
				
				for (int i = 0; i < columns.length; i++)
				{
					int value = 0;
					
					Cell cell = row.getCell(columns[i]);
					if (null != cell && ! cell.toString().isEmpty())
					{
						value = (int) Double.parseDouble(cell.toString());
					}
					
					if (1 == value)
					{
						sb.append("& \\cmark ");
					}
					else
					{
						sb.append("&  ");
					}
				}
				
				tableLines.add(sb.toString());
			}
		}
		
		return tableLines;
	}
	
	public boolean isEmpty(Sheet sheet, int rowNum, int[] columns)
	{
		boolean result = true;
		
		Row row = sheet.getRow(rowNum);
		if (null != row)
		{
			for (int i = 0; i < columns.length; i++)
			{
				int value = 0;
				
				Cell cell = row.getCell(columns[i]);
				if (null != cell && ! cell.toString().isEmpty())
				{
					value = (int) Double.parseDouble(cell.toString());
				}
				
				if (1 == value)
				{
					result = false;
					break;
				}
			}
		}
		
		return result;
	}
	
	public int[] computeCountInfo(Sheet sheet, int[] columns)
	{
		int[] counts = new int[columns.length];
		
		for (int rowNum = startRowNum; rowNum <= sheet.getLastRowNum(); rowNum++)
		{
			Row row = sheet.getRow(rowNum);
			if (null != row)
			{
				for (int i = 0; i < columns.length; i++)
				{
					int value = 0;
					
					Cell cell = row.getCell(columns[i]);
					if (null != cell && ! cell.toString().isEmpty())
					{
						value = (int) Double.parseDouble(cell.toString());
					}
					
					if (1 == value)
					{
						counts[i]++;
					}
					
				}
			}
		}
		
		return counts;
	}

	public int getStartRowNum() {
		return startRowNum;
	}

	public void setStartRowNum(int startRowNum) {
		this.startRowNum = startRowNum;
	}
}
