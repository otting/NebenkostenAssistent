package gui;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

    public static void unknowenError(Exception e) {
	popUp("Ein unerwarteter Fehler ist aufgetreten" + "\nBitte senden sie ErrorLog.txt an den Entwickler.");
	File f = new File("ErrorLog.txt");

	if (f.exists()) {
	    appendText(f, e);
	} else {
	    try {
		write(new BufferedWriter(new FileWriter(f)), e);
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	}

	e.printStackTrace();
    }

    private static void appendText(File f, Exception e) {
	try {
	    BufferedWriter writer = new BufferedWriter(new FileWriter(f, true));
	    write(writer, e);
	} catch (IOException ex) {
	    e.printStackTrace();
	}

    }

    private static void write(BufferedWriter writer, Exception e) {
	try {
	    writer.write("\n------------------------------------------------------\n");
	    writer.write(e.toString() + "\n");
	    for (StackTraceElement st : e.getStackTrace()) {
		writer.write(st.toString() + "\n");
	    }
	} catch (IOException e1) {
	    e1.printStackTrace();
	} finally {
	    try {
		writer.close();
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	}

    }
}
