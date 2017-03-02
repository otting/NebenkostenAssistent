package db.input;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import com.healthmarketscience.jackcess.Row;

import db.DbHandle;
import db.DbNames;
import db.Flat;
import db.Tenant;
import gui.ErrorHandle;

public class TenantInput implements DbNames {

    public static boolean addTenant(String name, Flat f, Date moveIn) {
	LinkedList<Tenant> tenants = f.getTenants(moveIn, new Date(Long.MAX_VALUE));
	if (!tenants.isEmpty()) {
	    if (ErrorHandle.askYesNo(
		    "Zu diesem Zeitpunkt wohnt " + tenants.getLast().getName() + "noch in der angegebenen Wohnung\n"
			    + "Soll der Auszug auf das Datum des Einzugs gelegt werden?")) {
		// 1 day before
		setMoveout(tenants.getLast(), new Date(moveIn.getTime() - 86400000));
		add(name, f, moveIn);
	    } else
		ErrorHandle
			.popUp("Bitte legen sie zuerst das Auszugdatum für " + tenants.getLast().getName() + " fest!");
	} else {
	    return add(name, f, moveIn);
	}
	return false;
    }

    public static void rename(Tenant t, String name) {
	DbInput.editRow(TENANT_TABLE, TENANT_ID, t.getId(), TENANT_NAME, name);
	t.setName(name);
    }

    public static void setMoveout(Tenant t, Date moveOut) {
	if (DbInput.editRow(TENANT_TABLE, TENANT_ID, t.getId(), TENANT_MOVE_OUT, moveOut))
	    t.setMoveOut(moveOut);
    }

    public static void setMoveIn(Tenant t, Date d) {
	if (DbInput.editRow(TENANT_TABLE, TENANT_ID, t.getId(), TENANT_MOVE_IN, d))
	    t.setMoveIn(d);
    }

    private static boolean add(String name, Flat f, Date moveIn) {
	return DbInput.addRow(TENANT_TABLE, new String[] { TENANT_NAME, TENANT_FLAT, TENANT_MOVE_IN }, name, f.getId(),
		moveIn);
    }

    public static void setRent(double value, Date d, Tenant t) {
	Calendar cal = Calendar.getInstance();
	cal.setTime(d);
	int year = cal.get(Calendar.YEAR);
	Row row = null;
	// remove rent for this year if it exists
	for (Row r : DbHandle.findAll(RENT_TABLE, RENT_YEAR, year)) {
	    if (r.getInt(RENT_TENANT) == t.getId()) {
		row = r;
	    }
	}
	if (row != null)
	    DbInput.removeRow(RENT_TABLE, row);

	DbInput.addRow(RENT_TABLE, new String[] { RENT_TENANT, RENT_YEAR, RENT_COST }, t.getId(), year, value);
    }

    public static void setPersonCount(Tenant t, Date d, int x) {
	DbInput.addRow(PEOPLE_COUNT_TABLE, new String[] { PEOPLE_COUNT_TENANT_ID, PEOPLE_COUNT_DATE, PEOPLE_COUNT },
		t.getId(), d, x);
    }

    public static void setPayment(double value, Tenant t, Date d) {
	// removes all multiple entries;
	DbInput.removeRow(PAYMENT_TABLE, PAYMENT_TENANT, t.getId(), PAYMENT_DATE, d);
	DbInput.addRow(PAYMENT_TABLE, new String[] { PAYMENT_TENANT, PAYMENT_VALUE, PAYMENT_DATE }, t.getId(), value,
		d);
    }

    public static void setHeatCost(Tenant t, Double value, Date d) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(d);
	DbInput.removeRow(HEATER_TABLE, HEATER_TENANT, t.getId(), HEATER_YEAR, calendar.get(Calendar.YEAR));
	DbInput.addRow(HEATER_TABLE, new String[] { HEATER_COST, HEATER_TENANT, HEATER_YEAR }, value, t.getId(),
		calendar.get(Calendar.YEAR));
    }
}
