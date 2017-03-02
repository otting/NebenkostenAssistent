package util;

import java.util.Date;

public abstract class Bill {

    private double hotWater, coldWater, electric, heater, personCount = 0;
    private Date start, end;

    public double getHotWater() {
	return hotWater;
    }

    public void setHotWater(double hotWater) {
	this.hotWater = hotWater;
    }

    public double getColdWater() {
	return coldWater;
    }

    public void setColdWater(double coldWater) {
	this.coldWater = coldWater;
    }

    public double getElectric() {
	return electric;
    }

    public Date getStart() {
	return start;
    }

    public void setStart(Date start) {
	this.start = start;
    }

    public void setElectric(double electric) {
	this.electric = electric;
    }

    public double getHeater() {
	return heater;
    }

    public void setHeater(double heater) {
	this.heater = heater;
    }

    public double getPersonCount() {
	return personCount;
    }

    public void setPersonCount(double personCount) {
	this.personCount = personCount;
    }

    public abstract int getShare();

    public Date getEnd() {
	return end;
    }

    public void setEnd(Date end) {
	this.end = end;
    }

}
