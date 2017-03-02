package util;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import gui.ErrorHandle;

public class ExcelHandle {
    private HSSFWorkbook workbook;
    private HSSFSheet sheet;
    private HSSFRow row;
    private HSSFCell cell;
    private String path;

    public ExcelHandle(String path) {
	this.path = path;
	workbook = new HSSFWorkbook();
    }

    /**
     * supposed to create an empty ExcelHandle
     */
    private ExcelHandle() {

    }

    public static ExcelHandle openExel(String path) throws FileNotFoundException, IOException {
	ExcelHandle e = new ExcelHandle();
	e.workbook = new HSSFWorkbook(new FileInputStream(new File(path)));
	e.path = path;
	return e;
    }

    public static ExcelHandle copyExcel(String from, String to) throws FileNotFoundException, IOException {
	return openExel(from).setPath(to);

    }

    public HSSFWorkbook getWorkbook() {
	return workbook;
    }

    public ExcelHandle addSheet(String name) {
	sheet = workbook.createSheet(name);
	return this;
    }

    public ExcelHandle cloneLastSheet() {
	workbook.cloneSheet(workbook.getNumberOfSheets() - 1);
	return this;
    }

    public ExcelHandle selectSheet(String name) {
	sheet = workbook.getSheet(name);
	if (sheet == null)
	    addSheet(name);
	return this;
    }

    public ExcelHandle selectSheet(int index) {
	HSSFSheet sh = workbook.getSheetAt(index);
	if (sh != null)
	    sheet = sh;
	else
	    throw new RuntimeException("Invalid index");
	return this;
    }

    public ExcelHandle selectLastSheet() {
	return selectSheet(workbook.getNumberOfSheets() - 1);
    }

    public ExcelHandle renameSelectedSheet(String name) {
	workbook.setSheetName(workbook.getSheetIndex(sheet), name);
	return this;
    }

    public ExcelHandle removeSelectedSheet() {
	workbook.removeSheetAt(workbook.getSheetIndex(sheet));
	return selectLastSheet();
    }

    public ExcelHandle write(int row, int col, String value) {
	selectCell(row, col);
	if (cell.getCellType() == Cell.CELL_TYPE_FORMULA)
	    cell.setCellFormula(value);
	else
	    cell.setCellValue(value);
	return this;
    }

    public ExcelHandle write(int row, int col, double value) {
	selectCell(row, col).setCellValue(value);
	return this;
    }

    public ExcelHandle write(int row, int col, int value) {
	selectCell(row, col).setCellValue(value);
	return this;
    }

    public ExcelHandle write(int row, int col, Date value) {
	selectCell(row, col).setCellValue(value);
	return this;
    }

    public HSSFCell[] getColumn(int index) {
	HSSFCell[] cells = new HSSFCell[sheet.getLastRowNum()];
	for (int i = 0; i < sheet.getLastRowNum(); i++) {
	    cells[i] = selectCell(i, index);
	}
	return cells;
    }

    public void save() {
	try {
	    HSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
	} catch (Exception e) {
	    System.err.println("Error evaluating formulas");
	}
	FileOutputStream fo;
	File f = new File(path);
	if (f.exists()) {
	    File next = new File(f.getPath() + ".old");
	    if (next.exists()) {
		next.delete();
	    }
	    f.renameTo(next);
	}
	try {
	    fo = new FileOutputStream(f);
	    workbook.write(fo);
	    fo.close();
	    Desktop.getDesktop().open(new File(path));
	} catch (IOException e) {
	    if (ErrorHandle.askYesNo("ExcelWorkbook: " + path + " could not be created\nRetry?")) {
		save();
	    }
	}
    }

    public void replace(String placeholder, String value) {
	if (findCell(placeholder)) {
	    cell.setCellValue(value);
	}
    }

    public void replace(String placeholder, int value) {
	if (findCell(placeholder)) {
	    cell.setCellValue(value);
	}
    }

    public void replace(String placeholder, double value) {
	if (findCell(placeholder)) {
	    cell.setCellValue(value);
	}
    }

