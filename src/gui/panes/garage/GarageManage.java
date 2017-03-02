package gui.panes.garage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import db.Garage;
import db.Tenant;
import db.util.GarageTenant;

public class GarageManage extends ObjectManageDummy {

    /**
     * 
     */
    private static final long serialVersionUID = -2694201772684777502L;

    LinkedList<Garage> garagen;
    private GarageTenant gtenant;
    Garage loaded;

    public GarageTenant getTenant() {
	if (getSelectedIndex() == -1) {
	    return new GarageTenant("", new Date(0), 0.0);
	}
	Garage g = garagen.get(getSelectedIndex());
	if (!g.equals(loaded)) {
	    loaded = g;
	    gtenant = GarageTenant.loadTenant(g);
	}
	return gtenant;

    }

    @Override
    public String getMieter() {
	return getTenant().getName();
    }

    @Override
    public String getLastChange() {
	SimpleDateFormat form = new SimpleDateFormat("dd.MM.yyyy");
	Date d = getTenant().getDate();
	if (d.equals(new Date(0))) {
	    return "nie";
	} else {
	    return form.format(d);
	}
    }

    @Override
    public double getMiete() {
	return getTenant().getRent();
    }

    @Override
    public LinkedList<String> loadObjects() {
	garagen = Garage.loadAll();
	LinkedList<String> names = new LinkedList<String>();
	for (Garage g : garagen) {
	    names.add(g.getName() + " > " + GarageTenant.loadTenant(g).getName());
	}
	return names;
    }

    @Override
    public String getObjectDescription() {
	return "Garage";
    }

    @Override
    public void creatObject() {
	ObjectCreate.open(this);
    }

    @Override
    public void setTenant(Tenant t, Date d, Double rent) {
	garagen.get(getSelectedIndex()).setTenant(t, d, rent);
    }

    @Override
    public void setTenant(String tenant, Date d, Double rent) {
	garagen.get(getSelectedIndex()).setTenant(tenant, d, rent);
    }

}
