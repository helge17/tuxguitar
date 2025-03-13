package app.tuxguitar.ui.widget;

import app.tuxguitar.ui.event.UILinkListener;

public interface UILinkLabel extends UIWrapLabel {

	void addLinkListener(UILinkListener listener);

	void removeLinkListener(UILinkListener listener);
}
