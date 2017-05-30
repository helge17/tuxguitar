package org.herac.tuxguitar.app.view.dialog.channel;

import org.herac.tuxguitar.ui.widget.UIWindow;

public interface TGChannelSettingsDialog {
	
	boolean isOpen();
	
	void open(UIWindow parent);
	
	void close();
}
