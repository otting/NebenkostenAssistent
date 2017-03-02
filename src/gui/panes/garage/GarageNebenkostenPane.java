package gui.panes.garage;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import db.util.GarageNebenkosten;
import gui.ErrorHandle;
import net.miginfocom.swing.MigLayout;

public class GarageNebenkostenPane extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 123123551251568561L;
    private JFormattedTextField grundSteuer;
    private JFormattedTextField oflWasser;
    private JLabel lblGrundsteuer;
    private JLabel lblOflWassergeb;
    private JButton btnSpeichern;
    private JLabel lblJahr;
    private JLabel lblZustzlicheKosten;
    private JList<GarageNebenkosten> zusatzKostenListe;
    private JPanel panel;
    private JTextField zusatzKostenName;
    private JButton btnZusatzKosten;
    private JScrollPane scrollPane;
    private JLabel lblName;
    private JLabel lblWert;
    private JFormattedTextField zusatzKostenWert;
    private JButton btnLoeschen;
    private JSpinner jahrSpinner;
    private GarageNebenkosten grund;
    private GarageNebenkosten oflW;

    private void reload() {
	int year = getYear();
	grund = null;
	oflW = null;
	grundSteuer.setText("0,0");
	oflWasser.setText("0,0");
	NumberFormat format = NumberFormat.getNumberInstance(Locale.GERMAN);
	DefaultListModel<GarageNebenkosten> model = new DefaultListModel<>();
	for (GarageNebenkosten gn : GarageNebenkosten.loadFor(year)) {
	    if (gn.getType().equals(GarageNebenkosten.GRUNDS_TYPE)) {
		grundSteuer.setText("");
		grundSteuer.setText(format.format(gn.getValue()));
		grund = gn;
	    } else if (gn.getType().equals(GarageNebenkosten.OFLW_TYPE)) {
		oflWasser.setText("");
		oflWasser.setText(format.format(gn.getValue()));
		oflW = gn;
	    } else {
		model.addElement(gn);
	    }
	}
	zusatzKostenListe.setModel(model);
    }

    private int getYear() {
	return Integer.parseInt(jahrSpinner.getValue().toString());
    }

    private double parseDouble(String str) {

	try {
	    return NumberFormat.getNumberInstance(Locale.GERMAN).parse(str).doubleValue();
	} catch (ParseException e) {
	    ErrorHandle.popUp("Ungültiges Nummer Format");
	    throw new NumberFormatException();
	}

    }

    private double getGrundS() {
	return parseDouble(grundSteuer.getText());
    }

    private double getOflWasser() {
	return parseDouble(oflWasser.getText());
    }

    private double getZusatzKosten() {
	return parseDouble(zusatzKostenWert.getText());
    }

    public GarageNebenkostenPane() {
	setLayout(new MigLayout("", "[84px][154px][grow]",
		"[20px][][20px][grow][20px,grow][][][23px][][][][20px][23px][23px]"));
	lblZustzlicheKosten = new JLabel("Zus\u00E4tzliche Kosten");
	add(lblZustzlicheKosten, "cell 2 0");

	lblJahr = new JLabel("Jahr");
	add(lblJahr, "cell 0 2,alignx right");

	jahrSpinner = new JSpinner();

	jahrSpinner.setModel(
		new SpinnerNumberModel(Calendar.getInstance().get(Calendar.YEAR), null, null, new Integer(1)));
	JSpinner.NumberEditor edit = new JSpinner.NumberEditor(jahrSpinner, "#");
	jahrSpinner.setEditor(edit);
	jahrSpinner.addChangeListener(new ChangeListener() {

	    @Override
	    public void stateChanged(ChangeEvent e) {
		reload();
	    }
	});
	add(jahrSpinner, "cell 1 2,alignx left,aligny center");

	panel = new JPanel();
	add(panel, "cell 2 2 1 2,alignx left,growy");
	panel.setLayout(new GridLayout(0, 2, 0, 0));

	lblName = new JLabel("Name");
	panel.add(lblName);

	zusatzKostenName = new JTextField();
	panel.add(zusatzKostenName);
	zusatzKostenName.setColumns(10);

	lblWert = new JLabel("Wert");
	panel.add(lblWert);

	zusatzKostenWert = new JFormattedTextField(NumberFormat.getNumberInstance(Locale.GERMAN));
	panel.add(zusatzKostenWert);
	zusatzKostenWert.setColumns(10);

	btnZusatzKosten = new JButton("Hinzuf\u00FCgen");
	panel.add(btnZusatzKosten);
	btnZusatzKosten.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (!zusatzKostenName.getText().isEmpty()) {
		    String name = zusatzKostenName.getText();
		    if (name.equals(GarageNebenkosten.GRUNDS_TYPE) || (name.equals(GarageNebenkosten.OFLW_TYPE))) {
			ErrorHandle.popUp("Ungültige Bezeichnung");
		    }
		    GarageNebenkosten.add(name, getZusatzKosten(), getYear());
		    reload();
		} else {
		    ErrorHandle.popUp("Bitte Bezeichnung angeben!");
		}
	    }
	});

	btnLoeschen = new JButton("L\u00F6schen");
	panel.add(btnLoeschen);
	btnLoeschen.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		zusatzKostenListe.getSelectedValue().delete();
		reload();
	    }
	});

	lblOflWassergeb = new JLabel("Ofl. Wassergeb.");
	lblOflWassergeb.setToolTipText("Oberfl\u00E4chenwasser Geb\u00FChr");
	add(lblOflWassergeb, "cell 0 3,alignx right,aligny bottom");

	oflWasser = new JFormattedTextField(NumberFormat.getNumberInstance(Locale.GERMANY));
	oflWasser.setText("0,0");
	add(oflWasser, "cell 1 3,alignx left,aligny bottom");
	oflWasser.setColumns(10);

	lblGrundsteuer = new JLabel("Grundsteuer");
	add(lblGrundsteuer, "cell 0 4,alignx right,aligny top");

	grundSteuer = new JFormattedTextField(NumberFormat.getNumberInstance(Locale.GERMANY));
	grundSteuer.setText("0,0");
	add(grundSteuer, "cell 1 4,alignx left,aligny top");
	grundSteuer.setColumns(10);

	scrollPane = new JScrollPane();
	add(scrollPane, "cell 2 4 1 4,grow");

	zusatzKostenListe = new JList<GarageNebenkosten>(new DefaultListModel<GarageNebenkosten>());
	scrollPane.setViewportView(zusatzKostenListe);

	btnSpeichern = new JButton("Speichern");
	btnSpeichern.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (grund != null) {
		    grund.setValue(getGrundS());
		    grund.save();
		} else {
		    GarageNebenkosten.add(GarageNebenkosten.GRUNDS_TYPE, getGrundS(), getYear());
		}

		if (oflW != null) {
		    oflW.setValue(getOflWasser());
		    oflW.save();
		} else {
		    GarageNebenkosten.add(GarageNebenkosten.OFLW_TYPE, getOflWasser(), getYear());
		}

		reload();
	    }
	});
	add(btnSpeichern, "cell 2 9,growx,aligny top");
	reload();
    }

}
