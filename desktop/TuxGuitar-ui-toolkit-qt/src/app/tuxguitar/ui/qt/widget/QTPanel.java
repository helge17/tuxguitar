package app.tuxguitar.ui.qt.widget;

import app.tuxguitar.ui.widget.UIPanel;
import io.qt.widgets.QFrame;

public class QTPanel extends QTAbstractPanel<QFrame> implements UIPanel {

	public QTPanel(QTContainer parent, boolean bordered) {
		super(new QFrame(parent.getContainerControl()), parent, bordered);
	}
}
