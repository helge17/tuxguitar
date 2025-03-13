package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListenerManager;
import app.tuxguitar.ui.qt.widget.QTTabFolder;
import app.tuxguitar.ui.widget.UITabItem;

public class QTTabFolderSelectionListenerManager extends UISelectionListenerManager {

	public static final String SIGNAL_METHOD = "handle(Integer)";

	private QTTabFolder control;

	public QTTabFolderSelectionListenerManager(QTTabFolder control) {
		this.control = control;
	}

	public void handle(UITabItem selectedItem) {
		this.onSelect(new UISelectionEvent(selectedItem));
	}

	public void handle(Integer row) {
		if( row != null ) {
			this.handle(this.control.getTab(row));
		}
	}
}
