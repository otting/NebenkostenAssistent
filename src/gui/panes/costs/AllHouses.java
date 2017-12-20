package gui.panes.costs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.healthmarketscience.jackcess.Row;

import db.DbHandle;
import db.DbNames;
import db.House;
import db.input.HouseInput;
import gui.ErrorHandle;
import net.miginfocom.swing.MigLayout;
import javax.swing.border.LineBorder;
import java.awt.Color;

public class AllHouses extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 5282972406165983939L;
    JSpinner date, gardenDateSpinner;
    JList<String> gardenCostlist;
    DefaultListModel<String> gardenModel;
    JSpinner garageDatum;
    private JFormattedTextField rainCost;
    JFormattedTextField gardenCost;

    public AllHouses() {
	setLayout(new MigLayout("", "[][][][grow][][]", "[][][][][40px][]"));
	Calendar cal = Calendar.getInstance();
	JLabel lblAbrechnungsjahr = new JLabel("Abrechnungsjahr:");
	add(lblAbrechnungsjahr, "cell 1 0");

	date = new JSpinner();
	add(date, "cell 2 0,growx");
	date.setModel(new SpinnerDateModel(new Date(cal.getTimeInMillis()), null, null, Calendar.YEAR));
	date.setEditor(new JSpinner.DateEditor(date, "yyyy"));
	date.addChangeListener(new ChangeListener() {

	    @Override
	    public void stateChanged(ChangeEvent e) {
		loadValues();
	    }
	});

	gardenModel = new DefaultListModel<String>();
	gardenCostlist = new JList<>(gardenModel);
	gardenCostlist.setBorder(new LineBorder(new Color(0, 0, 0)));
	gardenCostlist.setToolTipText("Gartenarbeit");
	add(gardenCostlist, "cell 3 0 1 5,grow");

	JPanel gardenPanel = new JPanel();
	add(gardenPanel, "cell 4 0 1 5,growx,aligny center");
	gardenPanel.setLayout(new MigLayout("", "[88px]", "[23px][][][][][][]"));

	JLabel lblGartenarbeit = new JLabel("Gartenarbeit:");
	gardenPanel.add(lblGartenarbeit, "cell 0 0,alignx center,aligny center");

	JLabel lblKosten_1 = new JLabel("Kosten:");
	gardenPanel.add(lblKosten_1, "cell 0 1,alignx center");

	gardenCost = new JFormattedTextField(NumberFormat.getNumberInstance());
	gardenPanel.add(gardenCost, "cell 0 2,alignx left,aligny center");
	gardenCost.setColumns(15);

	JLabel lblDatum = new JLabel("Datum:");
	gardenPanel.add(lblDatum, "cell 0 3,alignx center");
	gardenDateSpinner = new JSpinner(
		new SpinnerDateModel(new Date(cal.getTimeInMillis()), null, null, Calendar.MONTH));
	gardenDateSpinner.setEditor(new JSpinner.DateEditor(gardenDateSpinner, "dd.MM.yyyy"));
	gardenPanel.add(gardenDateSpinner, "cell 0 4,alignx center,aligny center");

	JButton addGardenCost = new JButton("Hinzufügen");
	addGardenCost.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		addGardenCost();
	    }
	});
	gardenPanel.add(addGardenCost, "cell 0 5,alignx center,aligny top");

	JButton removeGardenCost = new JButton("Löschen");
	removeGardenCost.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		removeGardenCost();
	    }
	});
	gardenPanel.add(removeGardenCost, "cell 0 5,alignx center,aligny top");

	Calendar start = Calendar.getInstance();
	start.set(Calendar.YEAR, 1991);
	addInterface();
	loadValues();

    }

    private void addInterface() {

	JLabel lblOflWasser = new JLabel("Ofl. Wasser");
	add(lblOflWasser, "cell 1 2,alignx trailing");

	rainCost = new JFormattedTextField(NumberFormat.getNumberInstance());
	add(rainCost, "cell 2 2");
	rainCost.setColumns(15);
	rainCost.setText("0");

	JButton btnSpeichern = new JButton("Speichern");
	add(btnSpeichern, "cell 2 5,growx");
	btnSpeichern.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		Calendar cal = Calendar.getInstance();
		cal.setTime((Date) date.getValue());
		HouseInput.setRainCost(cal.get(Calendar.YEAR), getDouble(rainCost));
	    }
	});

	loadValues();
    }

    private void loadGardencost(int year) {
	gardenModel.clear();
	SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
	NumberFormat moneyForm = NumberFormat.getCurrencyInstance(Locale.GERMANY);
	Calendar cal = Calendar.getInstance();
	for (Row r : DbHandle.getTable(DbNames.GARDEN_TABLE)) {
	    cal.setTime(r.getDate(DbNames.GARDEN_DATE));
	    if (cal.get(Calendar.YEAR) == year) {
		gardenModel.addElement(
			moneyForm.format(r.getDouble(DbNames.GARDEN_VALUE)) + " : " + format.format(cal.getTime()));
	    }
	}

	if (gardenModel.size() == 0)
	    gardenModel.addElement("- keine -");
    }

    private void addGardenCost() {
	Date d = (Date) date.getValue();
	double value = getDouble(gardenCost);
	if (value > 0.0) {
	    HouseInput.setGardenCost(value, d);
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(d);
	    loadGardencost(cal.get(Calendar.YEAR));
	} else {

	}
    }

    private void removeGardenCost() {
	String s = gardenCostlist.getSelectedValue();
	if (s == null)
	    return;
	DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
	try {
	    Date date = format.parse(s.split(":")[1].trim());
	    NumberFormat form = NumberFormat.getCurrencyInstance(Locale.GERMANY);
	    double cost = form.parse(s.split(":")[0]).doubleValue();
	    HouseInput.removeGardenCost(cost, date);
	    Date d = (Date) gardenDateSpinner.getValue();
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(d);
	    loadGardencost(cal.get(Calendar.YEAR));
	} catch (ParseException e) {
	    ErrorHandle.popUp("Konnte Kostenstelle nicht löschen!\n" + e.getMessage());
	    e.printStackTrace();
	}

    }

    private double getDouble(JFormattedTextField txt) {
	if (txt.getValue() instanceof Long) {
	    return ((Long) txt.getValue()).doubleValue();
	} else
	    return (Double) txt.getValue();

    }

    private void loadValues() {
	Calendar cal = Calendar.getInstance();
	cal.setTime((Date) date.getValue());

	int year = cal.get(Calendar.YEAR);

	rainCost.setValue(House.getAllRainCost(year));
	loadGardencost(year);
    }
}
