package util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;

public class StatSchieber {

    private final static String STATISTIK = "Statistik";
    private static int col = 4;
    private static ExcelHandle myExl;
    public static String firstTenant = "";

    public static void copyAll(ExcelHandle exl) {
	int selected = exl.getSelectedSheetIndex();
	myExl = exl;
	firstTenant = exl.selectSheet(2).getSheet().getSheetName();
	int n = exl.getWorkbook().getNumberOfSheets();

	int start = col;
	for (int i = 2; i < n; i++) {

	    exl.copyColumn(start, start + 1, 2);
	    String tenant = exl.selectSheet(i).getSheet().getSheetName();
	    exl.selectSheet(STATISTIK);
	    replaceCol(start + 1, tenant);
	    start++;
	}

	exl.selectSheet(selected);
    }

    private static void replaceCol(int c, String tenant) {

	HSSFCell[] cells = myExl.getColumn(c);
	for (int i = 2; i < cells.length; i++) {
	    if (cells[i] != null)
		try {
		    myExl.write(c, i, replaceString(cells[i], c, tenant));
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
    }

    private static String replaceString(HSSFCell cell, int column, String tenant) {
	if (cell == null)
	    return "";

	char from = (char) (((int) 'A') + col);
	char to = (char) (((int) 'A') + col + 1);

	if (cell.getCellType() != HSSFCell.CELL_TYPE_FORMULA && cell.getCellType() != HSSFCell.CELL_TYPE_STRING) {
	    return cell.getStringCellValue();
	}
	String s;
	if (cell.getCellType() == Cell.CELL_TYPE_FORMULA)
	    s = cell.getCellFormula();
	else
	    s = cell.getStringCellValue();
	String original = s;
	String sub = "";
	String regex = "[" + from + "][0-9]+";
	for (int i = 0; i < s.length() - 2; i++) {
	    sub = s.substring(i, i + 2);
	    if (sub.matches(regex)) {
		s = s.replaceFirst(regex, to + sub.substring(1));
	    }
	}
	s.replaceAll(firstTenant, tenant);
	System.out.println(original + " -> " + s);
	return s;

    }
}
