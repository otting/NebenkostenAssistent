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

    public static void setPrepayedHeatCost(Tenant t, Double value, Date d) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(d);
	DbInput.removeRow(PREPAYED_HEATER_TABLE, PREPAYED_HEATER_TENANT, t.getId(), PREPAYED_HEATER_YEAR,
		calendar.get(Calendar.YEAR));
	DbInput.addRow(PREPAYED_HEATER_TABLE,
		new String[] { PREPAYED_HEATER_PAYED, PREPAYED_HEATER_TENANT, PREPAYED_HEATER_YEAR }, value, t.getId(),
		calendar.get(Calendar.YEAR));
    }

    public static void setCableSupplyCost(Tenant t, Double value, Date d) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(d);
	DbInput.removeRow(HOUSESUPPLY_TABLE, HOUSESUPPLY_TENANT, t.getId(), HOUSESUPPLY_YEAR,
		calendar.get(Calendar.YEAR));
	DbInput.addRow(HOUSESUPPLY_TABLE, new String[] { HOUSESUPPLY_VALUE, HOUSESUPPLY_TENANT, HOUSESUPPLY_YEAR },
		value, t.getId(), calendar.get(Calendar.YEAR));

    }

    public static void setCableProvidingCost(Tenant t, Double value, Date d) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(d);
	DbInput.removeRow(PROVIDINGFEE_TABLE, PROVIDINGFEE_TENANT, t.getId(), PROVIDINGFEE_YEAR,
		calendar.get(Calendar.YEAR));
	DbInput.addRow(PROVIDINGFEE_TABLE, new String[] { PROVIDINGFEE_VALUE, PROVIDINGFEE_TENANT, PROVIDINGFEE_YEAR },
		value, t.getId(), calendar.get(Calendar.YEAR));

    }

    public static void setSonstigeKosten(Tenant t, Double value, Date d, String description) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(d);
	DbInput.removeRow(SONSTIGE_TABLE, SONSTIGE_TENANT, t.getId(), SONSTIGE_YEAR, calendar.get(Calendar.YEAR));
	DbInput.addRow(SONSTIGE_TABLE,
		new String[] { SONSTIGE_VALUE, SONSTIGE_TENANT, SONSTIGE_YEAR, SONSTIGE_DESCRIPTION }, value, t.getId(),
		calendar.get(Calendar.YEAR), description);

    }

    public static void setModernisierungsKosten(Tenant t, Double value, Date d, String description) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(d);
	DbInput.removeRow(MODERN_TABLE, MODERN_TENANT, t.getId(), MODERN_YEAR, calendar.get(Calendar.YEAR));
	DbInput.addRow(MODERN_TABLE, new String[] { MODERN_VALUE, MODERN_TENANT, MODERN_YEAR, MODERN_DESCRIPTION },
		value, t.getId(), calendar.get(Calendar.YEAR), description);

    }

}
