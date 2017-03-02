package db;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;

import com.healthmarketscience.jackcess.Row;

import db.input.DbInput;
import db.util.GarageNebenkosten;

public class Garage implements DbNames, IextraSpace {

    /**
     * used to distinct a garage from a house in the insurance table
     */
    public final static int pseudoHouseID = -1;

    private String name;
    private int id;

    public Garage(int id, String name) {
	this.setId(id);
	this.setName(name);
    }

    public static LinkedList<Garage> loadAll() {
	LinkedList<Garage> list = new LinkedList<Garage>();
	int id;
	String name;
	for (Row r : DbHandle.getTable(GARAGE_TABLE)) {
	    id = r.getInt(GARAGE_ID);
	    name = r.getString(GARAGE_NAME);
	    list.add(new Garage(id, name));
	}
	return list;
    }

    public static double getTotalCost(int year) {
	double sum = 0.0;
	for (GarageNebenkosten gn : GarageNebenkosten.loadFor(year)) {
	    sum += gn.getValue();
	}

	return sum;
	// double insurance = getInsuranceCost(year);
	// double garden = House.getTotalGardenCost(year);
	// double rain = House.getAllRainCost(year);
	// return insurance + (garden + rain) * getShare(year) /
	// House.getTotalShares(year);
    }

    @SuppressWarnings("unused")
    private static int getShare(int year) {
	int total = House.getTotalShares(year);
	int houseShare = 0;
	for (House h : House.loadHouses()) {
	    houseShare += h.getShare();
	}
	return total - houseShare;
    }

    public static void createGarage(String name) {
	DbInput.addRow(GARAGE_TABLE, new String[] { GARAGE_NAME }, name);
    }

    @Override
    public void setTenant(String tenant, Date date, double cost) {
	DbInput.addRow(GARAGE_EXTERN_TABLE,
		new String[] { GARAGE_EXTERN_TENANT, GARAGE_EXTERN_DATE, GARAGE_EXTERN_RENT, GARAGE_EXTERN_GARAGE },
		tenant, date, new BigDecimal(cost), getId());
    }

    @Override
    public void setTenant(Tenant tenant, Date date, double cost) {
	DbInput.addRow(
		GARAGE_CONTRACT_TABLE, new String[] { GARAGE_CONTRACT_GARAGE, GARAGE_CONTRACT_TENANT,
			GARAGE_CONTRACT_START, GARAGE_CONTRACT_RENT },
		getId(), tenant.getId(), date, new BigDecimal(cost));
    }

    @Override
    public void setNoTenant(Date date) {
	setTenant("Leerstand", date, 0.0);
    }

    public static double getNebenkosten(int year) {
	return GarageNebenkosten.loadFor(year).parallelStream().mapToDouble(gn -> gn.getValue()).sum();
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public static int getCount() {
	return loadAll().size();
    }
}
