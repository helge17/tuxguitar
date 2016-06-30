package org.herac.tuxguitar.ui.jfx.event;

import org.herac.tuxguitar.ui.event.UICheckTableSelectionEvent;
import org.herac.tuxguitar.ui.event.UICheckTableSelectionListenerManager;
import org.herac.tuxguitar.ui.jfx.JFXComponent;
import org.herac.tuxguitar.ui.widget.UITableItem;

public class JFXCheckTableSelectionListenerManager<T> extends UICheckTableSelectionListenerManager<T> {
	
	private JFXComponent<?> control;
	
	public JFXCheckTableSelectionListenerManager(JFXComponent<?> control) {
		this.control = control;
	}
	
	public void fireEvent(UITableItem<T> checkedItem) {
		this.onSelect(new UICheckTableSelectionEvent<T>(this.control, checkedItem));
	}
}
