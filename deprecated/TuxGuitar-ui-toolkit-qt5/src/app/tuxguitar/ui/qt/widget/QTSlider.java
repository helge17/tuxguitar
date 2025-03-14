package app.tuxguitar.ui.qt.widget;

import app.tuxguitar.ui.widget.UISlider;
import org.qtjambi.qt.core.Qt.Orientation;
import org.qtjambi.qt.widgets.QSlider;

public class QTSlider extends QTAbstractSlider<QSlider> implements UISlider {

	public QTSlider(QTContainer parent, Orientation orientation) {
		super(new QSlider(parent.getContainerControl()), parent);

		this.getControl().setOrientation(orientation);
	}
}
