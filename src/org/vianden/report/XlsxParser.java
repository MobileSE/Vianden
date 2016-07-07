package org.vianden.report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

class Item
{
	public String ref = "";
	public String tool = "";
	public String results = "";
}

public class XlsxParser {

	public static String xlsxPath = "/Users/li.li/Project/gitbitbucket/static_android_analysis_study/slr_total_results_manual_build.xlsx";
	
	public static void main(String[] args) throws Exception
	{
		Workbook wb = WorkbookFactory.create(new File(xlsxPath));
		Sheet sheet = wb.getSheetAt(0);
		
		//static analysis
		//checkColumn(sheet, new int[] {8, 9, 10, 11, 12});
		//dump(sheet, new int[] {8, 9, 10, 11, 12});
		//count(sheet, new int[] {8, 9, 10, 11, 12});
		
		
		//checkColumn(sheet, new int[] {13, 14, 15, 16, 17, 18, 19});
		dump(sheet, new int[] {13, 14, 15, 16, 17, 18, 19});
		count(sheet, new int[] {13, 14, 15, 16, 17, 18, 19});
		
		//android specific
		//checkColumn(sheet, new int[] {18, 19, 20, 21, 22, 23});
		//dump(sheet, new int[] {18, 19, 20, 21, 22, 23});
		//count(sheet, new int[] {18, 19, 20, 21, 22, 23});
		
		
		//security focus
		//checkColumn(sheet, new int[] {24, 25, 26, 27, 28, 29});
		//dump(sheet, new int[] {24, 25, 26, 27, 28, 29});
		//count(sheet, new int[] {24, 25, 26, 27, 28, 29});
	}

	public static boolean isSelected(Row row, int[] columns)
	{
		for (int c : columns)
		{
			Cell cell = row.getCell(c);
			
			String str = "0";
			
			if (null != cell)
			{
				str = cell.toString();
			}
			
			if (str.isEmpty())
			{
				str = "0";
			}
			
			if (str.contains("("))
			{
				str = str.split("\\(")[0].trim();
			}


			double value = Double.parseDouble(str);
			
			if (((int) value) == 1)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static String dump2(Sheet sheet, int[] columns)
	{
		List<String> lines = new ArrayList<String>();
		
		//starting from the second line
		for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++)
		{
			Row row = sheet.getRow(rowNum);
			
			if (row != null)
			{
				if (isSelected(row, columns))
				{
					StringBuilder sb = new StringBuilder();
					
					sb.append(row.getCell(4).toString() + "~\\cite{" + row.getCell(0).toString() + "} ");
					
					for (int c : columns)
					{
						Cell cell = row.getCell(c);
						String str = "0";
						if (cell != null)
						{
							str = cell.toString();
						}
						
						if (str.isEmpty())
						{
							str = "0";
						}
						
						if (str.contains("("))
						{
							str = str.split("\\(")[0].trim();
						}
						
						double value = Double.parseDouble(str);
						
						if (((int) value) == 1)
						{
							sb.append(" & \\cmark ");
						}
						else
						{
							sb.append(" &  ");
						}
					}
					
					lines.add(sb.toString());
				}
			}
		}
		
		int count = 0;
		StringBuilder ctx = new StringBuilder();

		
		for (int i = 0; i < lines.size()/2; i++)
		{
			StringBuilder sb = new StringBuilder();
			
			if (count % 2 == 0)
			{
				sb.append("\\rowcolor{lightgray}    ");
			}
			
			sb.append(lines.get(2*i));
			sb.append("&");
			sb.append(lines.get(2*i+1));
			
			ctx.append(sb.toString() + "    \\\\" + "\n");
			System.out.println(sb.toString() + "    \\\\");
			count++;
		}
		
		if (0 != lines.size() % 2)
		{
			StringBuilder sb = new StringBuilder();
			
			if (count % 2 == 0)
			{
				sb.append("\\rowcolor{lightgray}    ");
			}
			
			sb.append(lines.get(lines.size()-1));
			for (int c : columns)
				sb.append("&");
			
			ctx.append(sb.toString() + "    \\\\" + "\n");
			System.out.println(sb.toString() + "    \\\\");
			count++;
		}
		
		return ctx.toString();
	}
	
	public static String dump(Sheet sheet, int[] columns)
	{
		int count = 0;
		
		StringBuilder ctx = new StringBuilder();
		
		//starting from the second line
		for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++)
		{
			Row row = sheet.getRow(rowNum);
			if (row != null)
			{
				if (isSelected(row, columns))
				{
					StringBuilder sb = new StringBuilder();
					
					if (count % 2 == 0)
					{
						sb.append("\\rowcolor{lightgray}    ");
					}
					
					sb.append(row.getCell(4).toString() + "~\\cite{" + row.getCell(0).toString() + "} ");
					
					for (int c : columns)
					{
						Cell cell = row.getCell(c);
						String str = "0";
						if (cell != null)
						{
							str = cell.toString();
						}
						
						if (str.isEmpty())
						{
							str = "0";
						}
						
						if (str.contains("("))
						{
							str = str.split("\\(")[0].trim();
						}
						
						double value = Double.parseDouble(str);
						
						if (((int) value) == 1)
						{
							sb.append(" & \\cmark ");
						}
						else
						{
							//sb.append(" & \\xmark ");
							sb.append(" &  ");
						}
					}
					
					ctx.append(sb.toString() + "    \\\\" + "\n");
					System.out.println(sb.toString() + "    \\\\");
					count++;
				}
			}
		}
		
		return ctx.toString();
	}
	
