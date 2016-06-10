package org.herac.tuxguitar.ui.widget;

import org.herac.tuxguitar.ui.event.UILinkListener;

public interface UILinkLabel extends UIWrapLabel {
	
	void addLinkListener(UILinkListener listener);
	
	void removeLinkListener(UILinkListener listener);
}
