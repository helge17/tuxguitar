package org.herac.tuxguitar.ui.qt.toolbar;

import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QWidget;

public class QTToolSeparatorItem extends QTToolItem<QWidget> {
	
	private QAction action;

	public QTToolSeparatorItem(QTToolBar parent, QAction action) {
		super(parent.getControl().widgetForAction(action), parent);
		
		this.action = action;
	}
	
	public QTToolSeparatorItem(QTToolBar parent) {
		this(parent, parent.getControl().addSeparator());
	}
	
	public void dispose() {
		this.getToolBar().getControl().removeAction(this.action);
		
		super.dispose();
	}
}
