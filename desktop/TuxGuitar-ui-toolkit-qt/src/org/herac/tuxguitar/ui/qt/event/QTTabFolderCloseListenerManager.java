package org.herac.tuxguitar.ui.qt.event;

import org.herac.tuxguitar.ui.event.UICloseEvent;
import org.herac.tuxguitar.ui.event.UICloseListenerManager;
import org.herac.tuxguitar.ui.qt.widget.QTTabFolder;
import org.herac.tuxguitar.ui.widget.UITabItem;

public class QTTabFolderCloseListenerManager extends UICloseListenerManager {
	
	public static final String SIGNAL_METHOD = "handle(Integer)";
	
	private QTTabFolder control;
	
	public QTTabFolderCloseListenerManager(QTTabFolder control) {
		this.control = control;
	}
	
	public void handle(UITabItem selectedItem) {
		this.onClose(new UICloseEvent(selectedItem));
	}
	
	public void handle(Integer row) {
		if( row != null ) {
			this.handle(this.control.getTab(row));
		}
	}
}
