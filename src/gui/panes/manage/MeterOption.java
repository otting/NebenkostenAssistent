package gui.panes.manage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import db.DbHandle;
import db.DbNames;
import db.Flat;
import db.Meter;
import db.input.MeterInput;
import gui.ErrorHandle;
import net.miginfocom.swing.MigLayout;

public class MeterOption extends JPanel implements ActionListener, DbNames {

    /**
     * 
     */
    private static final long serialVersionUID = 3958144387869261863L;
    /**
     * Create the panel.
     */
    private ManagePane parent;
    private JFormattedTextField meterData;
    private JTextField serialNumber;
    private JCheckBox isMain;
    private JSpinner dateSpinner;
    private String newDataString, newMeterString;
    private JSpinner kindSpinner;
    private HashMap<String, Integer> meterKinds;

    public MeterOption(ManagePane mp) {
	setBorder(null);
	setLayout(new MigLayout("", "[grow][grow][grow]", "[23px][23px][20px]"));

	JButton btnNewData = new JButton("neuen Stand eintragen");
	add(btnNewData, "cell 0 0,alignx center,aligny center");
	btnNewData.addActionListener(this);
	newDataString = btnNewData.getActionCommand();

	meterData = new JFormattedTextField(NumberFormat.getNumberInstance());
	meterData.setColumns(20);
	meterData.setToolTipText("Z\u00E4hlerstand");
	add(meterData, "cell 1 0,growx,aligny center");

	Calendar cal = Calendar.getInstance();
	cal.set(Calendar.MONTH, 11);
	cal.set(Calendar.DATE, 31);
	dateSpinner = new JSpinner(new SpinnerDateModel(new Date(cal.getTimeInMillis()), null, null, Calendar.MONTH));
	dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd.MM.yyyy"));
	add(dateSpinner, "cell 2 0,growx,aligny center");

	JButton btnNewMeter = new JButton("Neuen Z\u00E4hler Anlegen");
	btnNewMeter.addActionListener(this);
	newMeterString = btnNewMeter.getActionCommand();
	add(btnNewMeter, "cell 0 1,growx,aligny center");

	serialNumber = new JTextField();
	serialNumber.setToolTipText("Seriennummer");
	add(serialNumber, "cell 1 1,growx,aligny center");
	serialNumber.setColumns(10);

	isMain = new JCheckBox("Hauptz\u00E4hler");
	add(isMain, "cell 2 1,alignx center,aligny center");

	kindSpinner = new JSpinner();
	Table t = DbHandle.getTable(METER_DESCRIPTION_TABLE);
	meterKinds = new HashMap<>();
	meterKinds.put("-", -1);
	for (Row r : t) {
	    meterKinds.put(r.getString(METER_DESCRIPTION_NAME), r.getInt(METER_DESCRIPTION_ID));
	}
	kindSpinner.setModel(new SpinnerListModel(meterKinds.keySet().toArray()));
	kindSpinner.getModel().setValue("-");
	add(kindSpinner, "cell 1 2,growx,aligny center");
	parent = mp;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
	System.out.println(e.getActionCommand());
	if (e.getActionCommand().equals(newDataString)) {
	    newData();
	} else if (e.getActionCommand().equals(newMeterString)) {
	    newMeter();
	}
    }

    private void newMeter() {
	boolean main = isMain.isSelected();
	Flat f;
	int kind = meterKinds.get(kindSpinner.getValue());
	if (kind == -1) {
	    ErrorHandle.popUp("Bitte Zähler Typ auswählen!");
	    return;
	}
	if (main) {
	    if (parent.getHouse() == null) {
		ErrorHandle.popUp("Bitte Haus auswählen!");
		return;
	    }
	    f = parent.getHouse().getFlat();

	} else {
	    if (parent.getFlat() == null) {
		ErrorHandle.popUp("Bitte Wohnung auswählen!");
		return;
	    }
	    f = parent.getFlat();
	}
	String serial = serialNumber.getText();
	if (serial == null || serial.equals("")) {
	    ErrorHandle.popUp("Bitte Seriennummer angeben!");
	    return;
	}

	MeterInput.addMeter(serialNumber.getText(), f, kind, main);
	kindSpinner.setValue("-");
	parent.refreshMeter();
    }

    private void newData() {
	Meter m;
	Date d;
	double value;

	d = (Date) dateSpinner.getValue();
	try {
	    value = Double.parseDouble(meterData.getText().replace(".", "").replace(",", "."));
	} catch (Exception e) {
	    ErrorHandle.popUp("Ungültige Zahl!");
	    return;
	}
	System.out.println(value);
	m = parent.getMeter();
	if (m == null) {
	    ErrorHandle.popUp("Bitte Zähler auswählen!");
	    return;
	}

	if (MeterInput.addValue(m, d, value)) {
	    parent.getMeterInfo().loadInfo(m);
	}
    }
}
