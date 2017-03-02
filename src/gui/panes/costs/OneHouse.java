package gui.panes.costs;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataListener;

import db.House;
import db.input.HouseInput;
import gui.ErrorHandle;
import gui.MainFrame;
import net.miginfocom.swing.MigLayout;

public class OneHouse extends JPanel implements ChangeListener, ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = -6898241597409166727L;
    private JFormattedTextField generalElectric, trashCost, wasteWaterCost, hotWater, internetCost, waterCost,
	    grundsteuer;
    private JSpinner spinner;
    private JComboBox<House> houseSelect;
    private JList<String> insuranceList;
    private DefaultListModel<String> insuranceModel;
    private JTextField insurance;
    private JFormattedTextField insuranceCost;

    /**
     * Create the panel.
     */

    public OneHouse() {
	setLayout(new MigLayout("", "[1px][9px][grow][grow][grow][grow]",
		"[1px][55px][87px][20px][20px][20px][][23px][]"));

	JLabel lblAbrechnungsjahr = new JLabel("Abrechnungsjahr:");
	add(lblAbrechnungsjahr, "cell 0 2 3 1,alignx right,aligny center");

	spinner = new JSpinner();
	Calendar cal = Calendar.getInstance();
	spinner.setModel(new SpinnerDateModel(new Date(1458046325162L), new Date(-2202635274838L),
		new Date(32510033525162L), Calendar.YEAR));
	spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy"));
	spinner.setValue(cal.getTime());
	spinner.addChangeListener(this);
	add(spinner, "cell 3 2,alignx left,aligny center");

	JLabel lblHaus = new JLabel("Haus:");
	add(lblHaus, "cell 4 2,alignx right,aligny center");

	houseSelect = new JComboBox<House>();
	houseSelect.setModel(getHouseModel());
	houseSelect.addActionListener(this);
	add(houseSelect, "cell 5 2,growx,aligny center");

	JLabel lblInternetProMonat = new JLabel("Kabela.geb.:");
	lblInternetProMonat.setToolTipText("Kabelanschlussgeb\u00FChr des Hauses f\u00FCr ein Jahr");
	add(lblInternetProMonat, "cell 2 3,alignx right,aligny center");

	internetCost = new JFormattedTextField(NumberFormat.getNumberInstance());
	internetCost.setToolTipText("Kabelanschlussgeb\u00FChr des Hauses f\u00FCr ein Jahr");
	internetCost.setColumns(20);
	add(internetCost, "cell 3 3,alignx left,aligny center");

	JLabel lblKanalgebhr = new JLabel("Kanalgebühr:");
	add(lblKanalgebhr, "cell 4 3,alignx right,aligny center");

	wasteWaterCost = new JFormattedTextField(NumberFormat.getNumberInstance());
	wasteWaterCost.setColumns(20);
	add(wasteWaterCost, "cell 5 3,growx,aligny center");

	JLabel lblWasserbezugsgeb = new JLabel("Wasserbezugsgeb.:");
	add(lblWasserbezugsgeb, "cell 0 4 3 1,alignx right,aligny center");

	waterCost = new JFormattedTextField(NumberFormat.getNumberInstance());
	waterCost.setColumns(20);
	add(waterCost, "cell 3 4,alignx left,aligny center");

	JLabel lblWwasserkosten = new JLabel("W.Wasserkosten:");
	lblWwasserkosten.setToolTipText("Warm Wasser");
	add(lblWwasserkosten, "cell 4 4,alignx right,aligny center");

	hotWater = new JFormattedTextField(NumberFormat.getNumberInstance());
	hotWater.setColumns(20);
	add(hotWater, "cell 5 4,growx,aligny center");

	JButton btnSave = new JButton("Speichern");
	btnSave.addActionListener(this);

	JLabel lblAllgemeinstrom = new JLabel("AllgemeinStrom:");
	add(lblAllgemeinstrom, "cell 0 5 3 1,alignx right,aligny center");

	generalElectric = new JFormattedTextField(NumberFormat.getNumberInstance());
	generalElectric.setColumns(20);
	add(generalElectric, "cell 3 5,alignx left,aligny center");

	JLabel lblAbfallkosten = new JLabel("Abfallkosten:");
	add(lblAbfallkosten, "cell 4 5,alignx right,aligny center");

	trashCost = new JFormattedTextField();
	trashCost.setColumns(20);
	add(trashCost, "cell 5 5,growx,aligny center");

	JLabel lblGrundsteuer = new JLabel("Grundsteuer:");
	add(lblGrundsteuer, "cell 2 6,alignx trailing");

	grundsteuer = new JFormattedTextField(NumberFormat.getNumberInstance());
	grundsteuer.setColumns(20);
	add(grundsteuer, "cell 3 6,alignx left");

	add(btnSave, "cell 4 7,growx,aligny top");

	addInsuranceInterface();

    }

    @Override
    public void stateChanged(ChangeEvent e) {
	loadValues();
    }

    private void loadValues() {
	House h = (House) houseSelect.getSelectedItem();
	Calendar cal = Calendar.getInstance();
	cal.setTime((Date) spinner.getValue());
	int year = cal.get(Calendar.YEAR);

	if (h != null) {
	    waterCost.setValue(h.getWaterCost(year));
	    internetCost.setValue(h.getCableCost(year));
	    hotWater.setValue(h.getHotWaterCost(year));
	    trashCost.setValue(h.getTrashCost(year));
	    wasteWaterCost.setValue(h.getWasteWaterCost(year));
	    generalElectric.setValue(h.getCommonElectricCost(year));
	    grundsteuer.setValue(h.getBaseTax(year));
	}
	refreshInsurance();
    }

    private void refreshInsurance() {
	// Insurance
	House h = (House) houseSelect.getSelectedItem();
	Calendar cal = Calendar.getInstance();
	cal.setTime((Date) spinner.getValue());
	int year = cal.get(Calendar.YEAR);
	insuranceModel.clear();
	LinkedList<String> insurance = null;
	if (h != null) {
	    insurance = h.getInsurances(year);
	}
	if (insurance != null)
	    for (String s : insurance) {
		insuranceModel.addElement(s);
	    }
	if (insuranceModel.size() == 0)
	    insuranceModel.addElement("- keine -");
    }

    private void addInsurance() {
	if (houseSelect.getSelectedIndex() == -1) {
	    ErrorHandle.popUp("Bitte Haus wählen!");
	    return;
	}
	String type = insurance.getText();
	if (type == "") {
	    ErrorHandle.popUp("Versicherungsname eintragen!");
	    return;
	}
	if (insuranceCost.getValue() == null) {
	    ErrorHandle.popUp("Versicherungssumme eintragen!");
	    return;
	}
	Calendar cal = Calendar.getInstance();
	cal.setTime((Date) spinner.getValue());
	double value = getDouble(insuranceCost);

	HouseInput.addInsurance(type, (House) houseSelect.getSelectedItem(), cal.get(Calendar.YEAR), value);

	refreshInsurance();
    }

    private void deleteInsurance() {
	String type = null;
	try {
	    type = insuranceList.getSelectedValue().split(":", 2)[0];
	} catch (NullPointerException e) {
	    ErrorHandle.popUp("Bitte Versicherung auswählen!");
	    return;
	}
	Calendar cal = Calendar.getInstance();
	cal.setTime((Date) spinner.getValue());
	HouseInput.deleteInsurance(type, ((House) houseSelect.getSelectedItem()).getId(), cal.get(Calendar.YEAR));
	insuranceModel.remove(insuranceList.getSelectedIndex());
	if (insuranceModel.isEmpty()) {
	    insuranceModel.addElement("- keine -");
	}
    }

    @Override
    public void actionPerformed(ActionEvent e) {

	switch (e.getActionCommand()) {
	case "comboBoxChanged":
	    loadValues();
	    break;
	case "Speichern":
	    save();
	    break;
	case "Löschen":
	    deleteInsurance();
	    break;
	case "Hinzufügen":
	    addInsurance();
	    break;
	default:
	    System.err.println(e.getActionCommand());
	}

    }

    private void addInsuranceInterface() {
	GridBagConstraints gbc_lblVersicherungen = new GridBagConstraints();
	gbc_lblVersicherungen.insets = new Insets(0, 0, 5, 5);
	gbc_lblVersicherungen.gridx = 0;
	gbc_lblVersicherungen.gridy = 6;
	insuranceModel = new DefaultListModel<>();
	GridBagConstraints gbc_list = new GridBagConstraints();
	gbc_list.insets = new Insets(0, 0, 0, 5);
	gbc_list.fill = GridBagConstraints.BOTH;
	gbc_list.gridx = 0;
	gbc_list.gridy = 7;
	GridBagConstraints gbc_panel = new GridBagConstraints();
	gbc_panel.insets = new Insets(0, 0, 0, 5);
	gbc_panel.fill = GridBagConstraints.BOTH;
	gbc_panel.gridx = 1;
	gbc_panel.gridy = 7;

	JPanel insurancePanel = new JPanel();
	add(insurancePanel, "cell 2 8,alignx center,aligny center");
	insurancePanel.setLayout(new MigLayout("", "[88px]", ""));

	JLabel lblVersicherung = new JLabel("Versicherung:");
	insurancePanel.add(lblVersicherung, "cell 0 0,alignx center,aligny center");

	insurance = new JTextField();
	insurance.setColumns(15);
	insurancePanel.add(insurance, "cell 0 1,grow");

	JLabel lblKosten = new JLabel("Kosten:");
	insurancePanel.add(lblKosten, "cell 0 2,alignx center,aligny center");
	insuranceCost = new JFormattedTextField(NumberFormat.getNumberInstance());
	insuranceCost.setColumns(15);
	insurancePanel.add(insuranceCost, "cell 0 3,grow");

	JButton btnAdd = new JButton("Hinzufügen");
	insurancePanel.add(btnAdd, "cell 0 4,grow");
	btnAdd.addActionListener(this);

	JButton btnDelete = new JButton("Löschen");
	insurancePanel.add(btnDelete, "cell 0 5,grow");
	btnDelete.addActionListener(this);

	insuranceList = new JList<>();
	insuranceList.setToolTipText("Versicherung");
	insuranceList.setFixedCellWidth(MainFrame.getFrameWidth() / 3);
	insuranceList.setModel(insuranceModel);
	insuranceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	add(insuranceList, "cell 3 8 2 1,grow");
    }

    private void save() {
	if (houseSelect.getSelectedIndex() == -1) {
	    ErrorHandle.popUp("Kein Haus gewählt");
	    return;
	}
	House h = (House) houseSelect.getSelectedItem();
	Calendar cal = Calendar.getInstance();
	cal.setTime((Date) spinner.getValue());
	int year = cal.get(Calendar.YEAR);

	HouseInput.setCabelCost(year, getDouble(internetCost), h);
	HouseInput.setGeneralElectric(year, getDouble(generalElectric), h);
	HouseInput.setHotWaterCost(year, getDouble(hotWater), h);
	HouseInput.setTrashCost(year, getDouble(trashCost), h);
	HouseInput.setWasteWaterCost(year, getDouble(wasteWaterCost), h);
	HouseInput.setWaterCost(year, getDouble(waterCost), h);
	HouseInput.setBaseTax(year, getDouble(grundsteuer), h);
    }

    private double getDouble(JFormattedTextField txt) {
	if (txt.getValue() instanceof Long) {
	    return ((Long) txt.getValue()).doubleValue();
	} else
	    return (Double) txt.getValue();

    }

    private ComboBoxModel<House> getHouseModel() {
	return new ComboBoxModel<House>() {

	    LinkedList<House> houses = House.loadHouses();
	    House selected = null;

	    @Override
	    public void addListDataListener(ListDataListener arg0) {

	    }

	    @Override
	    public House getElementAt(int arg0) {
		return houses.get(arg0);
	    }

	    @Override
	    public int getSize() {
		return houses.size();
	    }

	    @Override
	    public void removeListDataListener(ListDataListener arg0) {

	    }

	    @Override
	    public Object getSelectedItem() {
		return selected;
	    }

	    @Override
	    public void setSelectedItem(Object anItem) {
		selected = (House) anItem;
	    }
	};
    }

}
