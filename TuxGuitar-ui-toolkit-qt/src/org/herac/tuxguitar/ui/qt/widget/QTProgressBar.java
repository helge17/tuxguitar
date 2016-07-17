package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UIProgressBar;

import com.trolltech.qt.gui.QProgressBar;

public class QTProgressBar extends QTWidget<QProgressBar> implements UIProgressBar {
	
	public QTProgressBar(QTContainer parent) {
		super(new QProgressBar(parent.getContainerControl()), parent);
	}

	public void setValue(int value) {
		this.getControl().setValue(value);
	}

	public int getValue() {
		return this.getControl().value();
	}

	public void setMaximum(int maximum) {
		this.getControl().setMaximum(maximum);
	}

	public int getMaximum() {
		return this.getControl().maximum();
	}

	public void setMinimum(int minimum) {
		this.getControl().setMinimum(minimum);
	}

	public int getMinimum() {
		return this.getControl().minimum();
	}
}