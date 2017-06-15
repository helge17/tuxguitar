package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UIIndeterminateProgressBar;

import com.trolltech.qt.gui.QProgressBar;

public class QTIndeterminateProgressBar extends QTWidget<QProgressBar> implements UIIndeterminateProgressBar {
	
	private static final int INDETERMINATE_VALUE = 0;
	
	public QTIndeterminateProgressBar(QTContainer parent) {
		super(new QProgressBar(parent.getContainerControl()), parent);
		
		this.getControl().setMinimum(INDETERMINATE_VALUE);
		this.getControl().setMaximum(INDETERMINATE_VALUE);
	}
}