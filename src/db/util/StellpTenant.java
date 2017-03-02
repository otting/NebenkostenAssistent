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
import db.StellP;
import db.Tenant;

public class StellpTenant implements DbNames, Comparable<StellpTenant> {
    private Date date, end;
    private String name;
    private double rent;
    private int id;

    public StellpTenant() {
	this("Leerstand", new Date(0), 0.0);
    }

    public StellpTenant(String name, Date start, double rent) {
	this(-1, name, start, rent);
    }

    public StellpTenant(int id, String name, Date start, double rent) {
	setName(name);
	setDate(start);
	setRent(rent);
	setId(id);
    }

    public static StellpTenant loadTenant(StellP sp) {
	Row tenantContract = null;
	Row externalContract = null;

	Date d = new Date(0);
	Date holder;
	for (Row r : DbHandle.findAll(STELLP_CONTRACT_TABLE, STELLP_CONTRACT_STELLPLATZ, sp.getId())) {
	    holder = r.getDate(STELLP_CONTRACT_START);
	    if (holder.after(d)) {
		tenantContract = r;
		d = holder;
	    }
	}
	d = new Date(0);
	for (Row r : DbHandle.findAll(STELLP_EXTERN_TABLE, STELLP_EXTERN_STELLPLATZ, sp.getId())) {
	    holder = r.getDate(STELLP_EXTERN_START);
	    if (holder.after(d)) {
		externalContract = r;
		d = holder;
	    }
	}

	if (tenantContract != null && tenantContract.getDate(STELLP_CONTRACT_START).after(d)) {
	    return fromContractRow(tenantContract);
	} else if (externalContract != null) {
	    return fromExternalContractRow(externalContract);
	} else {
	    return new StellpTenant();
	}
    }

    public static LinkedList<TimeAndCost> loadCosts(Tenant t, int year) {

	LinkedList<TimeAndCost> list = new LinkedList<>();
	TimeAndCost tc;
	Calendar cal = new GregorianCalendar(year, 0, 0);
	Calendar endOfYear = new GregorianCalendar(year, 11, 30);
	Date start;
	for (StellpTenant sp : loadAll(t, year)) {
	    tc = new TimeAndCost();
	    if (sp.getDate().after(cal.getTime()))
		start = sp.getDate();
	    else
		start = cal.getTime();
	    if (sp.getEnd().before(endOfYear.getTime())) {
		tc.addTime(start, sp.getEnd());
		tc.addCost(tc.getMonth() * sp.getRent());
	    } else {
		tc.addTime(start, endOfYear.getTime());
		tc.addCost(sp.getRent() * tc.getMonth());
		tc.setNewMonthly(sp.getRent());
	    }
	    list.add(tc);
	}

	return list;
    }

    private static LinkedList<StellpTenant> loadAll(Tenant t, int year) {
	LinkedList<StellpTenant> myContract = new LinkedList<>();
	for (StellP sp : StellP.loadAll()) {
	    for (StellpTenant spt : loadRelevantContracts(year).get(sp.getId())) {
		if (spt.getID() == t.getId()) {
		    myContract.add(spt);
		}
	    }
	}
	return myContract;
    }

    // StellP ID + Contract Data
    private static HashMap<Integer, LinkedList<StellpTenant>> relevant = null;
    private static long time = 0;

    private static HashMap<Integer, LinkedList<StellpTenant>> loadRelevantContracts(int year) {

	if (relevant != null) {
	    if (System.currentTimeMillis() - time < 10000) {
		return relevant;
	    } else {
		time = System.currentTimeMillis();
	    }
	}

	relevant = new HashMap<Integer, LinkedList<StellpTenant>>();

	for (StellP g : StellP.loadAll()) {
	    relevant.put(g.getId(), loadRelevant(g, year));
	}

	return relevant;
    }

    /**
     * loads all contracts that are relevant for StellP g in given year.
     * 
     * @param sp
     * @param year
     * @return
     */
    private static LinkedList<StellpTenant> loadRelevant(StellP sp, int year) {
	LinkedList<StellpTenant> sps = new LinkedList<StellpTenant>();
	Calendar cal = Calendar.getInstance();
	Date lastRelevant = new Date(0);
	Row lastRelevantRow = null;
	for (Row r : DbHandle.findAll(STELLP_CONTRACT_TABLE, STELLP_CONTRACT_STELLPLATZ, sp.getId())) {
	    cal.setTime(r.getDate(STELLP_CONTRACT_START));
	    if (cal.get(Calendar.YEAR) < year) {
		if (cal.getTime().after(lastRelevant)) {
		    lastRelevant = cal.getTime();
		    lastRelevantRow = r;
		}
	    } else if (cal.get(Calendar.YEAR) == year) {
		sps.add(fromContractRow(r));
	    }
	}
	boolean externalRelevant = false;
	for (Row r : DbHandle.findAll(GARAGE_EXTERN_TABLE, GARAGE_EXTERN_GARAGE, sp.getId())) {
	    if (cal.get(Calendar.YEAR) < year) {
		if (cal.getTime().after(lastRelevant)) {
		    lastRelevant = cal.getTime();
		    lastRelevantRow = r;
		    externalRelevant = true;
		}
	    } else if (cal.get(Calendar.YEAR) == year) {
		sps.add(fromExternalContractRow(r));
	    }
	}

	if (lastRelevantRow == null) {
	    sps.add(new StellpTenant());
	} else if (externalRelevant) {
	    sps.add(fromExternalContractRow(lastRelevantRow));
	} else {
	    sps.add(fromContractRow(lastRelevantRow));
	}
	Collections.sort(sps);
	for (StellpTenant gt : sps) {
	    if (sps.indexOf(gt) < sps.size() - 1) {
		gt.setEnd(sps.get(sps.indexOf(gt) + 1).getDate());
	    }
	}
	return sps;
    }

    private static StellpTenant fromExternalContractRow(Row r) {
	return (new StellpTenant(r.getString(STELLP_EXTERN_TENANT), r.getDate(STELLP_EXTERN_START),
		r.getDouble(STELLP_EXTERN_RENT)));
    }

    private static StellpTenant fromContractRow(Row r) {
	int tenant = r.getInt(STELLP_CONTRACT_TENANT);
	String name = Tenant.loadName(r.getInt(STELLP_CONTRACT_TENANT));
	double rent = r.getDouble(STELLP_CONTRACT_RENT);
	Date date = r.getDate(STELLP_CONTRACT_START);
	return (new StellpTenant(tenant, name, date, rent));
    }

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public String getName() {
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

    public int getID() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public Date getEnd() {
	if (end == null)
	    return new Date(Long.MAX_VALUE);
	return end;
    }

    public void setEnd(Date end) {
	this.end = end;
    }

    public String toString() {
	return getName() + ": " + getDate() + "->" + getEnd() + "\n";
    }

    @Override
    public int compareTo(StellpTenant o) {
	if (getDate() == null)
	    return -1;
	if (o.getDate() == null)
	    return 1;
	return new Long(getDate().getTime()).compareTo(o.getDate().getTime());
    }
}
