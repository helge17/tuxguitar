package org.herac.tuxguitar.ui.qt.menu;

import org.herac.tuxguitar.ui.menu.UIMenuBar;
import org.herac.tuxguitar.ui.qt.widget.QTAbstractWindow;

import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMenuBar;

public class QTMenuBar extends QTAbstractMenu<QMenuBar> implements UIMenuBar {
	
	private QTAbstractWindow<?> window;
	
	public QTMenuBar(QTAbstractWindow<?> window) {
		super(new QMenuBar(window.getControl()));
		
		this.window = window;
		this.window.setMenuBar(this);
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
	
	public void updateVisibility() {
		this.getControl().setVisible(false);
		this.getControl().setVisible(true);
	}
	
	public void dispose() {
		this.window.setMenuBar(null);
		
		super.dispose();
	}
}
