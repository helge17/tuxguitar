package app.tuxguitar.ui.qt.widget;

import app.tuxguitar.ui.resource.UISize;
import app.tuxguitar.ui.widget.UIKnob;
import io.qt.widgets.QDial;

public class QTKnob extends QTAbstractSlider<QDial> implements UIKnob {

	public static final int FIXED_SIZE = 36;

	public QTKnob(QTContainer parent) {
		super(new QDial(parent.getContainerControl()), parent);
	}

	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		this.setPackedSize(new UISize(fixedWidth != null ? fixedWidth : FIXED_SIZE, fixedHeight != null ? fixedHeight : FIXED_SIZE));
	}
}
