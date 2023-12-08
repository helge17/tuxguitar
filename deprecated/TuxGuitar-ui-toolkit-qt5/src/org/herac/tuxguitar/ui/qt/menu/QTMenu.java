package org.herac.tuxguitar.ui.qt.menu;

import org.qtjambi.qt.widgets.QAction;
import org.qtjambi.qt.widgets.QMenu;

public class QTMenu extends QTAbstractMenu<QMenu> {
	
	public QTMenu(QMenu control) {
		super(control);
	}
	
	public QMenu createNativeMenu() {
		return this.getControl().addMenu(new String());
	}
	
	public QAction createNativeAction() {
		return this.getControl().addAction(new String());
	}
	
	public QAction createNativeSeparator() {
		return this.getControl().addSeparator();
	}
}
