package db.input;

import java.util.Calendar;
import java.util.Date;

import com.healthmarketscience.jackcess.Row;

import db.DbHandle;
import db.DbNames;
import db.House;

public class HouseInput implements DbNames {

    public static void deleteInsurance(String type, int houseID, int year) {
	for (Row r : DbHandle.findAll(INSURANCE_TABLE, INSURANCE_HOUSE, houseID)) {
	    if (r.getInt(INSURANCE_YEAR).intValue() == year && r.getString(INSURANCE_TYPE).equals(type)) {
		DbInput.removeRow(INSURANCE_TABLE, r);
	    }
	}
    }

    public static void addInsurance(String type, House house, int year, double cost) {
	addInsurance(type, house.getId(), year, cost);
    }

    public static void addInsurance(String type, int houseID, int year, double cost) {
	deleteInsurance(type, houseID, year);
	DbInput.addRow(INSURANCE_TABLE,
		new String[] { INSURANCE_COST, INSURANCE_TYPE, INSURANCE_YEAR, INSURANCE_HOUSE }, cost, type, year,
		houseID);
    }

    private static void removeCost(String table, String colYear, String houseColId, int year, double cost, House h) {
	DbInput.removeRow(table, houseColId, h.getId(), colYear, year);
    }

    public static void setGeneralElectric(int year, double cost, House h) {

	removeCost(COMMON_TABLE, COMMON_YEAR, COMMON_HOUSE, year, cost, h);
	DbInput.addRow(COMMON_TABLE, new String[] { COMMON_YEAR, COMMON_HOUSE, COMMON_COST }, year, h.getId(), cost);
    }

    public static void setCabelCost(int year, double cost, House h) {
	removeCost(CABLE_TABLE, CABLE_YEAR, CABLE_HOUSE, year, cost, h);
	DbInput.addRow(CABLE_TABLE, new String[] { CABLE_YEAR, CABLE_HOUSE, CABLE_COST }, year, h.getId(), cost);
    }

    public static void setTrashCost(int year, double cost, House h) {
	removeCost(TRASH_TABLE, TRASH_YEAR, TRASH_HOUSE, year, cost, h);

	DbInput.addRow(TRASH_TABLE, new String[] { TRASH_HOUSE, TRASH_YEAR, TRASH_COST }, h.getId(), year, cost);
    }

    public static void setWasteWaterCost(int year, double cost, House h) {
	removeCost(WASTEW_TABLE, WASTEW_YEAR, WASTEW_HOUSE, year, cost, h);
	DbInput.addRow(WASTEW_TABLE, new String[] { WASTEW_HOUSE, WASTEW_YEAR, WASTEW_COST }, h.getId(), year, cost);
    }

    public static void setRainCost(int year, double cost) {
	DbInput.removeRow(RAIN_TABLE, RAIN_YEAR, year);
	DbInput.addRow(RAIN_TABLE, new String[] { RAIN_YEAR, RAIN_COST }, year, cost);
    }

    public static void setWaterCost(int year, double cost, House h) {
	removeCost(WATER_TABLE, WATER_YEAR, WATER_HOUSE, year, cost, h);
	DbInput.addRow(WATER_TABLE, new String[] { WATER_YEAR, WATER_HOUSE, WATER_COST }, year, h.getId(), cost);
    }

    public static void setHotWaterCost(int year, double cost, House h) {
	removeCost(HOTWATER_TABLE, HOTWATER_YEAR, HOTWATER_HOUSE, year, cost, h);
	DbInput.addRow(HOTWATER_TABLE, new String[] { HOTWATER_YEAR, HOTWATER_HOUSE, HOTWATER_COST }, year, h.getId(),
		cost);
    }

    public static void setGardenCost(double cost, Date d) {
	Calendar cal = Calendar.getInstance();
	cal.setTime(d);
	cal.set(Calendar.HOUR_OF_DAY, 0);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);
	cal.set(Calendar.MILLISECOND, 0);
	d.setTime(cal.getTimeInMillis());
	DbInput.addRow(GARDEN_TABLE, new String[] { GARDEN_DATE, GARDEN_VALUE }, d, cost);
    }

    public static void removeGardenCost(double cost, Date d) {
	DbInput.removeRow(GARDEN_TABLE, GARDEN_DATE, d, GARDEN_VALUE, cost);
    }

    public static void setBaseTax(int year, double cost, House h) {
	removeCost(BASE_TAX_TABLE, BASE_TAX_YEAR, BASE_TAX_HOUSE, year, cost, h);
	DbInput.addRow(BASE_TAX_TABLE, new String[] { BASE_TAX_YEAR, BASE_TAX_VALUE, BASE_TAX_HOUSE }, year, cost,
		h.getId());
    }
}
