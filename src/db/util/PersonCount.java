package db.util;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import com.healthmarketscience.jackcess.Row;

import db.DbHandle;
import db.DbNames;
import db.Tenant;

public class PersonCount implements DbNames {

    private Tenant t;
    private LinkedList<Count> counts;

    public PersonCount(Tenant t) {

	setTenant(t);
	counts = new LinkedList<>();

	for (Row r : DbHandle.findAll(PEOPLE_COUNT_TABLE, PEOPLE_COUNT_TENANT_ID, getTenant().getId())) {
	    counts.add(new Count(r.getDate(PEOPLE_COUNT_DATE), r.getInt(PEOPLE_COUNT).intValue()));
	}
    }

    public Tenant getTenant() {
	return t;
    }

    public void setTenant(Tenant t) {
	this.t = t;
    }

    class Count {
	int count;
	Date date;

	public Count(Date d, int c) {
	    count = c;
	    date = d;
	}

	public String toString() {
	    return date + " : " + count;
	}
    }

    /**
     * 
     * @param year
     * @return average count in year
     */
    public float getCountAverage(int year) {

	float avg = getCountSum(year);
	int month = 0;
	for (int i = 0; i < 12; i++) {
	    month += (getCount(year, i) > 0) ? 1 : 0;
	}

	return avg / month;
    }

    public int getCountSum(int year) {
	int sum = 0;
	int count;
	for (int i = 0; i < 12; i++) {
	    count = getCount(year, i);
	    sum += count;
	}
	return sum;
    }

    int[] countPerMonth = new int[12];

    /**
     * 
     * @param year
     * @param month
     *            from 0 to 11 with 0 = January
     * @return
     */
    public int getCount(int year, int month) {

	Calendar cal = Calendar.getInstance();
	cal.set(year, month, 1);
	if (cal.getTime().after(getTenant().getMoveOut()) || cal.getTime().before(t.getMoveIn()))
	    return 0;

	if (!Arrays.equals(countPerMonth, new int[12])) {
	    return countPerMonth[month];
	}

	int lastCount = 0;
	int lastMonth = 0;
	int beginMonth = 1;
	for (Count c : counts) {
	    cal.setTime(c.date);
	    if (cal.get(Calendar.YEAR) < year) {
		lastCount = c.count;
	    } else if (cal.get(Calendar.YEAR) == year) {
		beginMonth = (cal.get(Calendar.DAY_OF_MONTH) >= 15) ? 0 : 1;
		markCount(lastMonth, cal.get(Calendar.MONTH) - beginMonth, lastCount);
		lastMonth = cal.get(Calendar.MONTH) - beginMonth + 1;
		lastCount = c.count;

	    } else {
		break;
	    }
	}
	if (lastMonth < 11) {
	    int to = 11;
	    cal.setTime(t.getMoveOut());
	    if (cal.get(Calendar.YEAR) == year) {
		to = cal.get(Calendar.MONTH);
		to -= (cal.get(Calendar.DAY_OF_MONTH) < 15) ? 1 : 0;
	    }
	    markCount(lastMonth, to, lastCount);
	}

	return countPerMonth[month];

    }

    private void markCount(int from, int to, int count) {
	to = (to >= 12) ? 11 : to;
	from = (from < 0) ? 0 : from;
	for (; from <= to; from++) {
	    countPerMonth[from] = count;
	}
    }

    public int getLast() {
	if (counts.isEmpty())
	    return 0;
	return counts.getLast().count;
    }

    public Date getLastDate() {
	return counts.getLast().date;
    }

}
