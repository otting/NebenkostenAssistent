package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.eclipse.wb.swing.FocusTraversalOnArray;

import db.DbHandle;
import gui.panes.AbrechnungPane;
import gui.panes.ScaleOptionPane;
import gui.panes.costs.CostManage;
import gui.panes.garage.GarageHub;
import gui.panes.manage.ManagePane;

public class MainFrame extends JFrame implements ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private static int frameWidth;
    private static int basicDPI = 11;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	int width = (int) screenSize.getWidth();
	int height = (int) screenSize.getHeight();

	adaptDPI(width, height);
	frameWidth = width * 3 / 7;
	WorkFrame.width = frameWidth;
	WorkFrame.height = height * 3 / 7;
	EventQueue.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		try {
		    MainFrame frame = new MainFrame();
		    frame.setVisible(true);
		} catch (Exception e) {
		    ErrorHandle.popUp("Ein unerwarteter Fehler ist aufgetreten:\n" + e.getMessage());
		    e.printStackTrace();
		}
	    }
	});
    }

    public static int getFrameWidth() {
	return frameWidth;
    }

    private JButton btnCalculation;
    private JButton btnManageHouses;
    private JButton btnHouseCost;

    /**
     * Create the frame.
     */
    public MainFrame() {
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(100, 100, 450, 300);
	setLocationRelativeTo(null);

	JMenuBar menuBar = new JMenuBar();
	setJMenuBar(menuBar);

	JMenuItem mntmEinstellungen = new JMenuItem("Einstellungen");
	menuBar.add(mntmEinstellungen);
	mntmEinstellungen.addActionListener(this);

	JMenu mnDbMenu = new JMenu("Database");
	menuBar.add(mnDbMenu);

	JMenuItem mntmOpenDatabase = new JMenuItem("Open Database");
	mntmOpenDatabase.addActionListener(this);
	mnDbMenu.add(mntmOpenDatabase);
	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(new BorderLayout(0, 0));

	JPanel panel = new JPanel();
	contentPane.add(panel, BorderLayout.CENTER);

	File f = FileOpen.getLast();
	if (f != null) {
	    db.DbHandle.openMainDB(f);
	}
	// deaktivate buttons if no db is chosen
	boolean activeBtn = (DbHandle.mainDB != null);

	btnCalculation = new JButton("Abrechnung Erstellen");
	btnCalculation.setEnabled(activeBtn);
	btnCalculation.addActionListener(this);
	panel.setLayout(new GridLayout(0, 1, 1, 5));
	panel.add(btnCalculation);

	btnManageHouses = new JButton("Haus Verwaltung");
	btnManageHouses.setEnabled(activeBtn);
	btnManageHouses.setBounds(new Rectangle(0, 0, 300, 30));
	btnManageHouses.addActionListener(this);
	panel.add(btnManageHouses);

	btnHouseCost = new JButton("Haus Kosten Verwaltung");
	panel.add(btnHouseCost);
	btnHouseCost.setEnabled(activeBtn);

	JButton btnGaragenUStellpltze = new JButton("Garagen");
	btnGaragenUStellpltze.addActionListener(this);
	panel.add(btnGaragenUStellpltze);
	btnHouseCost.addActionListener(this);
	panel.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[] { btnCalculation, btnManageHouses }));
	pack();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
	switch (e.getActionCommand()) {
	case "Haus Verwaltung":
	    new WorkFrame(this, new ManagePane(), "Manage");
	    break;
	case "Abrechnung Erstellen":
	    new WorkFrame(this, new AbrechnungPane(), "Abrechnung");
	    break;
	case "Haus Kosten Verwaltung":
	    new WorkFrame(this, new CostManage(), "CostManage");
	    break;
	case "Open Database":
	    File f = FileOpen.open("accdb", "Open Database");
	    if (f != null) {
		DbHandle.openMainDB(f);
		btnCalculation.setEnabled(true);
		btnManageHouses.setEnabled(true);
		btnHouseCost.setEnabled(true);
	    }
	    break;
	case "Einstellungen":
	    GuiScaler.openOptions(this);
	    break;
	case "Garagen":
	    new WorkFrame(this, new GarageHub(), "GSHub");
	    break;
	default:
	    System.out.println(e.getActionCommand());
	}

    }

    public static void adaptDPI(int width, int height) {
	if (!GuiScaler.loadSettings()) {
	    int res = width * height;
	    System.out.println("" + width + " : " + height);
	    float scale = res / (1920f * 1080f);
	    System.out.println(scale);
	    float size = basicDPI * scale;
	    ScaleOptionPane.setDefaultSize(size);
	}

    }
}
