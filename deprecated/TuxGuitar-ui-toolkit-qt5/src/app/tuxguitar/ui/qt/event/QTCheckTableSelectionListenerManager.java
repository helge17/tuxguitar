package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UICheckTableSelectionEvent;
import app.tuxguitar.ui.event.UICheckTableSelectionListenerManager;
import app.tuxguitar.ui.qt.widget.QTTable;
import app.tuxguitar.ui.widget.UITableItem;

public class QTCheckTableSelectionListenerManager<T> extends UICheckTableSelectionListenerManager<T> {

	public static final String SIGNAL_METHOD = "handle(Integer,Integer)";

	private QTTable<T> control;

	public QTCheckTableSelectionListenerManager(QTTable<T> control) {
		this.control = control;
	}

	public void handle(UITableItem<T> selectedItem) {
		this.onSelect(new UICheckTableSelectionEvent<T>(this.control, selectedItem));
	}

	public void handle(Integer row, Integer column) {
		if( row != null && column != null && column == 0 ) {
			this.handle(this.control.getItem(row));
		}
	}
}
