package db;

import java.util.Date;

public interface IextraSpace {
    /**
     * used if the tenant for this extra space is NOT a regular tenant from
     * flats
     * 
     * @param tenant
     */
    public void setTenant(String tenant, Date date, double cost);

    public void setTenant(Tenant tenant, Date date, double cost);

    /**
     * Used if the extra space is not rented after the given date
     * 
     * @param d
     */
    public void setNoTenant(Date date);

    public String toString();

}
