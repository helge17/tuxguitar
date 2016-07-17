package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UICheckBox;

import com.trolltech.qt.gui.QCheckBox;

public class QTCheckBox extends QTAbstractButton<QCheckBox> implements UICheckBox {
	
	public QTCheckBox(QTContainer parent) {
		super(new QCheckBox(parent.getContainerControl()), parent);
	}
	
	public boolean isSelected() {
		return this.getControl().isChecked();
	}
	
	public void setSelected(boolean selected) {
		this.getControl().setChecked(selected);
	}
}