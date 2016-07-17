package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UILabel;

import com.trolltech.qt.gui.QLabel;

public class QTLabel extends QTWidget<QLabel> implements UILabel {
	
	public QTLabel(QTContainer parent) {
		super(new QLabel(parent.getContainerControl()), parent);
	}

	public String getText() {
		return this.getControl().text();
	}

	public void setText(String text) {
		this.getControl().setText(text);
	}
}