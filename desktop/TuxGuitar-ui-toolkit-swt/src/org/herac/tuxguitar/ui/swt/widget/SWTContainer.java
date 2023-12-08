package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIControl;

public interface SWTContainer<T extends Composite> extends UIContainer {
	
	T getControl();
	
	void addChild(UIControl uiControl);
	
	void removeChild(UIControl uiControl);
}
