package app.tuxguitar.ui.qt.widget;

import app.tuxguitar.ui.resource.UISize;
import app.tuxguitar.ui.widget.UIScrollBar;
import io.qt.widgets.QScrollBar;

public class QTScrollBar extends QTAbstractSlider<QScrollBar> implements UIScrollBar {

	public QTScrollBar(QScrollBar scrollBar, QTContainer parent) {
		super(scrollBar, parent);
	}

	public UISize getSize() {
		// TODO QT 5->6 - see commit f5c9ee16f4cb5b9b0c7802edd652f1be2fe3ad82
		return (new UISize((float) this.getControl().getWidth(), (float) this.getControl().getHeight()));
	}
}
