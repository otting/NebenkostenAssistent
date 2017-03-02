package gui.panes.costs;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class CostManage extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -7799075799439993517L;

    /**
     * Create the panel.
     */
    public CostManage() {
	JTabbedPane tabbedPane = new JTabbedPane();
	tabbedPane.addTab("Pro Haus", new OneHouse());
	tabbedPane.addTab("Alle Häuser", new AllHouses());

	setLayout(new GridLayout(0, 1, 0, 0));
	add(tabbedPane);

    }

}