	public static String dump(Sheet sheet, int[] columns, int publicAvaliable)
	{
		int count = 0;
		
		StringBuilder ctx = new StringBuilder();
		
		//starting from the second line
		for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++)
		{
			Row row = sheet.getRow(rowNum);
			if (row != null)
			{
				if (isSelected(row, columns))
				{
					Cell cell = row.getCell(publicAvaliable);
					if (null == cell)
					{
						continue;
					}
					
					int cellValue = (int) cell.getNumericCellValue();
					if (1 != cellValue)
					{
						continue;
					}
					
					StringBuilder sb = new StringBuilder();
					
					if (count % 2 == 0)
					{
						sb.append("\\rowcolor{lightgray}    ");
					}
					
					sb.append(row.getCell(4).toString() + "~\\cite{" + row.getCell(0).toString() + "} ");
					
					for (int i = 0; i < columns.length; i++)
					{
						int c = columns[i];
						
						if (i == columns.length-1)
						{
							if (null == row.getCell(c))
							{
								sb.append(" & 0");
							}
							else
							{
								String str = row.getCell(c).toString();
								
								if (str.isEmpty())
								{
									str = "0";
								}
								
								if (str.contains("("))
								{
									str = str.split("\\(")[0].trim();
								}
								
								double value = Double.parseDouble(str);
								
								sb.append(" & " + (int)value);
							}
						}
						else
						{
							String str = row.getCell(c).toString();
							
							if (str.isEmpty())
							{
								str = "0";
							}
							
							if (str.contains("("))
							{
								str = str.split("\\(")[0].trim();
							}
							
							double value = Double.parseDouble(str);
							
							if (((int) value) == 1)
							{
								sb.append(" & \\cmark ");
							}
							else
							{
								//sb.append(" & \\xmark ");
								sb.append(" & ");
							}
						}
					}
					
					ctx.append(sb.toString() + "    \\\\" + "\n");
					System.out.println(sb.toString() + "    \\\\");
					count++;
				}
			}
		}
		
		return ctx.toString();
	}
	
	public static String[] checkColumn(Sheet sheet, int[] columns)
	{
		String[] header = new String[columns.length];
		
		Row row = sheet.getRow(0);
		for (int i = 0; i < columns.length; i++)
		{
			System.out.println(row.getCell(columns[i]));
			header[i] = "" + row.getCell(columns[i]);
		}
		
		return header;
	}
	
	public static String count(Sheet sheet, int[] columns)
	{		
		StringBuilder sb = new StringBuilder();
		sb.append("\\textbf{Total}");
		
		for (int i = 0; i < columns.length; i++)
		{
			int column = columns[i];
			
			int count = 0;
			
			for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++)
			{
				Row row = sheet.getRow(rowNum);
			
				if (rowNum == 0)
				{
					continue;
				}
				
				if (row == null)
				{
					continue;
				}
				
				Cell c = row.getCell(column);
				
				String v = "0";
				if (null != c)
				{
					v = c.toString().toLowerCase();
				}
				
				//String v = c.toString().toLowerCase();
				v = v.replace("\n", "");
				if (v.contains("("))
				{
					v = v.split("\\(")[0].trim();
				}
				
				if (v.contains("1"))
				{
					count++;
				}
			}
			
			sb.append("  &  " + "\\textbf{" + count + "}");
		}
		
		System.out.println(sb.toString() + "    \\\\");
		return sb.toString() + "    \\\\";
	}
	
	public static String count2(Sheet sheet, int[] columns)
	{		
		StringBuilder sb = new StringBuilder();
		sb.append("\\hline" + "\n");
		sb.append("\\multicolumn{" + (columns.length+2) + "}{c}{\\textbf{Total}}");
		
		for (int i = 0; i < columns.length; i++)
		{
			int column = columns[i];
			
			int count = 0;
			
			for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++)
			{
				Row row = sheet.getRow(rowNum);
			
				if (rowNum == 0)
				{
					continue;
				}
				
				if (row == null)
				{
					continue;
				}
				
				Cell c = row.getCell(column);
				
				String v = "0";
				if (null != c)
				{
					v = c.toString().toLowerCase();
				}
				
				//String v = c.toString().toLowerCase();
				v = v.replace("\n", "");
				if (v.contains("("))
				{
					v = v.split("\\(")[0].trim();
				}
				
				if (v.contains("1"))
				{
					count++;
				}
			}
			
			sb.append("  &  " + "\\textbf{" + count + "}");
		}
		
		System.out.println(sb.toString() + "    \\\\");
		return sb.toString() + "    \\\\";
	}
}
