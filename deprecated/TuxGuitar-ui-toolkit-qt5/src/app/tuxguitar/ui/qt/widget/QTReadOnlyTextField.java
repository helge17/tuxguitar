package app.tuxguitar.ui.qt.widget;

import app.tuxguitar.ui.widget.UIReadOnlyTextField;

public class QTReadOnlyTextField extends QTTextField implements UIReadOnlyTextField {

	public QTReadOnlyTextField(QTContainer parent) {
		super(parent);

		this.getControl().setReadOnly(true);
	}
}