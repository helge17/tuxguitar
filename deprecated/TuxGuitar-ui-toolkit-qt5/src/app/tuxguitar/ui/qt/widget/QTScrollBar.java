package app.tuxguitar.ui.qt.widget;

import app.tuxguitar.ui.widget.UIScrollBar;
import org.qtjambi.qt.widgets.QScrollBar;

public class QTScrollBar extends QTAbstractSlider<QScrollBar> implements UIScrollBar {

	public QTScrollBar(QScrollBar scrollBar, QTContainer parent) {
		super(scrollBar, parent);
	}
}
