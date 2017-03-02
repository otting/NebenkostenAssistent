package db.input;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import db.DbHandle;
import gui.ErrorHandle;

public class DbInput {

    /**
     * 
     * @param table
     * @param col
     * @param obj
     *            must have the exact same length of col
     * @return
     */

    public static boolean addRow(String table, String[] col, Object... obj) {
	int i = 0;
	HashMap<String, Object> map = new HashMap<>();
	for (String s : col) {
	    map.put(s, obj[i++]);
	}
	return addRow(table, map);

    }

    public static boolean addRow(String table, Map<String, Object> map) {
	Table tbl = DbHandle.getTable(table);
	try {
	    tbl.addRowFromMap(map);
	    return true;
	} catch (IOException e) {
	    if (ErrorHandle
		    .askYesNo("Konnte nicht in Tabelle " + table + " schreiben\nWollen sie es erneut versuchen?")) {
		return addRow(table, map);
	    } else {
		e.printStackTrace();
	    }
	}
	return false;
    }

    public static boolean editRow(String table, String idColumn, Object id, String targetCol, Object newValue) {
	Table tbl = DbHandle.getTable(table);
	Row r = DbHandle.findUnique(tbl, idColumn, id);
	r.replace(targetCol, newValue);
	try {
	    tbl.updateRow(r);
	    return true;
	} catch (IOException e) {
	    if (ErrorHandle
		    .askYesNo("Konnte nicht in Tabelle " + table + "Schreiben\nWollen sie es erneut versuchen?")) {
		return editRow(table, idColumn, id, targetCol, newValue);
	    }
	}
	return false;
    }

    public static boolean removeRow(String table, String idCol, Object id) {
	Table tbl = DbHandle.getTable(table);
	Row r = DbHandle.search(table, idCol, id);
	if (r != null)
	    return removeRow(tbl, r);
	else
	    return false;
    }

    /**
     * removes every row that contains the given information
     * 
     * @param table
     * @param idCol
     * @param id
     * @param idCol2
     * @param id2
     * @return
     */
    public static boolean removeRow(String table, String idCol, Object id, String idCol2, Object id2) {
	boolean success = true;
	LinkedList<Row> all = DbHandle.findAll(table, idCol, id);
	for (Row r : all) {
	    if (r.get(idCol2).equals(id2)) {
		if (!removeRow(table, r)) {
		    success = false;
		}
	    }
	}
	return success;

    }

    public static boolean removeRow(String table, Row r) {
	if (table == null || r == null)
	    return false;
	Table tbl = DbHandle.getTable(table);
	return removeRow(tbl, r);
    }

    private static boolean removeRow(Table tbl, Row r) {
	if (tbl == null || r == null)
	    return false;
	try {
	    tbl.deleteRow(r);
	    return true;
	} catch (IOException e) {
	    if (ErrorHandle.askYesNo("Konnte nicht auf " + tbl.getName() + " zugreifen\nErneut versuchen?")) {
		return removeRow(tbl, r);
	    } else
		e.printStackTrace();
	}
	return false;
    }
}
