package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UIRadioButton;

import com.trolltech.qt.gui.QRadioButton;

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