package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UISeparator;

import com.trolltech.qt.core.Qt.Orientation;
import com.trolltech.qt.gui.QFrame;
import com.trolltech.qt.gui.QFrame.Shape;

public class QTSeparator extends QTWidget<QFrame> implements UISeparator {
	
	public QTSeparator(QTContainer parent, Orientation orientation) {
		super(new QFrame(parent.getContainerControl()), parent);
		
		this.getControl().setFrameShadow(QFrame.Shadow.Sunken);
		this.getControl().setFrameShape(Orientation.Horizontal.equals(orientation) ? Shape.HLine : Shape.VLine);
	}
}
