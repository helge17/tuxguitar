package org.herac.tuxguitar.ui.qt.toolbar;

import org.herac.tuxguitar.ui.qt.widget.QTWidget;
import org.qtjambi.qt.widgets.QWidget;

public abstract class QTToolItem<T extends QWidget> extends QTWidget<T> {
	
	public QTToolItem(T control, QTToolBar toolBar) {
		super(control, toolBar);
	}
	
	public QTToolBar getToolBar() {
		return (QTToolBar) this.getParent();
	}
}
