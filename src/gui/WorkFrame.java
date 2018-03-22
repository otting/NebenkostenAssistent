package gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class WorkFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = -7783509968212805832L;
    private final JFrame parent;
    public static int width, height;
    private static final File saveFile = new File("config" + File.separator + "windowResolutions");
    /**
     * used to save the resolution for specific panes
     */
    private String name;

    /**
     * 
     * @param parent
     * @param pane
     *            the content pane
     * @param paneName
     *            required to save the panes resolution, should be unique
     */
    public WorkFrame(JFrame parent, JComponent pane, String paneName) {
	super();

	setBounds(100, 100, WorkFrame.width, height);
	setContentPane(pane);

	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	setLocationRelativeTo(null);
	setResizable(true);

	this.parent = parent;
	name = paneName;
	// must be called after the name is set!
	Dimension dim = loadResolution();
	if (dim != null)
	    setSize(dim);
	addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowClosing(WindowEvent e) {
		saveResolution(getSize());
		WorkFrame.this.parent.setEnabled(true);
		WorkFrame.this.parent.setVisible(true);
	    }
	});
	parent.setEnabled(false);

	parent.setVisible(false);
	setVisible(true);
    }

    public void saveResolution(Dimension d) {

	if (name == null) {
	    ErrorHandle.popUp("Name for this Window is not set");
	}
	try {
	    boolean found = false;
	    StringBuilder text = new StringBuilder();
	    NumberFormat formatter = new DecimalFormat("#0.00");

	    if (saveFile.exists()) {
		BufferedReader read = new BufferedReader(new FileReader(saveFile));
		String line;

		while ((line = read.readLine()) != null) {
		    if (line.contains(name)) {
			found = true;

			line = name + ":" + formatter.format(d.getHeight()) + ":" + formatter.format(d.getWidth());
			System.out.println(line);
		    }
		    text.append(line + System.lineSeparator());
		}

		read.close();

	    } else {
		if (!saveFile.getParentFile().exists()) {
		    saveFile.getParentFile().mkdirs();
		}
	    }
	    if (!saveFile.exists() || !found) {
		text.append(name + ":" + formatter.format(d.getHeight()) + ":" + formatter.format(d.getWidth())
			+ System.lineSeparator());
		System.out.println(text.toString());
	    }

	    FileOutputStream fileOut = new FileOutputStream(saveFile);
	    fileOut.write(text.toString().getBytes());
	    fileOut.close();

	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    /**
     * 
     * @return saved Dimension if available
     */
    public Dimension loadResolution() {
	if (name == null) {
	    ErrorHandle.popUp("Name for this Window is not set");
	}
	Dimension d = null;
	NumberFormat format = NumberFormat.getNumberInstance(Locale.GERMAN);
	format.setMaximumFractionDigits(2);
	if (!saveFile.exists())
	    return d;
	try {
	    BufferedReader read = new BufferedReader(new FileReader(saveFile));
	    String line;
	    while ((line = read.readLine()) != null) {
		if (line.contains(name)) {
		    String[] str = line.split(":", 3);
		    d = new Dimension();
		    d.setSize(format.parse(str[2]).doubleValue(), format.parse(str[1]).doubleValue());
		    read.close();
		    return d;
		}
	    }
	    read.close();
	} catch (IOException | ParseException e) {
	    e.printStackTrace();
	}

	return d;
    }
}
