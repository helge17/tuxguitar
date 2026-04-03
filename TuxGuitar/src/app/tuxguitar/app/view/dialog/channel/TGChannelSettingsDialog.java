package app.tuxguitar.app.view.dialog.channel;

import app.tuxguitar.ui.widget.UIWindow;

public interface TGChannelSettingsDialog {

	boolean isOpen();

	void open(UIWindow parent);

	void close();
}
