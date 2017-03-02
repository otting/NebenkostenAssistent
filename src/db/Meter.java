package db;

import java.security.InvalidAlgorithmParameterException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;

import com.healthmarketscience.jackcess.Row;

import db.util.Time;
import gui.ErrorHandle;

/**
 * this class is used to calculate annual consumption of different meters
 * (Electric, Water, Heater)
 * 
 * @author christian
 *
 */

public class Meter implements DbNames, Comparable<Meter>, LoadAble {

    private final String id;
    // cold water, warm water etc
    private String description;
    private String tag;
    private int kind;
    private boolean main;
    private Flat flat;

    /** saves all meters Key = FlatID */
    public static TreeMap<Integer, LinkedList<Meter>> all = new TreeMap<Integer, LinkedList<Meter>>();

    public static LinkedList<Meter> loadMeters(Flat flat) {
	LinkedList<Meter> meter = new LinkedList<Meter>();
	LinkedList<Row> rows = DbHandle.findAll(METER_TABLE, METER_FLAT, flat.getId());
	String meterID;
	for (Row r : rows) {
	    if (!r.getBoolean(METER_MAINCOUNTER)) {
		meterID = r.getString(METER_TABLE_ID);
		meter.add(new Meter(meterID, flat));
	    }
	}
	return meter;
    }

    public static LinkedList<Meter> loadMeter(House house) {
	LinkedList<Meter> meter = new LinkedList<Meter>();
	for (Flat f : house.getFlats()) {
	    meter.addAll(f.loadMeter());
	}

	return meter;
    }

    public static LinkedList<Meter> findMainMeter(House house) {
	LinkedList<Row> rows = new LinkedList<Row>();
	LinkedList<Meter> meter = new LinkedList<Meter>();
	for (Flat f : house.getFlats()) {
	    rows.addAll(DbHandle.findAll(METER_TABLE, METER_FLAT, f.getId()));
	    for (Row r : rows) {
		if (r.getBoolean(METER_MAINCOUNTER))
		    meter.add(new Meter(r.getString(METER_TABLE_ID), f));
	    }
	    rows = new LinkedList<Row>();
	}
	return meter;
    }

    public Meter(String id, Flat flat) {
	this.id = id;
	this.flat = flat;
	loadInformation();
	list(this);
    }

    private void loadInformation() {
	Row r = DbHandle.findUnique(METER_TABLE, METER_ID, id);
	setKind(r.getInt(METER_KIND));
	main = r.getBoolean(METER_MAINCOUNTER);
	r = DbHandle.findUnique(METER_DESCRIPTION_TABLE, METER_DESCRIPTION_ID, kind);
	setDescription(r.getString(METER_DESCRIPTION_NAME));
	setTag(r.getString(METER_DESCRIPTION_TAG));

    }

    /**
     * 
     * @param meter
     *            should only contain meters who have values in the given period
     * @param start
     * @param end
     * @return
     * @throws InvalidAlgorithmParameterException
     */
    public static double calcConsumption(LinkedList<Meter> meter, Date start, Date end)
	    throws InvalidAlgorithmParameterException {

	Collections.sort(meter);
	Date begin = start;
	double sum = 0f;
	for (Meter m : meter) {
	    if (m.getFirstEntryDate().after(start)) {
		begin = m.getFirstEntryDate();
	    } else {
		begin = start;
	    }
	    if (m.getLastEntryDate().before(end)) {
		sum += m.calcConsumption(begin, m.getLastEntryDate());
	    } else {
		sum += m.calcConsumption(start, end);
	    }
	}

	return sum;
    }

    /**
     * filters given meters and only calculates those who are of the give kind
     * 
     * @param meter
     * @param start
     * @param end
     * @param kind
     * @return
     * @throws InvalidAlgorithmParameterException
     */
    public static double calcConsumption(LinkedList<Meter> meter, Date start, Date end, String kind)
	    throws InvalidAlgorithmParameterException {
	if (meter.isEmpty()) {
	    throw new InvalidAlgorithmParameterException("No " + kind + " meter found");
	}
	Iterator<Meter> iter = meter.iterator();
	while (iter.hasNext()) {
	    if (iter.next().getDescription().equals(kind)) {
		iter.remove();
	    }
	}
	return calcConsumption(meter, start, end);
    }

    /**
     * calculates consumption in the given time period, will interpolate or
     * extrapolate the data to fit the period
     * 
     * @param start
     * @param end
     * @return
     * @throws InvalidAlgorithmParameterException
     */
    public double calcConsumption(Date start, Date end) throws InvalidAlgorithmParameterException {
	Row first = getClosestValue(start, null);
	Date one = first.getDate(METER_VALUES_DATE);
	Row last = getClosestValue(end, one);

	if (first == null || last == null || first.equals(last) || start.after(end)) {
	    ErrorHandle.popUp(this + ": There is not enough Data in this interval");
	    throw new InvalidAlgorithmParameterException("Nicht genug Zählerstände für " + this.toString());
	}

	double startValue = first.getDouble(METER_VALUES_VALUE);
	double endValue = last.getDouble(METER_VALUES_VALUE);

	double consumption = endValue - startValue;
	// calculates the actual consumption in the give period of time
	return (consumption);

    }

