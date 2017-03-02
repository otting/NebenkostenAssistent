package db;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;

import com.healthmarketscience.jackcess.Row;

import db.input.DbInput;

public class StellP implements DbNames, IextraSpace {

    private String name;
    private int id;

    public StellP(int id, String name) {
	setName(name);
	setId(id);
    }

    /**
     * creates a new entry in the database
     * 
     * @param name
     */
    public static void createStellP(String name) {
	DbInput.addRow(STELLP_TABLE, new String[] { STELLP_NAME }, name);
    }

    public static LinkedList<StellP> loadAll() {
	LinkedList<StellP> list = new LinkedList<>();
	int id;
	String name;
	for (Row r : DbHandle.getTable(STELLP_TABLE)) {
	    id = r.getInt(STELLP_ID);
	    name = r.getString(STELLP_NAME);
	    list.add(new StellP(id, name));
	}
	return list;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String toString() {
	return getName();
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    @Override
    public void setTenant(String tenant, Date date, double cost) {
	DbInput.addRow(STELLP_EXTERN_TABLE, new String[] { STELLP_EXTERN_TENANT, STELLP_EXTERN_START,
		STELLP_EXTERN_RENT, STELLP_EXTERN_STELLPLATZ }, tenant, date, new BigDecimal(cost), getId());
    }

    @Override
    public void setTenant(Tenant tenant, Date date, double cost) {
	DbInput.addRow(
		STELLP_CONTRACT_TABLE, new String[] { STELLP_CONTRACT_STELLPLATZ, STELLP_CONTRACT_TENANT,
			STELLP_CONTRACT_START, STELLP_CONTRACT_RENT },
		getId(), tenant.getId(), date, new BigDecimal(cost));
    }

    @Override
    public void setNoTenant(Date date) {
	setTenant("Leerstand", date, 0.0);
    }
}
