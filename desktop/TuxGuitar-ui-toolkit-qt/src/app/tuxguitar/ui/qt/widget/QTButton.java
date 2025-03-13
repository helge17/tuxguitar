package app.tuxguitar.ui.qt.widget;

import app.tuxguitar.ui.widget.UIButton;
import io.qt.widgets.QPushButton;

public class QTButton extends QTAbstractButton<QPushButton> implements UIButton {

	public QTButton(QTContainer parent) {
		super(new QPushButton(parent.getContainerControl()), parent);
	}

	public void setDefaultButton() {
		this.getControl().setDefault(true);
	}
}