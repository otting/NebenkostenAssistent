package gui.panes.manage;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EmptyBorder;

import db.Flat;
import db.Tenant;
import db.input.TenantInput;
import gui.ErrorHandle;
import net.miginfocom.swing.MigLayout;

public class NewTenant extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1472532602520305320L;
    private final JPanel contentPanel = new JPanel();
    private JTextField textField;
    JSpinner datespinner;
    private Flat flat;
    private ManagePane parent;
    private JFormattedTextField txtMiete;
    private JFormattedTextField txtPerso;

    /**
     * Launch the application.
     * 
     * @param parent
     */
    public static void open(Flat flat, ManagePane parent) {
	try {
	    NewTenant dialog = new NewTenant(flat, parent);
	    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    dialog.setVisible(true);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Create the dialog.
     * 
     * @param myparent
     */
    public NewTenant(Flat f, ManagePane myparent) {
	parent = myparent;
	flat = f;
	setUndecorated(true);
	setModal(true);
	setBounds(100, 100, 450, 300);
	getContentPane().setLayout(new BorderLayout());
	contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	getContentPane().add(contentPanel, BorderLayout.CENTER);
	contentPanel.setLayout(new MigLayout("", "[][grow][][]", "[][][][]"));
	{
	    JLabel lblName = new JLabel("Name:");
	    contentPanel.add(lblName, "cell 0 0,alignx trailing");
	}
	{
	    textField = new JTextField();
	    contentPanel.add(textField, "cell 1 0,alignx left");
	    textField.setColumns(10);
	}
	{
	    JLabel lblDate = new JLabel("Einzug:");
	    contentPanel.add(lblDate, "cell 0 1");
	}
	{
	    datespinner = new JSpinner();
	    datespinner.setModel(new SpinnerDateModel());
	    JSpinner.DateEditor edit = new JSpinner.DateEditor(datespinner, "dd.MM.yyyy");
	    datespinner.setEditor(edit);
	    contentPanel.add(datespinner, "cell 1 1,alignx left");
	}
	{
	    JLabel lblMiete = new JLabel("Miete:");
	    contentPanel.add(lblMiete, "cell 0 2,alignx trailing");
	}
	{
	    txtMiete = new JFormattedTextField(NumberFormat.getNumberInstance(Locale.GERMANY));
	    txtMiete.setText("0");
	    contentPanel.add(txtMiete, "cell 1 2,growx");
	}
	{
	    JLabel lblPersoZahl = new JLabel("Perso Zahl:");
	    contentPanel.add(lblPersoZahl, "cell 0 3,alignx trailing");
	}
	{

	    txtPerso = new JFormattedTextField(NumberFormat.getIntegerInstance());
	    txtPerso.setText("0");
	    contentPanel.add(txtPerso, "cell 1 3,growx");
	}
	{
	    JPanel buttonPane = new JPanel();
	    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
	    getContentPane().add(buttonPane, BorderLayout.SOUTH);
	    {
		JButton okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		okButton.addActionListener(new ActionListener() {

		    @Override
		    public void actionPerformed(ActionEvent e) {
			double rent = 0.0;
			int personcount = 0;
			try {
			    if (textField.getText().isEmpty())
				throw new Exception("Namens Feld falsch");
			    if (datespinner.getValue() == null)
				throw new Exception("Datum ungültig");
			    rent = NumberFormat.getNumberInstance(Locale.GERMAN).parse(txtMiete.getText())
				    .doubleValue();
			    personcount = NumberFormat.getIntegerInstance().parse(txtPerso.getText()).intValue();

			} catch (Exception ex) {
			    ErrorHandle.popUp(ex.getMessage());
			    return;
			}

			TenantInput.addTenant(textField.getText(), flat, (Date) datespinner.getValue());
			Tenant t = Tenant.loadTenants(flat).getLast();
			TenantInput.setRent(rent, (Date) datespinner.getValue(), t);
			TenantInput.setPersonCount(t, (Date) datespinner.getValue(), personcount);
			parent.refreshAll();
			dispose();
		    }
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
	    }
	    {
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {

		    @Override
		    public void actionPerformed(ActionEvent e) {
			dispose();
		    }
		});
	    }
	}
	pack();
	setLocationRelativeTo(myparent);
    }

}
