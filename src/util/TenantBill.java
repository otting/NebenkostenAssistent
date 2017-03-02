package util;

import db.Flat;
import db.Tenant;

public class TenantBill extends Bill {
    private Flat flat;
    private Tenant tenant;
    private double payed;
    private double rent;

    public TenantBill(Tenant t) {
	setTenant(t);
    }

    public Tenant getTenant() {
	return tenant;
    }

    public double getSquareMeter() {
	return tenant.getFlat().getSquaremeter();
    }

    public void setTenant(Tenant tenant) {
	this.tenant = tenant;
    }

    @Override
    public int getShare() {
	return tenant.getFlat().getShare();
    }

    public void setBalance(double pay) {
	payed = pay;
    }

    public double getBalance() {
	return payed;
    }

    public double getRent() {
	return rent;
    }

    public void setRent(double rent) {
	this.rent = rent;
    }

    public Flat getFlat() {
	if (flat == null) {
	    return flat = getTenant().getFlat();
	}
	return flat;
    }

    public void setFlat(Flat flat) {
	this.flat = flat;
    }
}
