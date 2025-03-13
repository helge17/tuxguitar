package app.tuxguitar.ui.event;

import app.tuxguitar.ui.UIComponent;
import app.tuxguitar.ui.widget.UITableItem;

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
