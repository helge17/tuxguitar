package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UIReadOnlyTextBox;

public class QTReadOnlyTextBox extends QTTextArea implements UIReadOnlyTextBox {
	
	public QTReadOnlyTextBox(QTContainer parent, boolean vScroll, boolean hScroll) {
		super(parent, vScroll, hScroll);
		
		this.getControl().setReadOnly(true);
	}
}