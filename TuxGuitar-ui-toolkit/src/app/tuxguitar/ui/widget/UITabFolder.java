package app.tuxguitar.ui.widget;

import java.util.List;

import app.tuxguitar.ui.event.UICloseListener;
import app.tuxguitar.ui.event.UISelectionListener;

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
