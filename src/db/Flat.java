package db;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

public class Flat implements DbNames, LoadAble {

    private final int id;
    private final House house;
    private final double squaremeter;
    private final String description;
    private static LinkedList<Flat> flats;

    /**
     * 
     * @param id
     * @param house
     * @param squarem
     */
    public Flat(int id, House house, double squarem) {
	this.id = id;
	this.house = house;
	squaremeter = squarem;
	description = loadDescription();
    }

    /**
     * loads all flats linked to the house
     * 
     * @param houseId
     * @return
     */
    public static LinkedList<Flat> loadFlats(House h) {
	LinkedList<Flat> flats = new LinkedList<Flat>();
	Table t = DbHandle.getTable(FLAT_TABLE);
	String col = t.getColumns().get(0).getName();

	Double squarem;
	for (Row r : DbHandle.findAll(FLAT_TABLE, FLAT_HOUSE, h.id)) {
	    squarem = r.getDouble(FLAT_SQUAREMETER);
	    flats.add(new Flat(r.getInt(col), h, squarem));
	}

	return flats;
    }

    public static LinkedList<Flat> loadFlats() {
	LinkedList<Flat> all = new LinkedList<Flat>();
	Table t = DbHandle.getTable(FLAT_TABLE);
	String col = t.getColumns().get(0).getName();

	double squarem;
	for (Row r : t) {
	    squarem = r.getDouble(FLAT_SQUAREMETER);
	    all.add(new Flat(r.getInt(col), House.getHouse(r.getInt(FLAT_HOUSE)), squarem));
	}
	flats = all;
	return all;
    }

    /**
     * returns every tenant that lived in the flat between given dates
     * 
     * @param from
     * @param to
     * @return
     */
    public LinkedList<Tenant> getTenants(Date start, Date end) {
	LinkedList<Tenant> tenants = new LinkedList<Tenant>();

	tenants = Tenant.loadTenants(this);
	LinkedList<Tenant> remain = new LinkedList<Tenant>();

	for (Tenant t : tenants) {
	    if (t.getMoveIn().after(end) || t.getMoveOut().before(start)) {
		;
	    } else {
		remain.add(t);
	    }
	}

	return tenants;

    }

    public LinkedList<Tenant> getTenants(int year) {
	Calendar start = Calendar.getInstance();
	start.set(year, 0, 0);
	Calendar end = Calendar.getInstance();
	end.set(year, 11, 31);
	return getTenants(start.getTime(), end.getTime());
    }

    public double getAveragePeopleCount(int year) {

	LinkedList<Tenant> tenants = getTenants(year);
	int sum = 0;
	for (Tenant t : tenants) {
	    sum += t.getPersonCount().getCountSum(year);
	}

	return (double) sum / 12;
    }

    public int getShare() {
	Row r = DbHandle.findId(id, DbHandle.getTable(FLAT_TABLE));
	return r.getInt(FLAT_SHARE);
    }

    /**
     * 
     * @return
     */
    public LinkedList<Meter> getMeter(Date start, Date end) {
	LinkedList<Meter> meter = Meter.loadMeters(this);
	LinkedList<Meter> validMeter = new LinkedList<Meter>();

	for (Meter m : meter) {
	    if (m.inInterval(start, end)) {
		validMeter.add(m);
	    }
	}

	return validMeter;
    }

    public LinkedList<Meter> loadMeter() {
	return Meter.loadMeters(this);
    }

    public int getId() {
	return id;
    }

    @Override
    public House getHouse() {
	return house;
    }

    public double getSquaremeter() {
	return squaremeter;
    }

    @Override
    public String toString() {
	return description;
    }

    private String loadDescription() {
	Table tbl = DbHandle.getTable(FLAT_TABLE);
	return DbHandle.findUnique(FLAT_TABLE, tbl.getColumns().get(0).getName(), getId()).getString(FLAT_DESCRIPTION);
    }

    public static Flat getFlat(int id) {
	if (flats == null) {
	    loadFlats();
	}
	for (Flat f : flats) {
	    if (f.id == id) {
		return f;
	    }
	}

	throw new InvalidParameterException("Flat with id " + id + " was not found");
    }

    public boolean equals(Flat f) {
	return f.getId() == id;
    }

    @Override
    public Flat getFlat() {
	return this;
    }

}
