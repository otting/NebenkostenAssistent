package gui.panes.garage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import db.StellP;
import db.Tenant;
import db.util.StellpTenant;

public class StellPManage extends ObjectManageDummy {

    /**
     * 
     */
    private static final long serialVersionUID = -819148866742279597L;

    LinkedList<StellP> stellplaetze;
    private StellpTenant stpTenant;
    StellP loaded;

    @Override
    public void setTenant(Tenant t, Date d, Double rent) {
	stellplaetze.get(getSelectedIndex()).setTenant(t, d, rent);
    }

    @Override
    public void setTenant(String tenant, Date d, Double rent) {
	stellplaetze.get(getSelectedIndex()).setTenant(tenant, d, rent);
    }

    public StellpTenant getTenant() {
	if (getSelectedIndex() == -1) {
	    return new StellpTenant("", new Date(0), 0.0);
	}
	StellP stp = stellplaetze.get(getSelectedIndex());
	if (!stp.equals(loaded)) {
	    loaded = stp;
	    stpTenant = StellpTenant.loadTenant(stp);
	}
	return stpTenant;
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
	LinkedList<String> strings = new LinkedList<>();
	stellplaetze = StellP.loadAll();
	for (StellP sp : stellplaetze) {
	    strings.add(sp.toString());
	}
	return strings;
    }

    @Override
    public void creatObject() {
	ObjectCreate.open(this);
    }

    @Override
    public String getObjectDescription() {
	return "Stellplatz";
    }

}
