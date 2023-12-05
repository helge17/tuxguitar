package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UISeparator;
import org.qtjambi.qt.core.Qt.Orientation;
import org.qtjambi.qt.widgets.QFrame;
import org.qtjambi.qt.widgets.QFrame.Shape;

public class QTSeparator extends QTWidget<QFrame> implements UISeparator {
	
	public QTSeparator(QTContainer parent, Orientation orientation) {
		super(new QFrame(parent.getContainerControl()), parent);
		
		this.getControl().setFrameShadow(QFrame.Shadow.Sunken);
		this.getControl().setFrameShape(Orientation.Horizontal.equals(orientation) ? Shape.HLine : Shape.VLine);
	}
}
