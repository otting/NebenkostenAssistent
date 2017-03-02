package db.util;

import java.util.Date;

public class TimeAndCost {
    private Long time;
    private double cost;
    private double newMonthly;

    public TimeAndCost() {
	this(0l, 0.0, 0.0);
    }

    public TimeAndCost(Long time, double cost, double newMonthly) {
	setTime(time);
	setCost(cost);
	setNewMonthly(newMonthly);
    }

    public int getDays() {
	return (int) Time.millisecondsToDays(time);
    }

    public int getMonth() {
	return Time.millisecondsToMonth(time);
    }

    public Long getTime() {
	return time;
    }

    public void setTime(Long time) {
	this.time = time;
    }

    public double getCost() {
	return cost;
    }

    public void setCost(double cost) {
	this.cost = cost;
    }

    public double getNewMonthly() {
	return newMonthly;
    }

    public void setNewMonthly(double newMonthly) {
	this.newMonthly = newMonthly;
    }

    /**
     * Adds time between date a and b, where b comes after a
     * 
     * @param a
     * @param b
     */
    public void addTime(Date a, Date b) {
	if (a.getTime() > b.getTime())
	    addTime(0);
	else
	    addTime(b.getTime() - a.getTime());
    }

    public void addTime(long dong) {
	setTime(getTime() + dong);
    }

    public void addCost(double cost) {
	setCost(getCost() + cost);
    }

}
