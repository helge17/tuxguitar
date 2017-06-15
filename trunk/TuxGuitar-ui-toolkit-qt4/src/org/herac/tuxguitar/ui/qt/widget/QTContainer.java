package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UIContainer;

import com.trolltech.qt.gui.QWidget;

public interface QTContainer extends UIContainer {
	
	QWidget getContainerControl();
	
	void addChild(QTWidget<? extends QWidget> uiControl);
	
	void removeChild(QTWidget<? extends QWidget> uiControl);
}
