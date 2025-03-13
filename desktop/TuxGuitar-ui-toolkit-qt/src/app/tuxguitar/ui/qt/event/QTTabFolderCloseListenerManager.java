package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UICloseEvent;
import app.tuxguitar.ui.event.UICloseListenerManager;
import app.tuxguitar.ui.qt.widget.QTTabFolder;
import app.tuxguitar.ui.widget.UITabItem;

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
