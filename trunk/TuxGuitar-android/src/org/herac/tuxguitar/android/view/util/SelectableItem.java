package org.herac.tuxguitar.android.view.util;

public class SelectableItem {
	
	private Object item;
	private String label;
	private String dropDownLabel;
	
	public SelectableItem(Object item, String label, String dropDownLabel) {
		this.item = item;
		this.label = label;
		this.dropDownLabel = dropDownLabel;
	}
	
	public SelectableItem(Object item, String label) {
		this(item, label, label);
	}
	
	public Object getItem() {
		return this.item;
	}
	
	public String getLabel() {
		return label;
	}

	public String getDropDownLabel() {
		return dropDownLabel;
	}

	public String toString() {
		return getLabel();
	}
	
	public boolean equals(Object o) {
		if( o instanceof SelectableItem ) {
			Object item1 = getItem();
			Object item2 = ((SelectableItem) o).getItem();
			if( item1 != null && item2 != null ) {
				return item1.equals(item2);
			}
			return (item1 == item2);
		}
		return false;
	}
}
