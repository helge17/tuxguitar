package org.herac.tuxguitar.android.view.expandableList;

import java.util.ArrayList;
import java.util.List;

public class TGExpandableGroup extends TGExpandableResource {

	private int text;
	private List<TGExpandableItem> items;
	
	public TGExpandableGroup(int text) {
		this.text = text;
		this.items = new ArrayList<TGExpandableItem>();
	}
	
	public int getText() {
		return text;
	}
	
	public List<TGExpandableItem> getItems() {
		return items;
	}
}
