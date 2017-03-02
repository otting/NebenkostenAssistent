package gui.panes.garage;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EmptyBorder;

import db.Tenant;
import gui.ErrorHandle;
import net.miginfocom.swing.MigLayout;

public class MieterWechsel extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 2906809848411977138L;
    private final JPanel contentPanel = new JPanel();
    JComboBox<Tenant> mieterSelect;
    JSpinner datum;
    private JFormattedTextField txtMiete;
    private JTextField txtMieter;
    private static ObjectManageDummy parent;
    private JRadioButton rMieter;
    private JRadioButton rExternePerson;
    private JRadioButton rLeerstand;

    /**
     * Launch the application.
     */
    public static void open(ObjectManageDummy parent) {
	try {
	    System.out.println("MieterWechsel");
	    MieterWechsel.parent = parent;
	    MieterWechsel dialog = new MieterWechsel();
	    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    dialog.setModal(true);
	    dialog.setLocationRelativeTo(parent);
	    dialog.pack();
	    dialog.setVisible(true);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public boolean neuerMieter() {
	try {
	    if (rMieter.isSelected()) {
		parent.setTenant(getTenant(), getDate(), getMiete());
	    } else if (rLeerstand.isSelected()) {
		parent.setTenant("<Leerstand>", getDate(), 0.0);
	    } else if (rExternePerson.isSelected()) {
		parent.setTenant(getMieterName(), getDate(), getMiete());
	    } else
		return false;
	    parent.refresh();
	    return true;
	} catch (Exception e) {
	    ErrorHandle.popUp("Bitte Eingabe Überprüfen!");
	    e.printStackTrace();
	    return false;
	}
    }

    private Double getMiete() throws ParseException {
	NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);
	Double d = format.parse(txtMiete.getText()).doubleValue();
	if (d == null || d <= 0)
	    throw new RuntimeException("Ungültiger eintrag für Miete");
	return d;
    }

    private Date getDate() {
	Date d = (Date) datum.getValue();
	return d;
    }

    private Tenant getTenant() {
	Tenant t = (Tenant) mieterSelect.getSelectedItem();
	if (t == null) {
	    throw new RuntimeException("Kein Mieter Ausgewählt");
	}
	return t;
    }

    private String getMieterName() throws Exception {
	String name = txtMieter.getText();
	if (name == null || "".equals(name))
	    throw new Exception("Ungültiger Mieter Name");
	return name;
    }

    public void close() {
	this.dispose();
    }

    private void enableAllOptions() {
	txtMiete.setVisible(true);
	txtMieter.setVisible(true);
	mieterSelect.setVisible(true);
    }

    /**
     * Create the dialog.
     */
    public MieterWechsel() {
	setBounds(100, 100, 450, 300);
	getContentPane().setLayout(new MigLayout("", "[434px]", "[228px][33px]"));
	contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	getContentPane().add(contentPanel, "cell 0 0,grow");
	contentPanel.setLayout(new MigLayout("", "[][grow]", "[][][][][]"));
	{
	    JPanel panel = new JPanel();
	    contentPanel.add(panel, "cell 1 0,grow");
	    ButtonGroup buttons = new ButtonGroup();
	    {
		rMieter = new JRadioButton("Mieter");
		rMieter.setSelected(true);
		rMieter.addActionListener(new ActionListener() {

		    @Override
		    public void actionPerformed(ActionEvent e) {
			enableAllOptions();
			txtMieter.setVisible(false);
		    }
		});
		buttons.add(rMieter);
		panel.add(rMieter);
	    }
	    {
		rExternePerson = new JRadioButton("Externe Person");
		rExternePerson.addActionListener(new ActionListener() {

		    @Override
		    public void actionPerformed(ActionEvent e) {
			enableAllOptions();
			mieterSelect.setVisible(false);
		    }
		});
		panel.add(rExternePerson);
		buttons.add(rExternePerson);
	    }
	    {
		rLeerstand = new JRadioButton("Leerstand");
		rLeerstand.addActionListener(new ActionListener() {

		    @Override
		    public void actionPerformed(ActionEvent e) {
			txtMieter.setVisible(false);
			txtMiete.setVisible(false);
			mieterSelect.setVisible(false);
		    }
		});
		buttons.add(rLeerstand);
		panel.add(rLeerstand);
	    }
	}
	{
	    JLabel lblMiete = new JLabel("Miete:");
	    contentPanel.add(lblMiete, "cell 0 1,alignx trailing");
	}
	{
	    txtMiete = new JFormattedTextField(NumberFormat.getNumberInstance());
	    contentPanel.add(txtMiete, "cell 1 1,alignx left");
	    txtMiete.setColumns(10);
	}
	{
	    JLabel lblDatum = new JLabel("Datum:");
	    contentPanel.add(lblDatum, "cell 0 2");
	}
	{
	    datum = new JSpinner();
	    datum.setModel(new SpinnerDateModel());
	    datum.setEditor(new JSpinner.DateEditor(datum, "dd.MM.yyyy"));
	    contentPanel.add(datum, "cell 1 2");
	}
	{
	    JLabel lblMieter = new JLabel("Mieter:");
	    contentPanel.add(lblMieter, "cell 0 3,alignx trailing");
	}
	{
	    mieterSelect = new JComboBox<Tenant>();
	    for (Tenant t : Tenant.loadAll(0)) {
		mieterSelect.addItem(t);
	    }
	    contentPanel.add(mieterSelect, "cell 1 3,alignx left");
	}
	{
	    txtMieter = new JTextField();
	    txtMieter.setVisible(false);
	    contentPanel.add(txtMieter, "cell 1 4,alignx left");
	    txtMieter.setColumns(10);
	}
	{
	    JPanel buttonPane = new JPanel();
	    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
	    getContentPane().add(buttonPane, "cell 0 1,growx,aligny top");
	    {
		JButton okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		okButton.addActionListener(new ActionListener() {

		    @Override
		    public void actionPerformed(ActionEvent e) {
			if (neuerMieter())
			    close();
		    }
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
	    }
	    {
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

		    @Override
		    public void actionPerformed(ActionEvent e) {
			close();
		    }
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	    }
	}
    }

}
