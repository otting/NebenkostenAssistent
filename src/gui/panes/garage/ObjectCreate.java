package gui.panes.garage;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import db.Garage;
import db.StellP;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ObjectCreate extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 5003204931363728635L;
    private final JPanel contentPanel = new JPanel();
    private JTextField txtGarageName;
    private static JDialog open;
    private static ObjectManageDummy om;

    public static void open(ObjectManageDummy objectManager) {
	om = objectManager;

	try {
	    ObjectCreate dialog = new ObjectCreate();
	    open = dialog;
	    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    dialog.setLocationRelativeTo(om);
	    dialog.pack();
	    dialog.setModal(true);
	    dialog.setVisible(true);
	    dialog.setLocationRelativeTo(null);

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private static void close() {
	open.dispose();
	open = null;
    }

    /**
     * Create the dialog.
     */
    public ObjectCreate() {
	setBounds(100, 100, 450, 300);
	getContentPane().setLayout(new BorderLayout());
	contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	getContentPane().add(contentPanel, BorderLayout.CENTER);
	contentPanel.setLayout(new MigLayout("", "[grow]", "[][]"));
	{
	    JLabel lblName = new JLabel("Name");
	    contentPanel.add(lblName, "cell 0 0");
	}
	{
	    txtGarageName = new JTextField();
	    contentPanel.add(txtGarageName, "cell 0 1,alignx left");
	    txtGarageName.setColumns(10);
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
			if (om instanceof GarageManage)
			    Garage.createGarage(txtGarageName.getText());
			else
			    StellP.createStellP(txtGarageName.getText());
			om.loadObjectList();
			close();
		    }
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
	    }
	    {
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(new ActionListener() {

		    @Override
		    public void actionPerformed(ActionEvent e) {
			close();
		    }
		});
		buttonPane.add(cancelButton);
	    }
	}
    }

}
