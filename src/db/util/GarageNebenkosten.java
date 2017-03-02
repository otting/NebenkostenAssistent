package db.util;

import java.util.LinkedList;

import com.healthmarketscience.jackcess.Row;

import db.DbHandle;
import db.DbNames;
import db.input.DbInput;
import gui.ErrorHandle;

public class GarageNebenkosten implements DbNames {

    private int id;
    private String type;
    private int year;
    private double value;
    public static final String OFLW_TYPE = "OflWasser";
    public static final String GRUNDS_TYPE = "Grundsteuer";

    public GarageNebenkosten(int id, String type, double value, int year) {
	setID(id);
	setType(type);
	setValue(value);
	setYear(year);
    }

    public static LinkedList<GarageNebenkosten> loadFor(int year) {
	LinkedList<GarageNebenkosten> list = new LinkedList<>();

	for (Row r : DbHandle.findAll(GARAGE_EXTRA_TABLE, GARAGE_EXTRA_YEAR, year)) {
	    list.add(new GarageNebenkosten(r.getInt(GARAGE_EXTRA_ID), r.getString(GARAGE_EXTRA_TYPE),
		    r.getDouble(GARAGE_EXTRA_VALUE), r.getInt(GARAGE_EXTRA_YEAR)));
	}

	return list;
    }

    public static double loadGrundsteuer(int year) {

	for (GarageNebenkosten gn : loadFor(year)) {
	    if (gn.getType().equals(GRUNDS_TYPE))
		return gn.getValue();
	}
	return 0.0;

    }

    public void save() {
	DbInput.editRow(GARAGE_EXTRA_TABLE, GARAGE_EXTRA_ID, getID(), GARAGE_EXTRA_VALUE, getValue());
    }

    public void delete() {
	Row r = DbHandle.findId(getID(), DbHandle.getTable(GARAGE_EXTRA_TABLE));
	if (!DbInput.removeRow(GARAGE_EXTRA_TABLE, r))
	    if (ErrorHandle.askYesNo("Eintrag konnte nicht gelöscht werden\nErneut versuchen?"))
		delete();
    }

    public static void add(String type, double value, int year) {
	DbInput.addRow(GARAGE_EXTRA_TABLE, new String[] { GARAGE_EXTRA_TYPE, GARAGE_EXTRA_VALUE, GARAGE_EXTRA_YEAR },
		type, value, year);
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public int getYear() {
	return year;
    }

    public void setYear(int year) {
	this.year = year;
    }

    public double getValue() {
	return value;
    }

    public void setValue(double value) {
	this.value = value;
    }

    public int getID() {
	return id;
    }

    public void setID(int id) {
	this.id = id;
    }

    public String toString() {
	return getType() + ": " + getValue();
    }

}
