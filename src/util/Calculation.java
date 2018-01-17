package util;

import java.security.InvalidAlgorithmParameterException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;

import javax.management.InvalidAttributeValueException;

import com.healthmarketscience.jackcess.Row;

import db.DbHandle;
import db.DbNames;
import db.Flat;
import db.House;
import db.Meter;
import db.Tenant;
import db.util.PersonCount;
import gui.ErrorHandle;

public class Calculation implements DbNames {

    static int year;
    static Date start, end;
    static House[] houses;

    /**
     * Constructor automatically starts calculating everything necessary
     * 
     * @param year
     * @param start
     * @param end
     * @param houses
     * @return
     * @throws InvalidAttributeValueException
     */
    public static void calculate(int year, Date start, Date end, LinkedList<House> houses)
	    throws InvalidAttributeValueException {
	Calculation.year = year;
	Calculation.start = start;
	Calculation.end = end;
	if (houses.isEmpty())
	    ErrorHandle.popUp("Kein Haus gewählt!");
	else
	    calculate(houses);

    }

    @SuppressWarnings("deprecation")
    public static void calculate(int year, LinkedList<House> houses) {
	try {
	    calculate(year, new Date(year - 1900, 0, 1), new Date(year - 1900 + 1, 0, 0), houses);
	} catch (InvalidAttributeValueException e) {
	    ErrorHandle.popUp("Abrechnung konnte nicht erstellt werden!\n" + e.getMessage());
	}
    }

    /**
     * <b>Only use this constructor if a single flat should be calculated</b>
     * 
     * @param year
     * @param start
     * @param end
     * @param flat
     * @return
     */
    public static void calculate(int year, Date start, Date end, Flat flat) {
	Calculation.year = year;
	Calculation.start = start;
	Calculation.end = end;
	flatCalc(flat);
    }

    private static void calculate(LinkedList<House> houses) throws InvalidAttributeValueException {

	int totalShare = getTotalShares();
	for (House h : houses) {
	    LinkedList<TenantBill> tbills = new LinkedList<TenantBill>();
	    LinkedList<Flat> flats = h.getFlats();
	    for (Flat f : flats) {
		tbills.addAll(flatCalc(f));
	    }

	    HouseBill hb = houseCalc(h, tbills);
	    hb.setTotalShare(totalShare);
	    try {
		ExcelExport.exportBills(year, h, hb, tbills);
	    } catch (Exception e) {
		ErrorHandle.popUp("Could not finish export of " + h + "\n" + e.getMessage());
		e.printStackTrace();
	    }
	}

    }

    private static int getTotalShares() throws InvalidAttributeValueException {
	int share = 0;
	for (Row r : DbHandle.getTable(TOTALSHARE_TABLE)) {
	    if (r.getInt(TOTALSHARE_YEAR) <= year) {
		share = r.getInt(TOTALSHARE_SHARE);
	    }
	}
	if (share <= 0) {
	    throw new InvalidAttributeValueException("Total amount of share may not be <= 0");
	}
	return share;
    }

    private static HouseBill houseCalc(House h, LinkedList<TenantBill> bills) {
	HouseBill hb = new HouseBill(h, start, end, year);
	for (TenantBill tb : bills) {
	    hb.setColdWater(hb.getColdWater() + tb.getColdWater());
	    hb.setHotWater(hb.getHotWater() + tb.getHotWater());
	}
	hb.setPersonCount(h.getPersonCount(year));
	for (Flat f : h.getFlats()) {
	    hb.addSquareMeter(f.getSquaremeter());
	}

	return hb;
    }

    private static LinkedList<TenantBill> flatCalc(Flat flat) {
	LinkedList<Tenant> tenants = flat.getTenants(start, end);
	LinkedList<TenantBill> bills = new LinkedList<TenantBill>();
	Collections.sort(tenants);
	TenantBill b;
	try {
	    for (Tenant tenant : tenants) {
		b = new TenantBill(tenant);
		b.setColdWater(calcConsumption(tenant, WATER_COLD));
		b.setHotWater(calcConsumption(tenant, WATER_HOT));
		b.setHeater(calcConsumption(tenant, HEATER));
		b.setPersonCount(tenant.getPersonCount(year));
		b.setBalance(tenant.getPayment(end));
		b.setRent(tenant.getRent(year));
		b.setHeater(tenant.getHeaterCost(year));
		Date begin = start;
		if (start.before(tenant.getMoveIn())) {
		    begin = tenant.getMoveIn();
		}
		Date period = end;
		if (end.after(tenant.getMoveOut())) {
		    period = tenant.getMoveOut();
		}
		b.setStart(begin);
		b.setEnd(period);
		bills.add(b);
	    }

	} catch (InvalidAlgorithmParameterException e) {
	    ErrorHandle
		    .popUp("An error occured while calculating data for Flat " + flat + "\nthis flat will be skipped!");
	    e.printStackTrace();
	}
	LinkedList<TenantBill> all = emptyFlat(flat);
	if (!all.isEmpty())
	    bills.addAll(emptyFlat(flat));
	return bills;
    }

    private static LinkedList<TenantBill> emptyFlat(Flat flat) {
	int[] pc = new int[12];
	for (Tenant t : flat.getTenants(start, end)) {
	    PersonCount perC = t.getPersonCount();
	    for (int i = 0; i < 12; i++) {
		pc[i] += perC.getCount(year, i);
	    }
	}
	Calendar start = new GregorianCalendar();
	Calendar end = new GregorianCalendar();
	LinkedList<TenantBill> tbs = new LinkedList<TenantBill>();

	for (int i = 0; i < 12; i++) {
	    if (pc[i] == 0) {
		start.set(year, i, 1);
		while (pc[i + 1] == 0)
		    i++;
		end.set(year, i, 1);
		end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
		tbs.add(generateEmpty(flat, start, end));
	    }
	}

	return tbs;
    }

    private static TenantBill generateEmpty(Flat flat, Calendar from, Calendar to) {
	Tenant leer = new Tenant(-1, flat, "Leerstand", from.getTime(), to.getTime());
	TenantBill tb = new TenantBill(leer);
	LinkedList<Meter> meter = flat.getMeter(start, end);

	tb.setBalance(0);
	try {
	    tb.setColdWater(Meter.calcConsumption(meter, from.getTime(), to.getTime(), Meter.WATER_COLD));
	    tb.setEnd(to.getTime());
	    tb.setFlat(flat);
	    tb.setHeater(0.0);
	    tb.setHotWater(Meter.calcConsumption(meter, from.getTime(), to.getTime(), Meter.WATER_HOT));
	    tb.setPersonCount(0.0);
	    tb.setRent(0.0);
	    tb.setStart(from.getTime());
	    tb.setTenant(leer);
	} catch (InvalidAlgorithmParameterException e) {
	    System.err.println("Error beim Leerstand");
	    e.printStackTrace();
	}

	return tb;
    }

    private static double calcConsumption(Tenant tenant, String type) throws InvalidAlgorithmParameterException {

	LinkedList<Meter> meter = tenant.getFlat().getMeter(start, end);
	Iterator<Meter> iter = meter.iterator();

	while (iter.hasNext()) {
	    if (!iter.next().getDescription().equals(type)) {
		iter.remove();
	    }
	}

	Date s = start;
	Date e = end;

	if (tenant.getMoveOut().before(start))
	    return 0.0;
	if (tenant.getMoveIn().after(start))

	{
	    s = tenant.getMoveIn();
	}
	if (tenant.getMoveOut().before(end))

	{
	    e = tenant.getMoveOut();
	}
	return Meter.calcConsumption(meter, s, e);
    }

}
