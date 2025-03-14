package app.tuxguitar.ui.widget;

import app.tuxguitar.ui.event.UICloseListener;
import app.tuxguitar.ui.resource.UIImage;

public interface UIWindow extends UILayoutContainer {

	String getText();

	void setText(String text);

	UIImage getImage();

	void setImage(UIImage image);

	void open();

	void close();

	void minimize();

	void maximize();

	boolean isMaximized();

	void moveToTop();

	void addCloseListener(UICloseListener listener);

	void removeCloseListener(UICloseListener listener);
}
