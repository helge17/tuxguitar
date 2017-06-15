package org.herac.tuxguitar.ui.qt.event;

import org.herac.tuxguitar.ui.event.UIMouseDownListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.qt.widget.QTWidget;

public class QTMenuOpenListenerManager implements UIMouseDownListener {
	
	private QTWidget<?> control;
	
	public QTMenuOpenListenerManager(QTWidget<?> control) {
		this.control = control;
	}
	
	@Override
	public void onMouseDown(UIMouseEvent event) {
		if( this.control.getPopupMenu() != null && event.getButton() == 3 ) {
			this.control.openPopupMenu(event.getPosition());
		}
	}
}
