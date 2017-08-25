package gui.panes.manage;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.NoSuchElementException;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import db.Tenant;
import db.input.TenantInput;
import db.util.PersonCount;
import gui.ErrorHandle;
import net.miginfocom.swing.MigLayout;

public class TenantOptions extends JPanel implements ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = -1654691868826951469L;

    /**
     * Create the panel.
     */
    ManagePane parent;
    private JFormattedTextField txtName;
    private JSpinner auszugDatum, moveIn;
    private JFormattedTextField rent;
    private JSpinner rentDate;
    private JLabel lblPersonenzahl;
    private JFormattedTextField persoZahl;
    private JLabel lblEinzahlung;
    private JFormattedTextField einzahlung;
    private JSpinner persoDatum;
    private JSpinner einzahlDatum;
    private JLabel lblHeitzkosten;
    private JFormattedTextField heitzKosten;
    private JSpinner heitzDatum;
    private JLabel lblAuszugDatum;
    private JLabel lblNichtFestgelegt;
    private JLabel lblKaltmietemonatlich;
    private JLabel lblName;

    public TenantOptions(ManagePane mp) {
	parent = mp;
	Calendar cal = Calendar.getInstance();
	setLayout(new MigLayout("", "[121px][139px][95px]", "[20px][20px][20px][20px][20px][20px][20px][23px][23px]"));

	lblName = new JLabel("Name:");
	add(lblName, "cell 0 0,alignx right,aligny center");

	txtName = new JFormattedTextField();
	txtName.setValue("Name");
	add(txtName, "cell 1 0,alignx left,aligny center");
	txtName.setColumns(15);

	JLabel lblEinzugDatum = new JLabel("Einzug Datum:");
	add(lblEinzugDatum, "cell 0 1,alignx right,aligny center");

	moveIn = new JSpinner(new SpinnerDateModel(new Date(cal.getTimeInMillis()), null, null, Calendar.MONTH));
	moveIn.setEditor(new JSpinner.DateEditor(moveIn, "dd.MM.yyyy"));
	add(moveIn, "cell 1 1,alignx left,aligny center");

	lblAuszugDatum = new JLabel("Auszug Datum:");
	add(lblAuszugDatum, "cell 0 2,alignx right,aligny center");
	auszugDatum = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.MONTH));
	auszugDatum.setEditor(new JSpinner.DateEditor(auszugDatum, "dd.MM.yyyy"));
	add(auszugDatum, "cell 1 2,alignx left,aligny center");

	lblNichtFestgelegt = new JLabel("NICHT FESTGELEGT");
	add(lblNichtFestgelegt, "cell 2 2,alignx center,aligny center");
	lblNichtFestgelegt.setVisible(false);

	lblKaltmietemonatlich = new JLabel("Kaltmiete (Monatlich):");
	add(lblKaltmietemonatlich, "cell 0 3,alignx right,aligny center");

	rent = new JFormattedTextField(NumberFormat.getNumberInstance());
	rent.setColumns(15);
	add(rent, "cell 1 3,alignx left,aligny center");
	rent.setValue(0.00f);

	rentDate = new JSpinner(new SpinnerDateModel(new Date(cal.getTimeInMillis()), null, null, Calendar.MONTH));
	add(rentDate, "cell 2 3,alignx left,aligny center");
	rentDate.setEditor(new JSpinner.DateEditor(rentDate, "yyyy"));
	rentDate.addChangeListener(new ChangeListener() {
	    @Override
	    public void stateChanged(ChangeEvent e) {
		Date d = (Date) rentDate.getValue();
		Calendar cal = new GregorianCalendar();
		cal.setTime(d);
		Tenant t = parent.getTenant();
		if (t != null && d != null)
		    rent.setValue(t.getRent(cal.get(Calendar.YEAR)));
	    }
	});

	lblEinzahlung = new JLabel("Einzahlung(Summe Jahr):");
	add(lblEinzahlung, "cell 0 4,alignx right,aligny center");

	einzahlung = new JFormattedTextField(NumberFormat.getNumberInstance());
	einzahlung.setText("0");
	einzahlung.setColumns(15);
	add(einzahlung, "cell 1 4,alignx left,aligny center");

	einzahlDatum = new JSpinner();
	einzahlDatum.setModel(new SpinnerDateModel(cal.getTime(), null, null, Calendar.MONTH));
	einzahlDatum.setEditor(new JSpinner.DateEditor(einzahlDatum, "yyyy"));
	add(einzahlDatum, "cell 2 4,alignx left,aligny center");
	einzahlDatum.addChangeListener(new ChangeListener() {
	    @Override
	    public void stateChanged(ChangeEvent e) {
		Date d = (Date) einzahlDatum.getValue();
		Tenant t = parent.getTenant();
		if (t != null && d != null)
		    einzahlung.setValue(t.getBalance(d));
	    }
	});

	lblPersonenzahl = new JLabel("PersonenZahl:");
	add(lblPersonenzahl, "cell 0 5,alignx right,aligny center");

	persoZahl = new JFormattedTextField(NumberFormat.getNumberInstance());
	persoZahl.setText("0");
	persoZahl.setColumns(15);
	add(persoZahl, "cell 1 5,alignx left,aligny center");

	persoDatum = new JSpinner(new SpinnerDateModel(new Date(1460671200000L), null, null, Calendar.MONTH));
	persoDatum.setEditor(new JSpinner.DateEditor(persoDatum, "dd.MM.yyyy"));
	add(persoDatum, "cell 2 5,alignx left,aligny center");

	lblHeitzkosten = new JLabel("Heitzkosten:");
	add(lblHeitzkosten, "cell 0 6,alignx right,aligny center");

	heitzKosten = new JFormattedTextField(NumberFormat.getNumberInstance());
	heitzKosten.setText("0");
	heitzKosten.setColumns(15);
	add(heitzKosten, "cell 1 6,alignx left,aligny center");

	heitzDatum = new JSpinner(new SpinnerDateModel(cal.getTime(), null, null, Calendar.MONTH));
	heitzDatum.setEditor(new JSpinner.DateEditor(heitzDatum, "yyyy"));
	add(heitzDatum, "cell 2 6,alignx left,aligny center");
	heitzDatum.addChangeListener(new ChangeListener() {

	    @Override
	    public void stateChanged(ChangeEvent e) {
		if (selected != null) {
		    Calendar cal = Calendar.getInstance();
		    cal.setTime((Date) heitzDatum.getValue());
		    heitzKosten.setValue(selected.getHeaterCost(cal.get(Calendar.YEAR)));
		}
	    }
	});

	JButton btnNeuerMieter = new JButton("Neuen Mieter Anlegen");
	btnNeuerMieter.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (parent.getFlat() != null)
		    NewTenant.open(parent.getFlat(), parent);
		else {
		    ErrorHandle.popUp("Bitte Wohnung auswählen");
		}
	    }
	});
	btnNeuerMieter.addActionListener(this);

	btnSpeichern = new JButton("Speichern");
	btnSpeichern.addActionListener(this);
	add(btnSpeichern, "cell 1 7,alignx left,aligny center");
	add(btnNeuerMieter, "cell 1 8,alignx left,aligny center");
	clear();

    }

    private HashMap<String, String> oldValues;
    private Tenant selected = null;

    public void setSelectedTenant(Tenant t) {
	if (t == null) {
	    return;
	}
	selected = t;
	initValues(t);
    }

    /**
     * Loads information of a tenant into the GUI
     * 
     * @param t
     */
    private void initValues(Tenant t) {
	if (t == null) {
	    clear();
	    return;
	}
	Calendar cal = Calendar.getInstance();

	moveIn.setValue(t.getMoveIn());
	Date d = t.getMoveOut();
	if (d.getTime() != Long.MAX_VALUE)
	    auszugDatum.setValue(d);
	else {
	    auszugDatum.setValue(cal.getTime());
	}
	lblNichtFestgelegt.setVisible((d.getTime() == Long.MAX_VALUE));

	int year = cal.get(Calendar.YEAR);

	txtName.setValue(t.getName());
	rent.setValue(t.getRent(year));
	cal.set(t.getLastRentYear(), 1, 1);
	rentDate.setValue(cal.getTime());
	cal = Calendar.getInstance();
	PersonCount pc = t.getPersonCount();
	try {
	    persoZahl.setValue(pc.getLast());
	    persoDatum.setValue(pc.getLastDate());
	} catch (NoSuchElementException e) {
	    persoZahl.setValue(0);
	    System.err.println("Person count 0");
	    persoDatum.setValue(new Date(0));
	}

	einzahlung.setValue(t.getBalance(cal.getTime()));
	cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
	einzahlDatum.setValue(cal.getTime());
	heitzDatum.setValue(cal.getTime());
	heitzKosten.setValue(t.getHeaterCost(cal.get(Calendar.YEAR)));

	saveOldValues();
    }

    /**
     * used to wipe information from this window
     */
    public void clear() {
	txtName.setValue("");
	rent.setValue(0);
	persoZahl.setValue(0);
	einzahlung.setValue(0);
    }

    private void saveValues() {
	double value;

	if (selected == null || parent.getTenant() == null) {
	    ErrorHandle.popUp("Bitte Mieter auswählen!");
	    return;
	}

	if (changed(txtName)) {
	    TenantInput.rename(selected, txtName.getText());
	}
	if (changed(moveIn)) {
	    TenantInput.setMoveIn(selected, (Date) moveIn.getValue());
	}

	if (changed(auszugDatum)) {
	    TenantInput.setMoveout(selected, (Date) auszugDatum.getValue());
	}
	if (changed(rent) || changed(rentDate)) {
	    value = Double.parseDouble(rent.getText().replace(".", "").replace(",", "."));
	    parent.setRent(value, (Date) rentDate.getValue());
	}
	if (changed(einzahlung) || changed(einzahlDatum)) {
	    value = Double.parseDouble(einzahlung.getText().replace(".", "").replace(",", "."));
	    System.out.println("saved einzahlung " + einzahlDatum.getValue());
	    parent.setPayment(value, (Date) einzahlDatum.getValue());
	}

	if (changed(persoZahl) || changed(persoDatum)) {
	    int x = Integer.parseInt(persoZahl.getText().replace(".", "").replace(",", "."));
	    parent.setPersonCount(x, (Date) persoDatum.getValue());
	}

	if (changed(heitzKosten) || changed(heitzDatum)) {
	    value = Double.parseDouble(heitzKosten.getText().replace(".", "").replace(",", "."));
	    parent.setHeatCost(value, (Date) heitzDatum.getValue());
	}
	int index = parent.getTenantIndex();
	parent.refreshTenant();
	parent.setTenantIndex(index);

    }

    private boolean changed(Component c) {
	if (oldValues == null)
	    return false;
	String name = c.getName();
	String old = oldValues.get(name);
	String value = getValue(c);
	return !old.equals(value);
    }

    /**
     * it is crucial that this method is called AFTER loading values for a tenant.
     * The values will later on be used to decide rather a value has to be saved or
     * not
     */
    private void saveOldValues() {
	oldValues = new HashMap<String, String>();
	for (Component c : getParent().getComponents()) {
	    saveComponent(c);
	}
	put(txtName);

    }

    private void saveComponent(Component comp) {
	if (comp instanceof Container) {
	    for (Component c : ((Container) comp).getComponents()) {
		saveComponent(c);
	    }
	}
	put(comp);

    }

    private String getValue(Component c) {
	String value = null;
	if (c instanceof JSpinner)
	    value = getValue((JSpinner) c);
	else if (c instanceof JFormattedTextField)
	    value = getValue((JFormattedTextField) c);

	return value;
    }

    private String getValue(JFormattedTextField x) {
	if (x.getValue() == null) {
	    return null;
	}
	return x.getValue().toString();
    }

    private String getValue(JSpinner x) {
	if (x.getValue() == null) {
	    return null;
	}
	return x.getValue().toString();
    }

    private static int nonameIndex = 0;
    private JButton btnSpeichern;

    /**
     * puts the name of the component inside oldValues with the linked value as
     * String
     * 
     * @param c
     */
    private void put(Component c) {
	if (c == null)
	    return;
	String value = getValue(c);
	if (value == null)
	    return;
	String name = c.getName();

	if (name == null || name == "") {
	    name = "" + nonameIndex;
	    nonameIndex++;
	    c.setName(name);
	}
	while (oldValues.containsKey(name)) {
	    name += nonameIndex++;
	}

	if (value != null)
	    oldValues.put(name, value);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	switch (e.getActionCommand()) {
	case "Speichern":
	    saveValues();
	    break;
	default:
	    System.err.println("Unknowed Action: " + e.getActionCommand());
	}

    }
}
