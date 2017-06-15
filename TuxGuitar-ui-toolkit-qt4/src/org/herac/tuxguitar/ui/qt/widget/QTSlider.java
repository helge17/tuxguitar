package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UISlider;

import com.trolltech.qt.core.Qt.Orientation;
import com.trolltech.qt.gui.QSlider;

public class QTSlider extends QTAbstractSlider<QSlider> implements UISlider {
	
	public QTSlider(QTContainer parent, Orientation orientation) {
		super(new QSlider(parent.getContainerControl()), parent);
		
		this.getControl().setOrientation(orientation);
	}
}
