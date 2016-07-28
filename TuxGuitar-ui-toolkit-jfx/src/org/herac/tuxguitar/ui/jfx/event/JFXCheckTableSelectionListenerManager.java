package org.herac.tuxguitar.ui.jfx.event;

import org.herac.tuxguitar.ui.event.UICheckTableSelectionEvent;
import org.herac.tuxguitar.ui.event.UICheckTableSelectionListenerManager;
import org.herac.tuxguitar.ui.jfx.widget.JFXEventReceiver;
import org.herac.tuxguitar.ui.widget.UITableItem;

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
