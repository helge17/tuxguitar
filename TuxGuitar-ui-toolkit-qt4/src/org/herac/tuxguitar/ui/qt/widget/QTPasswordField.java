package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UIPasswordField;

import com.trolltech.qt.gui.QLineEdit.EchoMode;

public class QTPasswordField extends QTTextField implements UIPasswordField {
	
	public QTPasswordField(QTContainer parent) {
		super(parent);
		
		this.getControl().setEchoMode(EchoMode.Password);
	}
}