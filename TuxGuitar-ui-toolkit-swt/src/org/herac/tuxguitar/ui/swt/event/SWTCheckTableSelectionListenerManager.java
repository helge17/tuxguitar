package org.herac.tuxguitar.ui.swt.event;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.herac.tuxguitar.ui.event.UICheckTableSelectionEvent;
import org.herac.tuxguitar.ui.event.UICheckTableSelectionListenerManager;
import org.herac.tuxguitar.ui.swt.SWTComponent;
import org.herac.tuxguitar.ui.widget.UITableItem;

public class SWTCheckTableSelectionListenerManager<T> extends UICheckTableSelectionListenerManager<T> implements SelectionListener {
	
	private SWTComponent<?> control;
	
	public SWTCheckTableSelectionListenerManager(SWTComponent<?> control) {
		this.control = control;
	}
	
	@SuppressWarnings("unchecked")
	public void widgetSelected(SelectionEvent event) {
		if( event.detail == SWT.CHECK && event.item != null && event.item.getData() instanceof UITableItem<?> ){
			this.onSelect(new UICheckTableSelectionEvent<T>(this.control, (UITableItem<T>) event.item.getData()));
		}
	}
	
	public void widgetDefaultSelected(SelectionEvent event) {
		// Nothing to do
	}
}
