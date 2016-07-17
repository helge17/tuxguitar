package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UIButton;

import com.trolltech.qt.gui.QPushButton;

public class QTButton extends QTAbstractButton<QPushButton> implements UIButton {
	
	public QTButton(QTContainer parent) {
		super(new QPushButton(parent.getContainerControl()), parent);
	}
	
	public void setDefaultButton() {
		this.getControl().setDefault(true);
	}
}