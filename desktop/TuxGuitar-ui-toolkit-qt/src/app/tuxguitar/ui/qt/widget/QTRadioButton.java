package app.tuxguitar.ui.qt.widget;

import app.tuxguitar.ui.widget.UIRadioButton;
import io.qt.widgets.QRadioButton;

public class QTRadioButton extends QTAbstractButton<QRadioButton> implements UIRadioButton {

	public QTRadioButton(QTContainer parent) {
		super(new QRadioButton(parent.getContainerControl()), parent);
	}

	public boolean isSelected() {
		return this.getControl().isChecked();
	}

	public void setSelected(boolean selected) {
		this.getControl().setChecked(selected);
	}
}