package db;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import db.util.GarageTenant;
import db.util.PersonCount;
import db.util.StellpTenant;
import db.util.TimeAndCost;
import gui.ErrorHandle;

/**
 * gathers all the important information for a tenant
 * 
 * @author christian
 *
 */

public class Tenant implements DbNames, Comparable<Tenant>, LoadAble {
    private int id;
    private Flat flat;
    private String name;
    private Date moveIn, moveOut;
    private PersonCount personCount;

    // name of the table holding the person count changes
    /**
     * Constructor
     * 
     * @param myId
     * @param flat
     * @param name
     */
    public Tenant(int myId, Flat flat, String name, Date in, Date out) {
	id = myId;
	this.flat = flat;
	this.name = name;
	moveIn = in;
	moveOut = out;
	setPersonCount(new PersonCount(this));
    }

    public Tenant(int myId, Flat flat, String name) {
	this(myId, flat, name, loadDateIn(myId), loadDateOut(myId));
    }

    public Tenant(int myId, Flat flat) {
	this(myId, flat, loadName(myId));
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public static LinkedList<Tenant> loadTenants(Flat flat) {
	LinkedList<Tenant> tenants = new LinkedList<Tenant>();
	LinkedList<Row> rows = DbHandle.findAll(TENANT_TABLE, TENANT_FLAT, flat.getId());
	int tenantID;
	for (Row r : rows) {
	    tenantID = r.getInt(TENANT_ID);
	    tenants.add(new Tenant(tenantID, flat));
	}
	return tenants;
    }

    private static Object loadTenantData(int myId, String colName) {
	Table tbl = DbHandle.getTable(TENANT_TABLE);
	String idCol = tbl.getColumns().get(0).getName();

	return DbHandle.findUnique(tbl, idCol, new Integer(myId)).get(colName);
    }

    public static String loadName(int myId) {
	String name = (String) loadTenantData(myId, TENANT_NAME);
	return name;
    }

    /**
     * 
     * @param myId
     * @return Date the tenant moved in
     */
    private static Date loadDateIn(int myId) {
	return (Date) loadTenantData(myId, TENANT_MOVE_IN);
    }

    /**
     * Find the date a tenant moved out, if no date exists the date is set to the
     * latest date that can be displayed
     * 
     * @param myId
     * @return
     */
    private static Date loadDateOut(int myId) {
	try {
	    // It could happen that no Date is found, in this case it is okay
	    ErrorHandle.silence();
	    Date d = (Date) loadTenantData(myId, TENANT_MOVE_OUT);
	    ErrorHandle.activate();
	    return d;
	} catch (InvalidParameterException e) {
	    return new Date(Long.MAX_VALUE);
	}
    }

    public void setMoveOut(Date d) {
	this.moveOut = d;
    }

    public double getPersonCount(int year) {
	return personCount.getCountAverage(year);
    }

    /**
     * returns latest personcount
     * 
     * @return
     */
    public PersonCount getPersonCount() {
	return personCount;
    }

    @Override
    public Flat getFlat() {
	return flat;
    }

    @Override
    public String toString() {

	if (getMoveOut().after(Calendar.getInstance().getTime()))
	    return name;
	else {
	    return name + "(a)";
	}
    }

    public Date getMoveIn() {
	return moveIn;
    }

    public Date getMoveOut() {
	if (moveOut == null) {
	    return new Date(Long.MAX_VALUE);
	}
	return moveOut;
    }

    @Override
    public House getHouse() {
	return flat.getHouse();
    }

    public void setMoveIn(Date d) {
	moveIn = d;
    }

    public void setPersonCount(PersonCount personCount) {
	this.personCount = personCount;
    }

    @Override
    public int compareTo(Tenant t) {
	return -this.getMoveIn().compareTo(t.getMoveIn());

    }

    public double getRent(int year) {
	double rent = 0f;
	int lastYear = 0;
	int y = 0;
	for (Row r : DbHandle.findAll(RENT_TABLE, RENT_TENANT, id)) {
	    y = r.getInt(RENT_YEAR);
	    if (y <= year && y > lastYear) {
		rent = r.getDouble(RENT_COST);
		lastYear = y;
	    }
	}
	return rent;
    }

    public int getLastRentYear() {
	LinkedList<Row> rows = DbHandle.findAll(RENT_TABLE, RENT_TENANT, getId());
	if (rows.isEmpty())
	    return 0;
	int val = rows.getLast().getInt(RENT_YEAR);
	return val;
    }

    public double getPayment(Date year) {
	Calendar cal = Calendar.getInstance();
	cal.setTime(year);
	int intyear = cal.get(Calendar.YEAR);
	for (Row r : DbHandle.findAll(TENANT_MONEY_TABLE, TENANT_MONEY_TENANT, id)) {
	    cal.setTime(r.getDate(TENANT_MONEY_DATE));
	    if (intyear == cal.get(Calendar.YEAR)) {
		return r.getDouble(TENANT_MONEY_MONEY);
	    }
	}
	return 0.0;
    }

    public double getHeaterCost(int year) {
	double balance = 0f;
	for (Row r : DbHandle.findAll(HEATER_TABLE, HEATER_TENANT, id)) {
	    if (r.getInt(HEATER_YEAR).intValue() == year)
		return r.getDouble(HEATER_COST).doubleValue();
	}
	return balance;
    }

    public int getId() {
	return id;
    }

    /**
     * 
     * @param years
     *            time period tenants are loaded in
     * @return all tenants who lived in a flat within last x years
     */
    public static LinkedList<Tenant> loadAll(int years) {

	Calendar cal = Calendar.getInstance();
	cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - years);
	Date loadMark = new Date(cal.getTimeInMillis());

	LinkedList<Tenant> tenant = new LinkedList<>();
	for (Flat f : Flat.loadFlats()) {
	    if (years <= 0) {
		tenant.addAll(loadTenants(f));
	    } else {
		for (Tenant t : loadTenants(f)) {
		    if (t.getMoveOut().after(loadMark)) {
			tenant.add(t);
		    }
		}
	    }
	}
	Collections.sort(tenant);
	return tenant;
    }

