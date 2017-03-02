package db;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.LinkedList;

public class LoadFabric {
    public static final int FLAT = 0, HOUSE = 1, METER = 2, TENANT = 3;

    /**
     * 
     * @param type
     * @param years:
     *            Period of time that older entry's are loaded in (does not
     *            influence flats and houses)
     * @return
     */
    public static LinkedList<LoadAble> load(Type type, int years) {

	LinkedList<LoadAble> list = new LinkedList<LoadAble>();
	switch (type) {
	case Flat:
	    list.addAll(Flat.loadFlats());
	    break;
	case House:
	    list.addAll(House.loadHouses());
	    break;
	case Meter:
	    list.addAll(Meter.loadAll(years));
	    break;
	case Tenant:
	    LinkedList<Tenant> ts = Tenant.loadAll(years);
	    Collections.sort(ts);
	    list.addAll(ts);
	    break;
	default:
	    throw new InvalidParameterException("invalid Type");
	}

	return list;
    }
}
