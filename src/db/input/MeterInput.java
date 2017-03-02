package db.input;

import java.util.Calendar;
import java.util.Date;

import com.healthmarketscience.jackcess.Row;

import db.DbHandle;
import db.Flat;
import db.Meter;
import gui.ErrorHandle;

public class MeterInput implements db.DbNames {

    /**
     * adds a value for an existing meter
     * 
     * @param meter
     * @param date
     * @param value
     * @return false if adding failed or there is a newer entry
     */
    public static boolean addValue(Meter meter, Date date, double value) {

	Date lastEntry = meter.getLastEntryDate();
	boolean isNewest = true;
	if (lastEntry.after(date)) {
	    if (!ErrorHandle.askYesNo("Es existiert schon ein aktuellerer Eintrag\ntrotzdem einfügen?"))
		return false;
	} else if (value < meter.getLastEntryValue()) {
	    ErrorHandle.popUp("Der angegebene Wert ist kleiner als der letzte!");
	    return false;
	}

	Row r;
	if (meter.getLastEntryValue() == 0.1) {
	    r = meter.getLastEntry();
	    DbInput.removeRow(METER_VALUES_TABLE, r);
	}
	r = meter.getEntryFor(date);
	if (r != null) {
	    if (ErrorHandle.askYesNo("Es existiert bereits ein Eintrag für dieses Datum\nÜberschreiben?"))
		DbInput.removeRow(METER_VALUES_TABLE, r);
	    else {
		return false;
	    }
	}

	DbInput.addRow(METER_VALUES_TABLE,
		new String[] { METER_VALUES_METER_ID, METER_VALUES_DATE, METER_VALUES_VALUE }, meter.getId(), date,
		value);
	return isNewest;
    }

    /**
     * Add a meter that is <b>NOT</b> a mainMeter to the db
     * 
     * @param id
     * @param flat
     * @param kind
     * @return new Meter
     */
    public static Meter addMeter(String id, Flat flat, int kind) {
	return addMeter(id, flat, kind, false);
    }

    /**
     * Adds a meter to the db
     * 
     * @param id
     * @param flat
     * @param kind
     * @param mainMeter
     * @return new Meter
     */
    public static Meter addMeter(String id, Flat flat, int kind, boolean mainMeter) {
	if (hasDouble(id)) {
	    ErrorHandle.popUp("The Serialnumber is already used");
	    return null;
	}
	DbInput.addRow(METER_TABLE, new String[] { METER_ID, METER_FLAT, METER_KIND, METER_MAINCOUNTER }, id,
		flat.getId(), kind, mainMeter);
	Meter m = new Meter(id, flat);
	addValue(m, Calendar.getInstance().getTime(), 0.1);
	return m;
    }

    private static boolean hasDouble(String id) {
	return (DbHandle.search(METER_TABLE, METER_ID, id) != null);
    }
}
