package db;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.LinkedList;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

public class House implements DbNames, LoadAble {

    int id, share, zip;
    String description, adress;
    private static LinkedList<House> houses;

    public House(int id, int share, String description, int zip, String adress) {
	this.id = id;
	this.share = share;
	this.description = description;
	this.zip = zip;
	this.adress = adress;
    }

    public static LinkedList<House> loadHouses() {
	int id, share, zip;
	String desc, adress;
	Table tbl = DbHandle.getTable(HOUSE_TABLE);

	LinkedList<House> all = new LinkedList<House>();

	for (Row r : tbl) {
	    id = r.getInt(tbl.getColumns().get(0).getName());
	    share = r.getInt(HOUSE_SHARE);
	    desc = r.getString(HOUSE_DESCRIPTION);
	    adress = r.getString(HOUSE_STREET) + " " + r.getString(HOUSE_NUMBER);
	    zip = r.getInt(HOUSE_ZIP).intValue();

	    all.add(new House(id, share, desc, zip, adress));
	}
	houses = all;
	return all;
    }

    public double getPersonCount(int year) {
	double sum = 0f;
	for (Flat f : getFlats()) {
	    sum += f.getAveragePeopleCount(year);
	}
	return sum;
    }

    public double getWaterCost(int year) {
	LinkedList<Row> rows = DbHandle.findAll(WATER_TABLE, WATER_HOUSE, id);
	double cost = 0.0;
	for (Row r : rows) {
	    if (r.getInt(WATER_YEAR) == year) {
		cost += r.getDouble(WATER_COST);
	    }
	}

	return cost;
    }

    public double getTrashCost(int year) {
	LinkedList<Row> rows = DbHandle.findAll(TRASH_TABLE, TRASH_HOUSE, id);
	double cost = 0.0;
	for (Row r : rows) {
	    if (r.getInt(TRASH_YEAR) == year) {
		cost += r.getDouble(TRASH_COST);
	    }
	}

	return cost;
    }

    public double getGardenCost(int year) {

	return getTotalGardenCost(year) * getShare() / getTotalShares(year);
    }

    public static double getTotalGardenCost(int year) {
	Table tbl = DbHandle.getTable(GARDEN_TABLE);
	Calendar cal = Calendar.getInstance();
	double sum = 0;
	for (Row r : tbl) {
	    cal.setTime(r.getDate(GARDEN_DATE));
	    if (cal.get(Calendar.YEAR) == year) {
		sum += r.getDouble(GARDEN_VALUE);
	    }
	}
	return sum;
    }

    public double getRainCost(int year) {
	return getAllRainCost(year) * getShare() / getTotalShares(year);
    }

    public double getWasteWaterCost(int year) {
	LinkedList<Row> rows = DbHandle.findAll(WASTEW_TABLE, WASTEW_HOUSE, id);
	double cost = 0.0;
	for (Row r : rows) {
	    if (r.getInt(WASTEW_YEAR) == year) {
		cost += r.getDouble(WASTEW_COST);
	    }
	}

	return cost;
    }

    public double getCableCost(int year) {

	double cost = 0.0;
	for (Row r : DbHandle.findAll(CABLE_TABLE, CABLE_HOUSE, getId())) {
	    if (r.getInt(CABLE_YEAR) <= year) {
		cost = r.getDouble(CABLE_COST);
	    }
	}

	return cost;
    }

    public double getHotWaterCost(int year) {
	double cost = 0.0;
	LinkedList<Row> rows = DbHandle.findAll(HOTWATER_TABLE, HOTWATER_HOUSE, id);
	for (Row r : rows) {
	    if (r.getInt(HOTWATER_YEAR) == year) {
		cost += r.getDouble(HOTWATER_COST);
	    }
	}

	return cost;
    }

    public int getId() {
	return id;
    }

    public int getShare() {
	Row r = DbHandle.findId(id, DbHandle.getTable(HOUSE_TABLE));
	return r.getInt(HOUSE_SHARE);
    }

    public LinkedList<Flat> getFlats() {
	return Flat.loadFlats(this);
    }

    @Override
    public String toString() {
	return description;
    }

    public LinkedList<String> getInsurances(int year) {
	LinkedList<String> insurance = new LinkedList<>();
	LinkedList<Row> rows = DbHandle.findAll(INSURANCE_TABLE, INSURANCE_YEAR, year);
	for (Row r : rows) {
	    if (r.getInt(INSURANCE_HOUSE).intValue() == getId())
		insurance.add(r.getString(INSURANCE_TYPE) + ": " + r.getDouble(INSURANCE_COST) + "€");
	}
	return insurance;

    }

    public static double getTotalInsurance(int year) {
	double sum = 0;
	for (House h : loadHouses()) {
	    sum += h.getInsurance(year);
	}

	return sum;
    }

    /**
     * @param year
     * @return total insurance cost in year x
     */
    public double getInsurance(int year) {
	double cost = 0.0;
	LinkedList<Row> rows = DbHandle.findAll(INSURANCE_TABLE, INSURANCE_YEAR, year);
	for (Row r : rows) {
	    if (r.getInt(INSURANCE_HOUSE).intValue() == getId())
		cost += r.getDouble(INSURANCE_COST);
	}
	return cost;
    }

    public static int getTotalShares(int year) {
	Table tbl = DbHandle.getTable(TOTALSHARE_TABLE);
	int lastYear = 0;
	Row last = null;
	for (Row r : tbl) {
	    if (r.getInt(TOTALSHARE_YEAR) > lastYear && r.getInt(TOTALSHARE_YEAR) <= year) {
		last = r;
		lastYear = r.getInt(TOTALSHARE_YEAR);
	    }
	}

	return last.getInt(TOTALSHARE_SHARE);
    }

    public double getInsuranceCost(int year) {
	return getInsurance(year);
    }

    public double getCommonElectricCost(int year) {
	double cost = 0.0;
	LinkedList<Row> rows = DbHandle.findAll(COMMON_TABLE, COMMON_HOUSE, id);
	int costYear = 0;
	for (Row r : rows) {
	    costYear = r.getInt(COMMON_YEAR);
	    if (costYear == year) {
		cost += r.getDouble(COMMON_COST);
	    }
	}
	return cost;
    }

    public static House getHouse(int id) {
	if (houses == null) {
	    loadHouses();
	}
	for (House h : houses) {
	    if (h.id == id) {
		return h;
	    }
	}
	throw new InvalidParameterException("House with id " + id + " was not found");
    }

    @Override
    public House getHouse() {
	return this;
    }

    public boolean equals(House h) {
	return (h.id == id);
    }

    /**
     * special case of LoadAble method, returns first flat in the house for further
     * usage
     */
    @Override
    public Flat getFlat() {
	LinkedList<Flat> flat = getFlats();
	if (flat.isEmpty())
	    return null;
	else {
	    return flat.getFirst();
	}
    }

    public double getBaseTax(int year) {

	double val = 0;
	for (Flat f : getFlats()) {
	    val += f.getGrundsteuer(year);
	}

	return val;
    }

    public static double getAllRainCost(int year) {
	LinkedList<Row> rows = DbHandle.findAll(RAIN_TABLE, RAIN_YEAR, year);
	double cost = 0.0;
	for (Row r : rows) {
	    cost += r.getDouble(RAIN_COST);
	}

	return cost;
    }
}
