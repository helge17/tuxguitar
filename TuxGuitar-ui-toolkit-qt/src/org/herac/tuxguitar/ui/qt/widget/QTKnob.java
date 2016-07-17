package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UIKnob;

import com.trolltech.qt.gui.QDial;

public class QTKnob extends QTAbstractSlider<QDial> implements UIKnob {
	
	public static final int FIXED_SIZE = 36;
	
	public QTKnob(QTContainer parent) {
		super(new QDial(parent.getContainerControl()), parent);
		
		this.getControl().setMaximumWidth(FIXED_SIZE);
		this.getControl().setMaximumHeight(FIXED_SIZE);
	}
}
