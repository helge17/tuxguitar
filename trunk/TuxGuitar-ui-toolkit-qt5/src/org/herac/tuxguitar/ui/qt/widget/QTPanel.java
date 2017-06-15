package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UIPanel;
import org.qtjambi.qt.widgets.QFrame;

public class QTPanel extends QTAbstractPanel<QFrame> implements UIPanel {
	
	public QTPanel(QTContainer parent, boolean bordered) {
		super(new QFrame(parent.getContainerControl()), parent, bordered);
	}
}
