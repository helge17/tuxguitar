package app.tuxguitar.ui.swt.widget;

import org.eclipse.swt.widgets.Composite;
import app.tuxguitar.ui.widget.UIContainer;
import app.tuxguitar.ui.widget.UIControl;

public interface SWTContainer<T extends Composite> extends UIContainer {

	T getControl();

	void addChild(UIControl uiControl);

	void removeChild(UIControl uiControl);
}
