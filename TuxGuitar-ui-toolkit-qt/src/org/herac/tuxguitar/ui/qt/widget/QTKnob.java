package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIKnob;

import com.trolltech.qt.gui.QDial;

public class QTKnob extends QTAbstractSlider<QDial> implements UIKnob {
	
	public static final int FIXED_SIZE = 36;
	
	public QTKnob(QTContainer parent) {
		super(new QDial(parent.getContainerControl()), parent);
	}
	
	public void computePackedSize() {
		this.setPackedSize(new UISize(FIXED_SIZE, FIXED_SIZE));
	}
}
