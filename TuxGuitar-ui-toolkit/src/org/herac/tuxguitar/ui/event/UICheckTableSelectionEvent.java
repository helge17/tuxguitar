package org.herac.tuxguitar.ui.event;

import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.widget.UITableItem;

public class UICheckTableSelectionEvent<T> extends UISelectionEvent {
	
	private UITableItem<T> selectedItem;
	
	public UICheckTableSelectionEvent(UIComponent control, UITableItem<T> selectedItem) {
		super(control);
		
		this.selectedItem = selectedItem;
	}

	public UITableItem<T> getSelectedItem() {
		return selectedItem;
	}
}
