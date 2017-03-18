package util;

import org.apache.poi.hssf.usermodel.HSSFCell;

public class StatSchieber {

    private final static String STATISTIK = "Statistik";
    private static int startColumn = 4;
    private static ExcelHandle myExl;
    public static String firstTenant = "";

    public static void copyAll(ExcelHandle exl) {
	int selected = exl.getSelectedSheetIndex();
	setMyExl(exl);
	firstTenant = exl.selectSheet(2).getSheet().getSheetName();
	int n = exl.getWorkbook().getNumberOfSheets();
	exl.selectSheet(STATISTIK);

	for (int i = 1; i < n - 2; i++) {

	    exl.copyColumn(startColumn, startColumn + i, 2);
	    String tenant = exl.selectSheet(i + 2).getSheet().getSheetName();
	    exl.selectSheet(STATISTIK);
	    replaceTenantName(startColumn + i, firstTenant, tenant);

	}

	exl.selectSheet(selected);
    }

    private static void replaceTenantName(int col, String old, String newName) {
	for (HSSFCell cell : myExl.getColumn(col)) {
	    if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
		cell.setCellFormula(cell.getCellFormula().replaceAll(old, newName));
	    }
	}
    }

    public static void setMyExl(ExcelHandle myExl) {
	StatSchieber.myExl = myExl;
    }

}
