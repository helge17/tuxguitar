package org.herac.tuxguitar.ui.widget;

import org.herac.tuxguitar.ui.event.UIPaintListener;

public interface UICanvas extends UIControl {

	void addPaintListener(UIPaintListener listener);
	
	void removePaintListener(UIPaintListener listener);
}
