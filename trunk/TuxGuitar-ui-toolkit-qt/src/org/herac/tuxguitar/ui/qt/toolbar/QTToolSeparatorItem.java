package org.herac.tuxguitar.ui.qt.toolbar;

import com.trolltech.qt.gui.QWidget;

public class QTToolSeparatorItem extends QTToolItem<QWidget> {
	
	public QTToolSeparatorItem(QTToolBar parent) {
		super(parent.getControl().widgetForAction(parent.getControl().addSeparator()), parent);
	}
}
