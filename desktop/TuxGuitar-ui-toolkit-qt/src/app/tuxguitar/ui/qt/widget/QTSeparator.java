package app.tuxguitar.ui.qt.widget;

import app.tuxguitar.ui.widget.UISeparator;
import io.qt.core.Qt.Orientation;
import io.qt.widgets.QFrame;
import io.qt.widgets.QFrame.Shape;

public class QTSeparator extends QTWidget<QFrame> implements UISeparator {

	public QTSeparator(QTContainer parent, Orientation orientation) {
		super(new QFrame(parent.getContainerControl()), parent);

		this.getControl().setFrameShadow(QFrame.Shadow.Sunken);
		this.getControl().setFrameShape(Orientation.Horizontal.equals(orientation) ? Shape.HLine : Shape.VLine);
	}
}
