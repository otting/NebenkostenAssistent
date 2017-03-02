package db;

import java.util.Comparator;
import java.util.Date;

import com.healthmarketscience.jackcess.Row;

public class RowCompare implements Comparator<Row> {

    private String colname;

    @Override
    public int compare(Row a, Row b) {
	Date aa = a.getDate(colname);
	Date bb = b.getDate(colname);
	if (aa == null)
	    return -1;
	if (bb == null)
	    return 1;

	return aa.compareTo(bb);
    }

    public RowCompare(String colname) {
	this.colname = colname;
    }
}
