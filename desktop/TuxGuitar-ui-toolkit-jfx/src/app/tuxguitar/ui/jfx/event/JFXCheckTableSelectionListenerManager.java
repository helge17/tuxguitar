package app.tuxguitar.ui.jfx.event;

import app.tuxguitar.ui.event.UICheckTableSelectionEvent;
import app.tuxguitar.ui.event.UICheckTableSelectionListenerManager;
import app.tuxguitar.ui.jfx.widget.JFXEventReceiver;
import app.tuxguitar.ui.widget.UITableItem;

public class JFXCheckTableSelectionListenerManager<T> extends UICheckTableSelectionListenerManager<T> {

	private JFXEventReceiver<?> control;

	public JFXCheckTableSelectionListenerManager(JFXEventReceiver<?> control) {
		this.control = control;
	}

	public void fireEvent(UITableItem<T> checkedItem) {
		if(!this.control.isIgnoreEvents()) {
			this.onSelect(new UICheckTableSelectionEvent<T>(this.control, checkedItem));
		}
	}
}
