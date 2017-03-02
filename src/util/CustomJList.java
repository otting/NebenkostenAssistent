package util;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

import db.Flat;
import db.House;
import db.LoadAble;
import db.Type;

public class CustomJList extends JList<LoadAble> {

    /**
     * 
     */
    private static final long serialVersionUID = 3621551221093829874L;
    private LoadAbleListModel model;

    /**
     * 
     * @param type
     *            refers to constant in LoadFabric
     */
    public CustomJList(Type type) {
	super();
	setModel(new LoadAbleListModel(type));
	setBorder(javax.swing.border.LineBorder.createGrayLineBorder());
	setSingleSelection(true);
    }

    public void setSingleSelection(boolean select) {
	if (select)
	    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	else
	    setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    public void setYears(int years) {
	model.setYears(years);
    }

    public void refresh() {
	model.refresh();
	int[] i = new int[1];
	i[0] = -1;
	setSelectedIndices(i);
	repaint();
    }

    public void filter(House hous) {
	if (hous == null)
	    removeFilter();
	else
	    model.filterHouse(hous);
    }

    public void filter(Flat f) {
	if (f == null)
	    removeFilter();
	else
	    model.filterFlat(f);
    }

    public void removeFilter() {
	model.removeFilter();
    }

    private void setModel(LoadAbleListModel model) {
	super.setModel(model);
	this.model = model;
    }

}