    public void replace(String placeholder, Date value) {
	if (findCell(placeholder)) {
	    cell.setCellValue(value);
	}
    }

    public void deleteSheetsAfter(int index) {
	index++;
	while (index < workbook.getNumberOfSheets()) {
	    workbook.removeSheetAt(index);
	}
    }

    public int getSelectedSheetIndex() {
	return workbook.getSheetIndex(sheet);
    }

    private boolean findCell(String placeholder) {
	for (Row r : sheet) {
	    row = sheet.getRow(r.getRowNum());
	    for (Cell c : r) {
		if (c != null)
		    if (c.getCellType() == Cell.CELL_TYPE_STRING && placeholder.equals(c.getStringCellValue())) {
			cell = row.getCell(c.getColumnIndex());
			return true;
		    }
	    }
	}
	ErrorHandle.popUp("Could not find placeholder: " + placeholder + " in " + path + ":" + sheet.getSheetName());
	return false;
    }

    public HSSFCell selectCell(int row, int col) {
	selectRow(row);
	cell = this.row.getCell(col);
	if (cell == null) {
	    cell = this.row.createCell(col, 1);
	    cell.setCellValue("");
	}
	return cell;
    }

    private HSSFRow selectRow(int row) {

	HSSFSheet s = getSheet();
	HSSFRow r = s.getRow(row);
	if (r != null) {
	    this.row = r;
	} else {
	    this.row = s.createRow(row);
	}
	return this.row;
    }

    public ExcelHandle setPath(String path) {
	this.path = path;
	return this;
    }

    public HSSFSheet getSheet() {
	if (sheet != null) {
	    return sheet;
	} else {
	    if (workbook.getNumberOfSheets() > 0) {
		sheet = workbook.getSheetAt(0);
	    } else {
		sheet = workbook.createSheet();
	    }
	    return sheet;
	}
    }

    public void copyColumn(int source, int target) {
	copyColumn(source, target, 0);
    }

    /**
     * 
     * @param source
     *            index of column
     * @param target
     *            index of column
     */
    public void copyColumn(int source, int target, int startRow) {
	for (int i = startRow; i < sheet.getLastRowNum(); i++) {
	    HSSFCell old = selectCell(i, source);
	    HSSFCell newCell = selectCell(i, target);
	    if (newCell == null)
		row.createCell(target, (old != null) ? old.getCellType() : 1);
	    copyCell(old, selectCell(i, target), true);
	}
    }

    public static void copyCell(HSSFCell oldCell, HSSFCell newCell, boolean withStyle) {
	if (oldCell == null)
	    return;

	if (oldCell.getSheet().getWorkbook() == newCell.getSheet().getWorkbook()) {
	    newCell.setCellStyle(oldCell.getCellStyle());
	} else {
	    HSSFCellStyle newCellStyle = newCell.getSheet().getWorkbook().createCellStyle();
	    newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
	    newCell.setCellStyle(newCellStyle);
	}

	switch (oldCell.getCellType()) {
	case HSSFCell.CELL_TYPE_STRING:
	    newCell.setCellValue(oldCell.getStringCellValue());
	    break;
	case HSSFCell.CELL_TYPE_NUMERIC:
	    newCell.setCellValue(oldCell.getNumericCellValue());
	    break;
	case HSSFCell.CELL_TYPE_BLANK:
	    newCell.setCellType(HSSFCell.CELL_TYPE_BLANK);
	    break;
	case HSSFCell.CELL_TYPE_BOOLEAN:
	    newCell.setCellValue(oldCell.getBooleanCellValue());
	    break;
	case HSSFCell.CELL_TYPE_ERROR:
	    newCell.setCellErrorValue(oldCell.getErrorCellValue());
	    break;
	case HSSFCell.CELL_TYPE_FORMULA:
	    try {
		newCell.setCellFormula(oldCell.getCellFormula());
	    } catch (FormulaParseException e) {
		newCell.setCellValue(oldCell.getErrorCellValue());
	    }
	    break;
	default:
	    break;
	}

    }
}
