package util;

import java.util.Date;

import javax.naming.directory.InvalidAttributesException;

import db.House;

public class HouseBill extends Bill {

    private House house;
    private int year, totalShare;
    private double squareMeter = 0;

    public HouseBill(House h, Date start, Date end, int year) {
	setHouse(h);
	setStart(start);
	setEnd(end);
	setYear(year);
    }

    public House getHouse() {
	return house;
    }

    public void setHouse(House house) {
	this.house = house;
    }

    public void setSquareMeter(double sm) {
	squareMeter = sm;
    }

    public void addSquareMeter(double sm) {
	setSquareMeter(getSquareMeter() + sm);
    }

    public double getSquareMeter() {
	return squareMeter;
    }

    public int getFlatCount() {
	return house.getFlats().size();
    }

    public int getYear() {
	return year;
    }

    public void setYear(int year) {
	this.year = year;
    }

    public double getTrashCost() throws InvalidAttributesException {
	double cost = house.getTrashCost(year);

	if (cost <= 0) {
	    throw new InvalidAttributesException("Trash cost is 0");
	}
	return cost;
    }

    public double getWaterCost() throws InvalidAttributesException {
	double cost = house.getWaterCost(year);
	if (cost <= 0) {
	    throw new InvalidAttributesException("Water cost is 0");
	}
	return cost;
    }

    public double getRainCost() throws InvalidAttributesException {
	double cost = house.getRainCost(year);
	if (cost <= 0) {
	    throw new InvalidAttributesException("Rain cost is 0");
	}
	return cost;
    }

    public double getWasteWaterCost() throws InvalidAttributesException {
	double cost = house.getWasteWaterCost(year);
	if (cost <= 0) {
	    throw new InvalidAttributesException("Waste water cost is 0");
	}
	return cost;
    }

    public double getCableCost() throws InvalidAttributesException {
	double cost = house.getCableCost(year);
	if (cost <= 0) {
	    throw new InvalidAttributesException("Cable cost is 0");
	}
	return cost;
    }

    public double getBaseTax() {
	return house.getBaseTax(getYear());
    }

    public double getHotWaterCost() throws InvalidAttributesException {
	double cost = house.getHotWaterCost(year);
	if (cost <= 0) {
	    throw new InvalidAttributesException("Hot water cost is 0");
	}
	return cost;
    }

    @Override
    public int getShare() {
	return house.getShare();
    }

    public double getEnsuranceCost() throws InvalidAttributesException {
	double cost = house.getInsuranceCost(year);
	if (cost <= 0) {
	    throw new InvalidAttributesException("Ensurance cost is 0");
	}
	return cost;
    }

    public double getCommonElectricCost() throws InvalidAttributesException {
	double cost = house.getCommonElectricCost(year);
	if (cost <= 0) {
	    throw new InvalidAttributesException("Cable cost is 0");
	}
	return cost;
    }

    public int getTotalShare() {
	return totalShare;
    }

    public void setTotalShare(int totalShare) {
	this.totalShare = totalShare;
    }
}
