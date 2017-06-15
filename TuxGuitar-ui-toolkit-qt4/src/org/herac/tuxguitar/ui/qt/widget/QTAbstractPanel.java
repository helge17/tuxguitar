package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UIPanel;

import com.trolltech.qt.gui.QFrame;
import com.trolltech.qt.gui.QFrame.Shape;

public abstract class QTAbstractPanel<T extends QFrame> extends QTLayoutContainer<T> implements UIPanel {
	
	public QTAbstractPanel(T control, QTContainer parent, boolean bordered) {
		super(control, parent);
		
		this.getControl().setAutoFillBackground(true);
		this.getControl().setFrameShape(bordered ? Shape.StyledPanel : Shape.NoFrame);
	}
}
