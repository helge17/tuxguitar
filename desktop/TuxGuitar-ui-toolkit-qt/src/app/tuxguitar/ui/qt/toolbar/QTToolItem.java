package app.tuxguitar.ui.qt.toolbar;

import app.tuxguitar.ui.qt.widget.QTWidget;
import io.qt.widgets.QWidget;

public abstract class QTToolItem<T extends QWidget> extends QTWidget<T> {

	public QTToolItem(T control, QTToolBar toolBar) {
		super(control, toolBar);
	}

	public QTToolBar getToolBar() {
		return (QTToolBar) this.getParent();
	}
}
