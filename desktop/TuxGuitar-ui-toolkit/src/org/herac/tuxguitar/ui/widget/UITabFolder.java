package org.herac.tuxguitar.ui.widget;

import java.util.List;

import org.herac.tuxguitar.ui.event.UICloseListener;
import org.herac.tuxguitar.ui.event.UISelectionListener;

public interface UITabFolder extends UIControl {
	
	UITabItem createTab();
	
	List<UITabItem> getTabs();
	
	UITabItem getSelectedTab();
	
	void setSelectedTab(UITabItem tab);
	
	int getSelectedIndex();
	
	void setSelectedIndex(int index);
	
	void addSelectionListener(UISelectionListener listener);
	
	void removeSelectionListener(UISelectionListener listener);
	
	void addTabCloseListener(UICloseListener listener);
	
	void removeTabCloseListener(UICloseListener listener);
}
