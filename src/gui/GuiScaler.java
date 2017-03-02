package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;

import gui.panes.ScaleOptionPane;

public class GuiScaler {

    public static float fonzSize = 11;

    public static void openOptions(JFrame callFrame) {
	JDialog frame = new JDialog(callFrame, "Font einstellung", true);
	frame.setBounds(callFrame.getBounds());
	frame.setLocationRelativeTo(callFrame);
	frame.setContentPane(new ScaleOptionPane());
	frame.setVisible(true);
	frame.setResizable(false);

    }

    public static boolean loadSettings() {
	File f = new File(ScaleOptionPane.CONFIG_PATH);
	float size;
	if (f.exists() && f.isFile()) {
	    try {
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		size = Float.parseFloat(br.readLine());
		fr.close();
	    } catch (IOException e) {
		e.printStackTrace();
		return false;
	    }
	    ScaleOptionPane.setDefaultSize(size);
	    return true;

	} else
	    return false;
    }
}
