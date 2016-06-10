package org.herac.tuxguitar.ui.widget;

import org.herac.tuxguitar.ui.event.UICloseListener;
import org.herac.tuxguitar.ui.menu.UIMenuBar;
import org.herac.tuxguitar.ui.resource.UIImage;

public interface UIWindow extends UILayoutContainer {
	
	String getText();

	void setText(String text);
	
	UIImage getImage();
	
	void setImage(UIImage image);
	
	void open();
	
	void close();
	
	void join();
	
	void minimize();
	
	void maximize();
	
	boolean isMaximized();
	
	void moveToTop();
	
	UIMenuBar getMenuBar();
	
	void setMenuBar(UIMenuBar menu);
	
	void addCloseListener(UICloseListener listener);
	
	void removeCloseListener(UICloseListener listener);
}
