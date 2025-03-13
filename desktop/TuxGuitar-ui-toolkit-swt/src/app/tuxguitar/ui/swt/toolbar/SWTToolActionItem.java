package app.tuxguitar.ui.swt.toolbar;

import org.eclipse.swt.widgets.ToolItem;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.swt.event.SWTSelectionListenerManager;
import app.tuxguitar.ui.toolbar.UIToolActionItem;

public class SWTToolActionItem extends SWTToolItem implements UIToolActionItem {

	private SWTSelectionListenerManager selectionListener;

	public SWTToolActionItem(ToolItem item, SWTToolBar parent) {
		super(item, parent);

		this.selectionListener = new SWTSelectionListenerManager(this);
	}

	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().addSelectionListener(this.selectionListener);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().removeSelectionListener(this.selectionListener);
		}
	}
}