    // GARAGE
    // STUFF-----------------------------------------------------------------------------------

    private LinkedList<TimeAndCost> garageCost;
    private int garageYear;

    public LinkedList<TimeAndCost> getGarageCost(int year) {

	if (garageCost != null && garageYear == year) {
	    return garageCost;
	}
	garageCost = GarageTenant.loadCosts(this, year);
	garageYear = year;
	return garageCost;

    }

    /**
     * returns amount of month garages were rented, can be > 12 if multiple garages
     * were rented
     * 
     * @param year
     * @return
     */
    public int getGarageUsage(int year) {

	return getGarageCost(year).stream().mapToInt(tc -> tc.getMonth()).sum();
    }

    public double getGarageRent(int year) {
	return getGarageCost(year).stream().mapToDouble(tc -> tc.getCost()).sum();
    }

    public double getFutureGarageRent(int year) {
	return getGarageCost(year).stream().mapToDouble(tc -> tc.getNewMonthly()).sum();
    }

    // STELLPLAETZE STUFF
    // --------------------------------------------------------------------------------------------------

    private LinkedList<TimeAndCost> stellpCost;
    private int stellpYear;

    public LinkedList<TimeAndCost> getStellPCost(int year) {

	if (stellpCost != null && stellpYear == year) {
	    return stellpCost;
	}
	stellpCost = StellpTenant.loadCosts(this, year);
	stellpYear = year;
	return stellpCost;

    }

    public double getCableProvidingCost(int year) {
	double value = 0f;
	int latest = 0;
	int rowyear;
	for (Row r : DbHandle.findAll(PROVIDINGFEE_TABLE, PROVIDINGFEE_TENANT, getId())) {
	    if ((rowyear = r.getInt(PROVIDINGFEE_YEAR)) > latest && rowyear <= year) {
		latest = rowyear;
		value = r.getDouble(PROVIDINGFEE_VALUE);
	    }

	}

	return value;
    }

    public double getHouseCableSupplyCost(int year) {
	double value = 0f;
	int latest = 0;
	int rowyear;
	for (Row r : DbHandle.findAll(HOUSESUPPLY_TABLE, HOUSESUPPLY_TENANT, getId())) {
	    if ((rowyear = r.getInt(HOUSESUPPLY_YEAR)) > latest && rowyear <= year) {
		latest = rowyear;
		value = r.getDouble(HOUSESUPPLY_VALUE);
	    }

	}

	return value;
    }

    /**
     * returns amount of month garages were rented, can be > 12 if multiple garages
     * were rented
     * 
     * @param year
     * @return
     */
    public int getStellPUsage(int year) {

	return getStellPCost(year).stream().mapToInt(tc -> tc.getMonth()).sum();
    }

    public double getStellPRent(int year) {
	return getStellPCost(year).stream().mapToDouble(tc -> tc.getCost()).sum();
    }

    public double getFutureStellPRent(int year) {
	return getStellPCost(year).stream().mapToDouble(tc -> tc.getNewMonthly()).sum();
    }

    public double getPrepayedHeaterCost(int year) {
	LinkedList<Row> rows = DbHandle.findAll(PREPAYED_HEATER_TABLE, PREPAYED_HEATER_TENANT, getId());
	List<Row> found = rows.stream().filter(r -> r.getInt(PREPAYED_HEATER_YEAR) == year)
		.collect(Collectors.toList());
	if (found.size() == 0)
	    return 0.0;
	else {
	    return found.get(0).getDouble(PREPAYED_HEATER_PAYED);
	}
    }

    public class Result {
	public String description;
	public double value;

	public Result(String s, double v) {
	    description = s;
	    value = v;
	}

	public Result() {
	    description = "Keine";
	    value = 0.0;
	}
    }

    public Result getSonstige(int year) {
	Row r = DbHandle.findUnique(SONSTIGE_TABLE, SONSTIGE_TENANT, getId(), SONSTIGE_YEAR, year);

	if (r != null) {
	    return new Result(r.getString(SONSTIGE_DESCRIPTION), r.getDouble(SONSTIGE_VALUE));
	}
	return new Result();

    }

    public Result getModernisierung(int year) {
	Row r = DbHandle.findUnique(MODERN_TABLE, MODERN_TENANT, getId(), MODERN_YEAR, year);

	if (r != null) {
	    return new Result(r.getString(MODERN_DESCRIPTION), r.getDouble(MODERN_VALUE));
	}
	return new Result();

    }
}
