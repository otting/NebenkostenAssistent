package gui.panes;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import gui.ErrorHandle;
import gui.MainFrame;
import net.miginfocom.swing.MigLayout;

public class ScaleOptionPane extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 3405540261294053798L;
    public static final String CONFIG_PATH = "font.cfg";
    public static float size;
    private JLabel lblTest;

    /**
     * Create the panel.
     */
    public ScaleOptionPane() {
	setLayout(new MigLayout("", "[grow]", "[][][][]"));

	lblTest = new JLabel("Das ist ein Testlabel");
	lblTest.setFont(new Font("Tahoma", Font.PLAIN, 12));
	add(lblTest, "cell 0 1,alignx left");

	JSpinner spinner = new JSpinner();
	spinner.setModel(new SpinnerNumberModel((int) size, 10, 100, 1));
	spinner.setFont(new Font("Tahoma", Font.PLAIN, 12));
	add(spinner, "cell 0 0,alignx left");
	spinner.addChangeListener(new ChangeListener() {

	    @Override
	    public void stateChanged(ChangeEvent e) {
		setSize((int) spinner.getValue());
	    }
	});
	;

	JButton btnSpeichern = new JButton("Speichern");
	btnSpeichern.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		saveSize((int) spinner.getValue());
	    }
	});
	add(btnSpeichern, "cell 0 3");

    }

    private void setSize(int s) {
	lblTest.setFont(lblTest.getFont().deriveFont((float) s));
	repaint();
    }

    private void saveSize(int s) {
	File f = new File(CONFIG_PATH);
	f.delete();
	try {
	    f.createNewFile();
	    PrintWriter pw = new PrintWriter(f);
	    pw.println(s);
	    pw.close();
	    Runtime.getRuntime().exec("attrib +H " + CONFIG_PATH);
	    setDefaultSize(s);
	    MainFrame.main(null);
	    ((JDialog) getParent().getParent().getParent()).getOwner().dispose();

	} catch (IOException e) {
	    ErrorHandle.popUp("Einstellung konnte nicht gespeichert werden");
	    e.printStackTrace();
	}

    }

    public static void setDefaultSize(float size) {
	ScaleOptionPane.size = size;
	System.out.println("Setting size to " + size);
	Set<Object> keySet = UIManager.getLookAndFeelDefaults().keySet();
	Object[] keys = keySet.toArray(new Object[keySet.size()]);

	for (Object key : keys) {

	    if (key != null && key.toString().toLowerCase().contains("font")) {
		Font font = UIManager.getDefaults().getFont(key);
		if (font != null) {
		    font = font.deriveFont((float) size);
		    UIManager.put(key, font);
		}

	    }

	}

    }

}
