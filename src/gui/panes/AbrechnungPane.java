package gui.panes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import db.House;
import gui.ErrorHandle;
import net.miginfocom.swing.MigLayout;
import util.Calculation;
import util.CustomJList;

public class AbrechnungPane extends JPanel implements ActionListener {
    /**
     * 
     */
    private static final long serialVersionUID = 12356L;
    private CustomJList houseList;
    private JTextField textField;

    public AbrechnungPane() {
	setBorder(new EmptyBorder(5, 5, 5, 5));
	setLayout(new MigLayout("", "[300px,grow][100px,grow]", "[290px]"));

	houseList = new CustomJList(db.Type.House);
	houseList.setToolTipText("H\u00E4user");
	houseList.setBorder(new CompoundBorder());
	add(houseList, "cell 1 0,grow");

	JPanel panel = new JPanel();
	add(panel, "cell 0 0,grow");

	JButton btnAbrechnungErstellen = new JButton("Abrechnung Erstellen");
	btnAbrechnungErstellen.addActionListener(this);
	panel.setLayout(new MigLayout("", "[141px][10px][135px]", "[20px][31px]"));
	panel.add(btnAbrechnungErstellen, "cell 0 1 3 1,alignx right,growy");

	JLabel lblAbrechnungsjahr = new JLabel("Abrechnungsjahr:");
	panel.add(lblAbrechnungsjahr, "cell 0 0,growx,aligny center");

	textField = new JTextField();
	textField.setText("" + (Calendar.getInstance().get(Calendar.YEAR) - 1));
	panel.add(textField, "cell 2 0,alignx left,aligny top");
	textField.setColumns(10);
	setVisible(true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent e) {
	switch (e.getActionCommand()) {
	case "Abrechnung Erstellen":
	    int year = 0;
	    try {
		year = Integer.parseInt(textField.getText());
	    } catch (Exception exc) {
		ErrorHandle.popUp("Bitte Jahr eingeben!");
		return;
	    }
	    // abrechnung erstellen
	    Calculation.calculate(year,
		    new LinkedList<House>((Collection<? extends House>) houseList.getSelectedValuesList()));
	    break;
	default:
	    System.err.println(e.getActionCommand());
	}
    }
}