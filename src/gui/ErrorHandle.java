package gui;

import java.awt.Color;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class ErrorHandle {
    private static boolean active = true;

    public static void popUp(String msg) {
	if (active)
	    JOptionPane.showMessageDialog(null, msg, "Fehler", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean askYesNo(String msg) {
	JTextArea txt = new JTextArea(msg);
	new UIManager();

	txt.setBackground((Color) UIManager.get("OptionPane.background"));
	return (JOptionPane.showConfirmDialog(null, txt, "", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
    }

    public static void silence() {
	active = false;
    }

    public static void activate() {
	active = true;
    }
}
