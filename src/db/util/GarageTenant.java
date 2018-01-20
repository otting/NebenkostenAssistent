package db.util;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;

import com.healthmarketscience.jackcess.Row;

import db.DbHandle;
import db.DbNames;
import db.Garage;
import db.Tenant;

public class GarageTenant implements DbNames, Comparable<GarageTenant> {
    private Date date, end;
    private String name;
    private double rent;
    private int id;

    public GarageTenant() {
	this("Leerstand", new Date(0), 0.0);
    }

    public GarageTenant(String name, Date start, double rent) {
	this(-1, name, start, rent);
    }

    public GarageTenant(int id, String name, Date start, double rent) {
	setName(name);
	setDate(start);
	setRent(rent);
	this.id = id;
	setEnd(new Date(Long.MAX_VALUE));
    }

    public static GarageTenant loadTenant(Garage g) {
	Row tenantContract = null;
	Row externalContract = null;

	Date d = new Date(0);
	Date holder;
	for (Row r : DbHandle.findAll(GARAGE_CONTRACT_TABLE, GARAGE_CONTRACT_GARAGE, g.getId())) {
	    holder = r.getDate(GARAGE_CONTRACT_START);
	    if (holder.after(d)) {
		tenantContract = r;
		d = holder;
	    }
	}
	d = new Date(0);
	for (Row r : DbHandle.findAll(GARAGE_EXTERN_TABLE, GARAGE_EXTERN_GARAGE, g.getId())) {
	    holder = r.getDate(GARAGE_EXTERN_DATE);
	    if (holder.after(d)) {
		externalContract = r;
		d = holder;
	    }
	}

	if (tenantContract != null && tenantContract.getDate(GARAGE_CONTRACT_START).after(d)) {
	    return fromContractRow(tenantContract);
	} else if (externalContract != null) {
	    return fromExternalContractRow(externalContract);
	} else {
	    return new GarageTenant();
	}
    }

    public static LinkedList<TimeAndCost> loadCosts(Tenant t, int year) {

	LinkedList<TimeAndCost> list = new LinkedList<>();
	TimeAndCost tc;
	Calendar cal = new GregorianCalendar(year, 0, 0);
	Calendar endOfYear = new GregorianCalendar(year, 11, 30);
	Date start;
	for (GarageTenant gt : loadAll(t, year)) {
	    tc = new TimeAndCost();
	    if (gt.getDate().after(cal.getTime()))
		start = gt.getDate();
	    else
		start = cal.getTime();
	    if (gt.getEnd().before(endOfYear.getTime())) {
		tc.addTime(start, gt.getEnd());
		tc.addCost(tc.getMonth() * gt.getRent());
	    } else {
		tc.addTime(start, endOfYear.getTime());
		tc.addCost(gt.getRent() * tc.getMonth());
		tc.setNewMonthly(gt.getRent());
	    }
	    list.add(tc);
	}

	return list;
    }

    private static LinkedList<GarageTenant> loadAll(Tenant t, int year) {
	LinkedList<GarageTenant> myContract = new LinkedList<>();
	for (Garage g : Garage.loadAll()) {
	    for (GarageTenant gt : loadRelevantContracts(year).get(g.getId())) {
		if (gt.getID() == t.getId()) {
		    myContract.add(gt);
		}
	    }
	}
	return myContract;
    }

    // Garage ID + Contract Data
    private static HashMap<Integer, LinkedList<GarageTenant>> relevant = null;
    private static long time = 0;

    private static HashMap<Integer, LinkedList<GarageTenant>> loadRelevantContracts(int year) {
	// only recalculate relevant data every 10 seconds
	if (relevant != null) {
	    if (System.currentTimeMillis() - time < 10000) {
		return relevant;
	    } else {
		time = System.currentTimeMillis();
	    }
	}

	relevant = new HashMap<Integer, LinkedList<GarageTenant>>();

	for (Garage g : Garage.loadAll()) {
	    relevant.put(g.getId(), loadRelevant(g, year));
	}

	return relevant;
    }

    /**
     * loads all contracts that are relevant for Garage g in given year.
     * 
     * @param g
     * @param year
     * @return
     */
    private static LinkedList<GarageTenant> loadRelevant(Garage g, int year) {
	LinkedList<GarageTenant> gts = new LinkedList<GarageTenant>();
	Calendar cal = Calendar.getInstance();
	Date lastRelevant = new Date(0);
	Row lastRelevantRow = null;
	LinkedList<Row> rows = DbHandle.findAll(GARAGE_CONTRACT_TABLE, GARAGE_CONTRACT_GARAGE, g.getId());
	for (Row r : rows) {
	    cal.setTime(r.getDate(GARAGE_CONTRACT_START));
	    if (cal.get(Calendar.YEAR) < year) {
		if (cal.getTime().after(lastRelevant)) {
		    lastRelevant = cal.getTime();
		    lastRelevantRow = r;
		}
	    } else if (cal.get(Calendar.YEAR) == year) {
		gts.add(fromContractRow(r));
	    }
	}
	boolean externalRelevant = false;
	rows = DbHandle.findAll(GARAGE_EXTERN_TABLE, GARAGE_EXTERN_GARAGE, g.getId());
	for (Row r : rows) {
	    cal.setTime(r.getDate(GARAGE_EXTERN_DATE));
	    if (cal.get(Calendar.YEAR) < year) {
		if (cal.getTime().after(lastRelevant)) {
		    lastRelevant = cal.getTime();
		    lastRelevantRow = r;
		    externalRelevant = true;
		}
	    } else if (cal.get(Calendar.YEAR) == year) {
		gts.add(fromExternalContractRow(r));
	    }
	}
	// decide rather the last relevant entry before the given year is a normal or
	// external tenant
	if (lastRelevantRow == null) {
	    gts.add(new GarageTenant());
	} else if (externalRelevant) {
	    gts.add(fromExternalContractRow(lastRelevantRow));
	} else {
	    gts.add(fromContractRow(lastRelevantRow));
	}
	Collections.sort(gts);
	for (GarageTenant gt : gts) {
	    if (gts.indexOf(gt) < gts.size() - 1) {
		gt.setEnd(gts.get(gts.indexOf(gt) + 1).getDate());
	    }
	}
	return gts;
    }

    private static GarageTenant fromExternalContractRow(Row r) {
	return (new GarageTenant(r.getString(GARAGE_EXTERN_TENANT), r.getDate(GARAGE_EXTERN_DATE),
		r.getDouble(GARAGE_EXTERN_RENT)));
    }

    private static GarageTenant fromContractRow(Row r) {
	return (new GarageTenant(r.getInt(GARAGE_CONTRACT_TENANT), Tenant.loadName(r.getInt(GARAGE_CONTRACT_TENANT)),
		r.getDate(GARAGE_CONTRACT_START), r.getDouble(GARAGE_CONTRACT_RENT)));
    }

    public boolean hasID() {
	return (id != -1);
    }

    public int getID() {
	return id;
    }

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public String getName() {
	if (name.isEmpty())
	    return "Leerstand";
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public double getRent() {
	return rent;
    }

    public void setRent(double rent) {
	this.rent = rent;
    }

    @Override
    public int compareTo(GarageTenant o) {
	return new Long(getDate().getTime()).compareTo(o.getDate().getTime());
    }

    public Date getEnd() {
	return end;
    }

    public void setEnd(Date end) {
	this.end = end;
    }

    public String toString() {
	return getName() + ": " + getDate() + "->" + getEnd() + "\n";
    }
}
