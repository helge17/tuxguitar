package app.tuxguitar.ui.qt.widget;

import app.tuxguitar.ui.widget.UIContainer;
import org.qtjambi.qt.widgets.QWidget;

public interface QTContainer extends UIContainer {

	QWidget getContainerControl();

	void addChild(QTWidget<? extends QWidget> uiControl);

	void removeChild(QTWidget<? extends QWidget> uiControl);
}
