package util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import db.Flat;
import db.House;
import db.LoadAble;
import db.LoadFabric;
import db.Meter;
import db.Tenant;
import db.Type;

public class LoadAbleListModel implements ListModel<LoadAble> {

    LinkedList<LoadAble> elements;
    LinkedList<House> houses;
    LinkedList<Flat> flats;
    LinkedList<Tenant> tenant;
    LinkedList<Meter> meter;
    private int years = 5;
    private Type type;
    private boolean mainOnly = false;
    private LoadAble filter;

    public LoadAbleListModel(Type type) {
	super();
	this.type = type;
	loadElements();
    }

    public void refresh() {
	loadElements();
	if (filter instanceof House) {
	    filterHouse((House) filter);
	} else if (filter instanceof Flat) {
	    filterFlat((Flat) filter);
	}
    }

    public void setYears(int years) {
	this.years = years;
	loadElements();
    }

    public void removeFilter() {
	elements = new LinkedList<LoadAble>();
	switch (type) {
	case Flat:
	    elements.addAll(flats);
	    break;
	case House:
	    break;
	case Meter:
	    if (isMainOnly())
		elements.addAll(meter.stream().filter(m -> m.isMain()).collect(Collectors.toList()));
	    else
		elements.addAll(meter);
	    break;
	case Tenant:
	    elements.addAll(tenant);
	    break;
	default:
	    break;

	}
    }

    public void filterHouse(House h) {
	LinkedList<LoadAble> newList = new LinkedList<LoadAble>();
	filter = h;
	switch (type) {
	case House:
	    break;
	case Flat:
	    newList.addAll(flats.stream().filter(f -> f.getHouse().equals(h)).collect(Collectors.toList()));
	    break;
	case Meter:
	    if (isMainOnly())
		newList.addAll(
			meter.stream().filter(m -> m.getHouse().equals(h) && m.isMain()).collect(Collectors.toList()));
	    else
		newList.addAll(meter.stream().filter(m -> m.getHouse().equals(h)).collect(Collectors.toList()));
	    break;
	case Tenant:
	    newList.addAll(tenant.stream().filter(t -> t.getHouse().equals(h)).collect(Collectors.toList()));
	    break;
	default:
	    break;
	}
	elements = newList;
    }

    public void filterFlat(Flat f) {
	filter = f;
	LinkedList<LoadAble> newList = new LinkedList<LoadAble>();
	switch (type) {
	case House:
	    break;
	case Flat:
	    break;
	case Meter:

	    newList.addAll(
		    meter.stream().filter(m -> m.getFlat().equals(f) && !m.isMain()).collect(Collectors.toList()));
	    break;
	case Tenant:

	    newList.addAll(tenant.stream().filter(t -> t.getFlat().equals(f)).collect(Collectors.toList()));

	    break;
	default:
	    break;
	}
	elements = newList;
    }

    @SuppressWarnings("unchecked")
    public void loadElements() {
	elements = LoadFabric.load(type, years);
	switch (type) {
	case House:
	    houses = new LinkedList<>();
	    houses.addAll((Collection<? extends House>) elements);
	    break;
	case Flat:
	    flats = new LinkedList<>();
	    flats.addAll((Collection<? extends Flat>) elements);
	    break;
	case Meter:
	    meter = new LinkedList<>();
	    meter.addAll((Collection<? extends Meter>) elements);
	    break;
	case Tenant:
	    tenant = new LinkedList<>();
	    tenant.addAll((Collection<? extends Tenant>) elements);
	}
    }

    @Override
    public LoadAble getElementAt(int index) {
	return elements.get(index);
    }

    @Override
    public int getSize() {
	return elements.size();
    }

    private LinkedList<ListDataListener> listener = new LinkedList<ListDataListener>();

    @Override
    public void addListDataListener(ListDataListener l) {
	listener.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
	listener.remove(l);
    }

    public boolean isMainOnly() {
	return mainOnly;
    }

    public void setMainOnly(boolean mainOnly) {
	this.mainOnly = mainOnly;
    }

}
