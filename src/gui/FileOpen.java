package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.filechooser.FileFilter;

import javax.swing.JFileChooser;

/**
 * This class has only the purpose of opening a file via JFileChooser the last
 * location used will be saved for convenience
 * 
 * @author christian
 *
 */
public class FileOpen {

    /**
     * optional call for open with Filter and Header
     * 
     * @param fileEnd
     *            can be null,
     * @param head
     *            can be null
     * @return
     */
    public static File open(String fileEnd, String head) {
	if (fileEnd != null)
	    filter = new FileFilter() {

		@Override
		public boolean accept(File f) {
		    return (f.isDirectory() || f.getPath().endsWith(fileEnd));
		}

		@Override
		public String getDescription() {
		    return fileEnd;
		}

	    };
	if (head != null) {
	    fcHead = head;
	}
	File file = open();
	filter = null;
	fcHead = null;
	return file;
    }

    private static FileFilter filter;
    private static String fcHead;

    public static File open() {

	File dir = new File(System.getProperty("user.dir"));

	JFileChooser fc = new JFileChooser();
	fc.setCurrentDirectory(dir);
	if (filter != null)
	    fc.setFileFilter(filter);
	if (fcHead != null)
	    fc.setDialogTitle(fcHead);
	fc.showOpenDialog(null);
	File f = fc.getSelectedFile();
	if (f == null) {
	    return null;
	}

	// save path of last file
	File last = new File("lastFile.tmp");
	if (last.exists()) {
	    last.delete();
	}
	PrintWriter w = null;
	try {
	    w = new PrintWriter(last);
	    w.println(f.getPath());
	} catch (FileNotFoundException e) {
	    System.err.println("could not create File for last used path");
	} finally {
	    w.close();
	}

	return f;
    }

    /**
     * 
     * @return last Db used
     */
    public static File getLast() {
	File last = new File("lastFile.tmp");
	try {
	    if (last.exists()) {
		FileReader fr = new FileReader(last);
		BufferedReader br = new BufferedReader(fr);
		String path = br.readLine();
		br.close();
		if (path != null && path.length() > 1) {
		    File f = new File(path);
		    if (f.exists()) {
			return f;
		    }
		}
	    }
	} catch (IOException e1) {
	    System.err.println("Could not access last opened File");
	    return null;
	}
	return null;
    }
}