    @Override
    public String toString() {
	String s = getTag() + " : " + getId();
	if (isMain()) {
	    s = "!" + s;
	}
	return (s);
    }

    public boolean isMain() {
	return main;
    }

    public String getId() {
	return id;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public int getKind() {
	return kind;
    }

    public void setKind(int kindID) {
	this.kind = kindID;
    }

    public int getFlatID() {
	return flat.getId();
    }

    public Row getLastEntry() {
	Row r = DbHandle.findLast(METER_VALUES_TABLE, METER_VALUES_METER_ID, id);
	return r;
    }

    public Date getLastEntryDate() {
	Row r = getLastEntry();
	if (r != null) {
	    Date d = r.getDate(METER_VALUES_DATE);
	    return d;
	} else
	    return new Date(0);

    }

    public double getLastEntryValue() {
	Row r = getLastEntry();
	if (r != null)
	    return r.getDouble(METER_VALUES_VALUE);
	else
	    return 0.0;
    }

    public Date getFirstEntryDate() {

	Date last = null;
	Date first = null;
	for (Row r : DbHandle.findAll(METER_VALUES_TABLE, METER_VALUES_METER_ID, id)) {
	    last = r.getDate(METER_VALUES_DATE);
	    if (first == null || last.before(first))
		first = last;
	}

	return first;

    }

    public Row getEntryFor(Date d) {
	for (Row r : DbHandle.getTable(METER_VALUES_TABLE)) {
	    if (r.getDate(METER_VALUES_DATE).equals(d) && r.getString(METER_VALUES_METER_ID).equals(getId())) {
		return r;
	    }
	}
	return null;
    }

    public boolean inInterval(Date start, Date end) {
	Date first = getFirstEntryDate();
	Date last = getLastEntryDate();
	if (first != null && last != null)
	    return (start.getTime() <= last.getTime() && end.getTime() >= first.getTime());
	return false;
    }

    /**
     * private functions -------------------------
     */

    /**
     * 
     * 
     * @param date
     * @param exclude
     *            optional parameter for excluding specific dates
     * @return the Row containing the last meter value before the given date
     *         <b>or the first after the date</b> if no match was found (up to 3
     *         month)
     * @throws InvalidAlgorithmParameterException
     */
    private Row getClosestValue(Date date, Date exclude) throws InvalidAlgorithmParameterException {
	LinkedList<Row> values = DbHandle.findAll(METER_VALUES_TABLE, METER_VALUES_METER_ID, id);
	if (values.isEmpty()) {
	    return null;
	}
	Date anno = null;
	Row last = null;
	// 1 Month in milliseconds
	long maxTime = 2592000000l;
	Date latest = new Date(date.getTime() + maxTime);
	// find last value before date
	for (Row r : values) {
	    anno = r.getDate(METER_VALUES_DATE);
	    if ((anno.before(date) || anno.equals(date)
		    || (last != null && Time.between(anno, date) < Time.between(last.getDate(METER_VALUES_DATE), date))
		    || (last == null && anno.before(latest))) && !anno.equals(exclude)) {
		if (last == null || anno.after(last.getDate(METER_VALUES_DATE)))
		    last = r;
	    }
	}

	if (last == null) {
	    System.out.println("No Date Found");
	    throw new InvalidAlgorithmParameterException("No Date found");
	}
	return last;
    }

    private void list(Meter m) {
	if (all.containsKey(getFlatID())) {
	    all.get(getFlatID()).add(m);
	} else {
	    LinkedList<Meter> l = new LinkedList<Meter>();
	    l.add(m);
	    all.put(getFlatID(), l);
	}
    }

    @Override
    public int compareTo(Meter m) {
	return getLastEntryDate().compareTo(m.getLastEntryDate());
    }

    /**
     * Returns all Meter active in the given time period
     * 
     * @param years:
     *            how long the Meter is allowed to be inactive (<=0 = all)
     * @return LinkedList of all Meters in time period
     */
    public static LinkedList<Meter> loadAll(int years) {
	// Date for time period
	Date start, end;
	end = new Date(Calendar.getInstance().getTimeInMillis());

	if (years > 0) {
	    Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - years);
	    start = new Date(cal.getTimeInMillis());
	} else {
	    start = new Date(0);
	}

	LinkedList<Meter> meter = new LinkedList<Meter>();
	for (Flat f : Flat.loadFlats()) {
	    meter.addAll(f.getMeter(start, end));
	}
	for (House h : House.loadHouses()) {
	    meter.addAll(Meter.findMainMeter(h));
	}
	Collections.sort(meter);
	return meter;
    }

    @Override
    public House getHouse() {
	return flat.getHouse();
    }

    @Override
    public Flat getFlat() {
	return flat;
    }

    public String getTag() {
	return tag;
    }

    public void setTag(String tag) {
	this.tag = tag;
    }

}
