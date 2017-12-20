package gui.panes.manage;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import db.Flat;
import db.House;
import db.LoadAble;
import db.Meter;
import db.Tenant;
import db.Type;
import db.input.TenantInput;
import gui.ErrorHandle;
import gui.WorkFrame;
import net.miginfocom.swing.MigLayout;
import util.CustomJList;

public class ManagePane extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -2777617349951317674L;

    private CustomJList house, flat, tenant, meter;
    private JPanel tenantPane, meterPane;
    private TenantOptions tenantOptionPane;
    private MeterOption meterOptions;

    /**
     * Create the panel.
     */
    public ManagePane() {
	setLayout(new GridLayout(1, 0, 0, 0));

	JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
	add(tabbedPane);

	// TENANT
	tenantPane = new JPanel();
	tabbedPane.addTab("Mieter", null, tenantPane, null);
	tenantPane.setLayout(new MigLayout("", "[334px,grow 300][106px,grow]", "[272px]"));
	JPanel tenantSelect = new JPanel();
	tenantPane.add(tenantSelect, "cell 0 0,alignx left,growy");
	tenantOptionPane = new TenantOptions(this);
	tenantPane.add(tenantOptionPane, "cell 1 0,alignx left,growy");
	// LIST INTIT
	int width = WorkFrame.width / 2 / 3;
	tenantSelect.setLayout(new MigLayout("", "[grow][grow][grow]", "[grow]"));
	house = new CustomJList(Type.House);
	house.setPreferredSize(new Dimension(WorkFrame.height, width));
	tenantSelect.add(house, "cell 0 0,grow");
	flat = new CustomJList(Type.Flat);
	flat.setPreferredSize(new Dimension(WorkFrame.height, width));
	tenantSelect.add(flat, "cell 1 0,grow");
	tenant = new CustomJList(Type.Tenant);
	tenantSelect.add(tenant, "cell 2 0,grow");
	meter = new CustomJList(Type.Meter);
	meter.setPreferredSize(new Dimension(WorkFrame.height, width));

	// METER
	meterPane = new JPanel();
	tabbedPane.addTab("Z\u00E4hler", null, meterPane, null);
	meterPane.setLayout(new MigLayout("", "[300px, grow][300px, grow]", "[grow 100][grow]"));

	JPanel meterSelect = new JPanel();
	meterPane.add(meterSelect, "cell 0 0");
	meterSelect.setLayout(new MigLayout("", "[grow][grow][grow]", "[grow]"));
	setMeterOptions(new MeterOption(this));
	meterPane.add(getMeterOptions(), "cell 1 0 ,growy");

	// Listener

	tabbedPane.addChangeListener(new ChangeListener() {

	    @Override
	    public void stateChanged(ChangeEvent e) {
		switch (tabbedPane.getSelectedIndex()) {
		case 0:
		    tenantSelect.add(house, "cell 0 0,grow");
		    tenantSelect.add(flat, "cell 1 0,grow");
		    tenantSelect.add(tenant, "cell 2 0,grow");
		    break;
		case 1:
		    meterSelect.add(house, "cell 0 0,grow");
		    meterSelect.add(flat, "cell 1 0,grow");
		    meterSelect.add(meter, "cell 2 0,grow");
		    break;
		}
	    }
	});

	house.addListSelectionListener(new ListSelectionListener() {

	    @Override
	    public void valueChanged(ListSelectionEvent e) {
		House h = (House) ManagePane.this.house.getSelectedValue();
		flat.clearSelection();
		flat.filter(h);
		flat.repaint();

		meter.clearSelection();
		meter.filter(h);
		meter.repaint();

		tenant.clearSelection();
		tenant.filter(h);
		tenant.repaint();
	    }
	});

	flat.addListSelectionListener(new ListSelectionListener() {

	    @Override
	    public void valueChanged(ListSelectionEvent e) {
		Flat h = (Flat) ManagePane.this.flat.getSelectedValue();
		if (h == null)
		    tenantOptionPane.clear();
		meter.clearSelection();
		meter.filter(h);
		meter.repaint();
		tenant.clearSelection();
		tenant.clearSelection();
		tenant.filter(h);
		tenant.repaint();
	    }
	});

	tenant.addListSelectionListener(new ListSelectionListener() {

	    @Override
	    public void valueChanged(ListSelectionEvent e) {
		if (tenant.getSelectedIndex() != -1) {
		    Tenant t = (Tenant) tenant.getSelectedValue();
		    tenantOptionPane.setSelectedTenant(t);
		} else {
		    tenantOptionPane.clear();
		}
	    }
	});

	meter.addListSelectionListener(new ListSelectionListener() {

	    @Override
	    public void valueChanged(ListSelectionEvent arg0) {
		Meter m = (Meter) meter.getSelectedValue();
		if (m != null) {
		    getMeterInfo().loadInfo(m);
		} else {
		    getMeterInfo().clear();
		}
	    }
	});

    }

    public void setMoveOut(Date d) {
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	Tenant t = (Tenant) tenant.getSelectedValue();
	if (t == null) {
	    ErrorHandle.popUp("Bitte Mieter auswählen!");
	    return;
	}
	// sicherheits nachfrage
	if (ErrorHandle.askYesNo("Auszug für " + t + " am " + sdf.format(d) + " festlegen?")) {
	    db.input.TenantInput.setMoveout(t, d);
	    tenant.refresh();
	}
    }

    public void setRent(Double value, Date d) {
	Tenant t = (Tenant) tenant.getSelectedValue();
	if (t == null) {
	    ErrorHandle.popUp("Bitte Mieter auswählen!");
	    return;
	}
	TenantInput.setRent(value, d, t);
    }

    public void setPersonCount(int x, Date d) {
	Tenant t = (Tenant) tenant.getSelectedValue();
	if (t != null)
	    TenantInput.setPersonCount(t, d, x);
	else
	    ErrorHandle.popUp("bitte Mieter wählen");
    }

    public void setPayment(double value, Date d) {
	Tenant t = (Tenant) tenant.getSelectedValue();
	if (t != null) {
	    TenantInput.setPayment(value, t, d);
	} else
	    ErrorHandle.popUp("bitte Mieter wählen");
    }

    public void addTenant(String name, Date moveIn) {
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	LoadAble la = flat.getSelectedValue();
	Flat f;
	if (la instanceof Flat)
	    f = (Flat) flat.getSelectedValue();
	else {
	    ErrorHandle.popUp("Bitte Wohnung Auswählen");
	    return;
	}
	if (ErrorHandle.askYesNo("Mieter:\t" + name + "\nEinzug:\t" + sdf.format(moveIn) + "\nWohnung:\t" + f
		+ "\nSind diese Daten korrekt?")) {
	    db.input.TenantInput.addTenant(name, f, moveIn);
	    tenant.refresh();
	}
    }

    public void refreshAll() {
	tenant.refresh();
	flat.refresh();
	house.refresh();
	refreshMeter();
    }

    public void refreshMeter() {
	meter.refresh();
    }

    public void refreshTenant() {
	tenant.refresh();
    }

    public MeterInfo getMeterInfo() {
	return getMeterOptions().getMeterInfo();

    }

    public Flat getFlat() {
	return (Flat) flat.getSelectedValue();
    }

    public House getHouse() {
	return (House) house.getSelectedValue();
    }

    public Meter getMeter() {
	return (Meter) meter.getSelectedValue();
    }

    public void setHeatCost(Double value, Date d) {
	TenantInput.setHeatCost(getTenant(), value, d);
    }

    public Tenant getTenant() {
	return (Tenant) tenant.getSelectedValue();
    }

    public int getTenantIndex() {
	return tenant.getSelectedIndex();
    }

    public void setTenantIndex(int index) {
	tenant.setSelectedIndex(index);
    }

    public MeterOption getMeterOptions() {
	return meterOptions;
    }

    public void setMeterOptions(MeterOption meterOptions) {
	this.meterOptions = meterOptions;
    }

}
