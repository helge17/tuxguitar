package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UIReadOnlyTextField;

public class QTReadOnlyTextField extends QTTextField implements UIReadOnlyTextField {
	
	public QTReadOnlyTextField(QTContainer parent) {
		super(parent);
		
		this.getControl().setReadOnly(true);
	}
}