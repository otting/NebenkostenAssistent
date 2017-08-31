package db;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.DataType;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import gui.ErrorHandle;
import gui.FileOpen;

public class DbHandle {

	/**
	 * has to be initialized to make this class work
	 */
	public static Database mainDB = null;

	public static Table getTable(String tbl) {
		if (mainDB == null) {
			openMainDB();
		}
		Table t = null;
		try {
			t = mainDB.getTable(tbl);
		} catch (IOException e) {
			error("Could not open table \"" + tbl + "\"");
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * Opens a database for use of all mayor actions (only one db can be declared as
	 * main db) This needs to be the database including all important information
	 * about tenants, flats and so on
	 * 
	 * @param path
	 *            File that includes the path of a database
	 */
	public static void openMainDB(File f) {
		if (mainDB != null) {
			try {
				mainDB.close();
			} catch (IOException e) {
				ErrorHandle.popUp("Datenbank konnte nicht geöffnet werden");
				e.printStackTrace();
			}
		}
		mainDB = openDB(f);
	}

	/**
	 * opens a file search window
	 * 
	 * @return database object
	 */
	public static Database openMainDB() {

		File f = FileOpen.getLast();
		if (f != null)
			return (mainDB = openDB(f));
		else
			return (mainDB = openDB(FileOpen.open()));
	}

	/**
	 * 
	 * @param file
	 * @return the Database represented in "file"
	 */
	private static Database openDB(File file) {
		if (file == null)
			throw new NullPointerException("the parameter 'file' is null");
		Database db = null;
		try {
			db = DatabaseBuilder.open(file);
		} catch (IOException e) {
			error("Could not open database at " + file.getAbsolutePath());
			e.printStackTrace();
		}
		return db;
	}

	/**
	 * @param path
	 * @return the database found at the given path
	 */
	public static Database openDB(String path) {
		return openDB(new File(path));
	}

	private static void error(String msg) {
		ErrorHandle.popUp(msg);
	}

	public static Row findId(int id, Table tbl) {
		return findUnique(tbl, tbl.getColumns().get(0).getName(), new Integer(id));
	}

	/**
	 * Used to find a unique (Or first) entry with the given value in a specific
	 * column
	 * 
	 * @param tbl
	 *            table in which the value is looked up
	 * @param column
	 *            name of the column you want to look in
	 * @param value
	 *            the value to look for
	 * @return the first Row containing the value
	 */
	public static <T> Row findUnique(Table tbl, String column, T value) {
		Cursor c;
		try {
			c = CursorBuilder.createCursor(tbl);
			Map<String, T> target = Collections.singletonMap(column, value);
			if (c.findFirstRow(target))
				return c.getCurrentRow();
			else {
				return null;
			}
		} catch (IOException e) {
			ErrorHandle.popUp("Could not access " + tbl.getName());
		}
		return null;

	}

	/**
	 * Searchs for the specified element, returns null if nothing was found.
	 * 
	 * @param tbl
	 * @param column
	 * @param value
	 * @return
	 */
	public static <T> Row search(String tbl, String column, T value) {
		try {
			Cursor c = CursorBuilder.createCursor(getTable(tbl));
			Map<String, T> target = Collections.singletonMap(column, value);
			if (c.findFirstRow(target)) {
				return c.getCurrentRow();
			}
		} catch (IOException e) {
			ErrorHandle.popUp("Could not access table " + tbl);
		}
		return null;
	}

	/**
	 * 
	 * @param table
	 *            name of table
	 * @param col
	 *            column
	 * @param value
	 *            value to look for
	 * @return first row that contains this value
	 */
	public static <T> Row findUnique(String table, String col, T value) {
		return (findUnique(getTable(table), col, value));
	}

	/**
	 * Looks for the last entry for the given parameters
	 * 
	 * @param tbl
	 *            table in which the value is looked up
	 * @param column
	 *            name of the column you want to look in
	 * @param value
	 *            the value to look for
	 * @return the last row containing the value
	 */
	public static <T extends Object> Row findLast(Table tbl, String column, T value) {
		LinkedList<Row> list = findAll(tbl.getName(), column, value);
		if (list != null && !list.isEmpty())
			return list.getLast();
		else
			return null;
	}

	public static <T> Row findLast(String table, String column, T value) {
		Table t = getTable(table);
		return findLast(t, column, value);
	}

	/**
	 * Finds all entrys that contain the given value in the given column. Sorted by
	 * date if available.
	 * 
	 * @param tbl
	 * @param column
	 * @param value
	 * @return a list of rows who contain the searched value (empty if nothing was
	 *         found)
	 */
	public static <T extends Object> LinkedList<Row> findAll(String table, String column, T value) {

		Table tbl = getTable(table);
		LinkedList<Row> list = new LinkedList<Row>();

		try {
			Cursor c = CursorBuilder.createCursor(tbl);
			Map<String, T> target = Collections.singletonMap(column, value);
			while (c.findNextRow(target)) {
				if (c.getCurrentRow() != null) {
					list.add(c.getCurrentRow());
				}
			}
		} catch (IOException e) {
			ErrorHandle.popUp("Error reading " + tbl.getName() + " : " + column);
		}

		String colname = null;
		for (Column col : tbl.getColumns()) {
			if (col.getType().equals(DataType.SHORT_DATE_TIME)) {
				colname = col.getName();
			}
		}
		if (colname != null)
			list.sort(new RowCompare(colname));

		return list;
	}


	/**
	 * Finds all entrys that contain the given value in the given column
	 * 
	 * @param tbl
	 * @param column
	 * @param value
	 * @return a list of rows who contain the searched value (empty if nothing was
	 *         found)
	 */
	public static <T extends Object> LinkedList<Row> findAll(String table, int column, T value) {
		return findAll(table, getTable(table).getColumns().get(column).getName(), value);
	}
}
