package gui.panes.manage;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import com.healthmarketscience.jackcess.Row;

import db.DbHandle;
import db.DbNames;
import db.Meter;
import db.input.DbInput;
import net.miginfocom.swing.MigLayout;

public class MeterInfo extends JPanel implements DbNames {

    /**
     * 
     */
    private static final long serialVersionUID = 161348299653514742L;

    /**
     * Create the panel.
     */
    JLabel main;

    private JList<MeterData> meterDataList;
    private Meter meter;

    public MeterInfo() {
	setBorder(new MatteBorder(1, 0, 0, 0, Color.BLACK));
	setLayout(new MigLayout("", "[grow]", "[][][grow]"));

	JPanel panel = new JPanel();
	add(panel, "cell 0 0,alignx left,growy");

	JLabel lblHauptzhler = new JLabel("Hauptz\u00E4hler:");
	panel.add(lblHauptzhler);

	main = new JLabel("Ja/Nein");
	panel.add(main);

	JButton btnEintragLschen = new JButton("Eintrag l\u00F6schen");
	btnEintragLschen.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		MeterData m = meterDataList.getSelectedValue();
		if (m != null) {
		    m.delete();
		}
		loadInfo(meter);
	    }
	});
	add(btnEintragLschen, "cell 0 1");

	meterDataList = new JList<MeterData>(new DefaultListModel<MeterData>());
	add(meterDataList, "cell 0 2,grow");
	clear();

    }

    private class MeterData {
	private Date d;
	private double value;
	private Row row;

	public MeterData(Row r) {
	    d = r.getDate(METER_VALUES_DATE);
	    value = r.getDouble(METER_VALUES_VALUE);
	    row = r;
	}

	public String toString() {
	    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
	    NumberFormat nFormat = new DecimalFormat("#,###");
	    return format.format(d) + ":" + nFormat.format(value);
	}

	public boolean delete() {
	    return DbInput.removeRow(METER_VALUES_TABLE, row);
	}
    }

    public void clear() {
	setVisible(false);
	meterDataList.setModel(new DefaultListModel<>());
    }

    public void loadInfo(Meter m) {
	if (m == null) {
	    System.err.println("Error Loading Meterinfo, Meter is null");
	    return;
	}
	setVisible(true);
	meter = m;
	setMainMeter(m.isMain());
	DefaultListModel<MeterData> newModel = new DefaultListModel<>();
	for (Row r : DbHandle.findAll(METER_VALUES_TABLE, METER_VALUES_METER_ID, m.getId())) {
	    newModel.addElement(new MeterData(r));
	}
	meterDataList.setModel(newModel);
    }

    public void setMainMeter(boolean main) {
	if (main)
	    this.main.setText("Ja");
	else
	    this.main.setText("Nein");

    }

}
