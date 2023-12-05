package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UIToggleButton;

public class QTToggleButton extends QTButton implements UIToggleButton {
	
	public QTToggleButton(QTContainer parent) {
		super(parent);
		
		this.getControl().setCheckable(true);
	}
	
	public boolean isSelected() {
		return this.getControl().isChecked();
	}
	
	public void setSelected(boolean selected) {
		this.getControl().setChecked(selected);
	}
}