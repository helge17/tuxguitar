package org.herac.tuxguitar.ui.qt.menu;

import org.herac.tuxguitar.ui.menu.UIMenuBar;

import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMenuBar;

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
