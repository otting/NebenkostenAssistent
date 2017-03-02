package gui.panes.garage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import db.Tenant;
import gui.ErrorHandle;
import net.miginfocom.swing.MigLayout;

public abstract class ObjectManageDummy extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 152672234L;
    private JTextField txtMieter;
    private JTextField txtLastChange;
    private JComboBox<String> objectList;
    private JTextField txtMiete;

    /**
     * Create the panel.
     */
    public ObjectManageDummy() {
	setLayout(new MigLayout("", "[][grow][][][][][]", "[][][][][][][][][][][]"));

	JLabel lblGarage = new JLabel(getObjectDescription());
	add(lblGarage, "cell 1 0");

	objectList = new JComboBox<String>();
	add(objectList, "cell 1 1,alignx left");

	JLabel lblMieter = new JLabel("Mieter");
	add(lblMieter, "cell 1 3");

	txtMieter = new JTextField();
	txtMieter.setEditable(false);
	add(txtMieter, "cell 1 4,alignx left");
	txtMieter.setColumns(10);

	JLabel lblLetztenderung = new JLabel("Letzte \u00C4nderung");
	add(lblLetztenderung, "cell 1 5");

	txtLastChange = new JTextField();
	txtLastChange.setEditable(false);
	add(txtLastChange, "cell 1 6,alignx left");
	txtLastChange.setColumns(10);

	JLabel lblMonatlicheMiete = new JLabel("Monatliche Miete");
	add(lblMonatlicheMiete, "cell 1 7");

	txtMiete = new JTextField();
	txtMiete.setEditable(false);
	add(txtMiete, "cell 1 8,alignx left");
	txtMiete.setColumns(10);

	JButton btnMieterWechseln = new JButton("Mieter Wechseln");
	btnMieterWechseln.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		mieterWechsel();
	    }
	});
	add(btnMieterWechseln, "cell 1 9");

	JButton btnNeuesObjekt = new JButton(getObjectDescription() + " Hinzufügen");
	btnNeuesObjekt.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		creatObject();
	    }
	});
	add(btnNeuesObjekt, "cell 1 10");

	init();

    }

    boolean refreshing = false;

    /**
     * load values
     */
    private void init() {

	loadObjectList();
	objectList.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (!refreshing) {
		    setMieter(getMieter());
		    setLastChange(getLastChange());
		    setMiete(getMiete());
		}
	    }
	});
	if (objectList.getModel().getSize() > 0) {
	    objectList.setSelectedIndex(0);
	}
    }

    /**
     * load all objects again
     */
    public void refresh() {
	refreshing = true;
	int i = getSelectedIndex();
	loadObjectList();
	refreshing = false;
	objectList.setSelectedIndex(i);
    }

    /**
     * place all objects in List
     */
    public void loadObjectList() {
	objectList.removeAllItems();
	for (String o : loadObjects()) {
	    objectList.addItem(o);
	}
    }

    /**
     * display the tenants name for the selected object
     * 
     * @param str
     */
    public void setMieter(String str) {
	txtMieter.setText(str);
    }

    /**
     * set Date the contract for the selected object was last changed
     * 
     * @param str
     */
    public void setLastChange(String str) {
	txtLastChange.setText(str);
    }

    /**
     * Display rent for the currently selected object
     * 
     * @param value
     */
    public void setMiete(double value) {
	NumberFormat formatter = NumberFormat.getCurrencyInstance();

	txtMiete.setText(formatter.format(value));
    }

    /**
     * get the index of the selected object
     * 
     * @return
     */
    public int getSelectedIndex() {
	return objectList.getSelectedIndex();
    }

    /**
     * opens a dialog to change the tenant for this object
     */
    public void mieterWechsel() {
	if (getSelectedIndex() >= 0)
	    MieterWechsel.open(this);
	else
	    ErrorHandle.popUp("Bitte Garage Auswählen");
    }

    /**
     * set a Tenant for the object
     * 
     * @param t
     * @param d
     * @param rent
     */
    public abstract void setTenant(Tenant t, Date d, Double rent);

    /**
     * set a Tenant for the object
     * 
     * @param tenant
     * @param d
     * @param rent
     */
    public abstract void setTenant(String tenant, Date d, Double rent);

    /**
     * 
     * @return name of the current tenant
     */
    public abstract String getMieter();

    /**
     * 
     * @return Date of the last change to the current contract
     */
    public abstract String getLastChange();

    /**
     * returns rent payed
     * 
     * @return
     */
    public abstract double getMiete();

    /**
     * loads a list of descriptions for objects
     * 
     * @return
     */
    public abstract LinkedList<String> loadObjects();

    /**
     * opens a dialog for creating a new object
     */
    public abstract void creatObject();

    /**
     * 
     * @return Kind of the objects
     */
    public abstract String getObjectDescription();

}
