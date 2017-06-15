package org.herac.tuxguitar.ui.qt.menu;

import org.herac.tuxguitar.ui.menu.UIMenuBar;

import org.qtjambi.qt.widgets.QAction;
import org.qtjambi.qt.widgets.QMenu;
import org.qtjambi.qt.widgets.QMenuBar;

public class QTMenuBar extends QTAbstractMenu<QMenuBar> implements UIMenuBar {
	
	public QTMenuBar() {
		super(new QMenuBar());
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
