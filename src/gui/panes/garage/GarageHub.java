package gui.panes.garage;

import javax.swing.JTabbedPane;

public class GarageHub extends JTabbedPane {

    /**
     * 
     */
    private static final long serialVersionUID = 1363541973557055097L;

    /**
     * Create the panel.
     */
    public GarageHub() {
	addTab("Nebenkosten", new GarageNebenkostenPane());
	addTab("Garagen", new GarageManage());
	addTab("Stellplätze", new StellPManage());
    }

}
