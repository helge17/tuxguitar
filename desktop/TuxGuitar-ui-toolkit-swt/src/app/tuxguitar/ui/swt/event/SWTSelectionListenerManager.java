package app.tuxguitar.ui.swt.event;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListenerManager;
import app.tuxguitar.ui.swt.widget.SWTEventReceiver;

public class SWTSelectionListenerManager extends UISelectionListenerManager implements SelectionListener {

	private SWTEventReceiver<?> control;

	public SWTSelectionListenerManager(SWTEventReceiver<?> control) {
		this.control = control;
	}

	public SWTEventReceiver<?> getControl() {
		return this.control;
	}

	public void widgetSelected(SelectionEvent e) {
		if(!this.control.isIgnoreEvents()) {
			// warning, when a SWT radio button is selected, if another radio button was selected it gets unselected and also generates an event
			// only consider the selected radio button
			boolean doIt = true;
			if ((e.widget instanceof MenuItem) && ((e.widget.getStyle() & SWT.RADIO) != 0)) {
				doIt = ((MenuItem)e.widget).getSelection();
			}
			if (doIt) {
				this.onSelect(new UISelectionEvent(this.control));
			}
		}
	}

	public void widgetDefaultSelected(SelectionEvent event) {
		// Nothing to do
	}
}
