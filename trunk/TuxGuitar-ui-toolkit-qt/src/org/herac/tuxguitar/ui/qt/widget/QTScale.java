package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UIScale;

import com.trolltech.qt.core.Qt.Orientation;
import com.trolltech.qt.gui.QSlider;

public class QTScale extends QTAbstractSlider<QSlider> implements UIScale {
	
	public QTScale(QTContainer parent, Orientation orientation) {
		super(new QSlider(parent.getContainerControl()), parent);
		
		this.getControl().setOrientation(orientation);
		this.getControl().setInvertedAppearance(true);
	}
}
