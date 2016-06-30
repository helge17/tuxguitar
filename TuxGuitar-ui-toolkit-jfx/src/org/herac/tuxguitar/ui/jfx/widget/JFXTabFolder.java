package org.herac.tuxguitar.ui.jfx.widget;

import java.util.ArrayList;
import java.util.List;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.event.UICloseEvent;
import org.herac.tuxguitar.ui.event.UICloseListener;
import org.herac.tuxguitar.ui.event.UICloseListenerManager;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.event.UISelectionListenerManager;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UITabFolder;
import org.herac.tuxguitar.ui.widget.UITabItem;

public class JFXTabFolder extends JFXControl<TabPane> implements UITabFolder {
	
	private List<JFXTabItem> tabs;
	private UICloseListenerManager closeListener;
	private UISelectionListenerManager selectionListener;
	
	public JFXTabFolder(JFXContainer<? extends Region> parent, boolean showClose) {
		super(new TabPane(), parent);
		
		this.tabs = new ArrayList<JFXTabItem>();
		this.closeListener = new UICloseListenerManager();
		this.selectionListener = new UISelectionListenerManager();
		
		this.getControl().setTabClosingPolicy(showClose ? TabClosingPolicy.ALL_TABS : TabClosingPolicy.UNAVAILABLE);
	}
	
	public void dispose() {
		List<JFXTabItem> items = new ArrayList<JFXTabItem>(this.tabs);
		for(JFXTabItem item : items) {
			if(!item.isDisposed()) {
				item.dispose();
			}
		}
		super.dispose();
	}
	
	public void addTab(JFXTabItem tabItem) {
		this.setOnTabCloseRequest(tabItem);
		this.setOnSelectionChanged(tabItem);
		this.getControl().getTabs().add(tabItem.getItem());
		this.tabs.add(tabItem);
	}
	
	public void removeTab(JFXTabItem tabItem) {
		if( this.tabs.contains(tabItem)) { 
			this.tabs.remove(tabItem);
			this.getControl().getTabs().remove(tabItem.getItem());
		}
	}

	public UITabItem createTab() {
		return new JFXTabItem(this);
	}

	public List<UITabItem> getTabs() {
		return new ArrayList<UITabItem>(this.tabs);
	}
	
	public UITabItem findTab(Tab item) {
		if( item != null ) {
			for(JFXTabItem tab : this.tabs) {
				if( item.equals(tab.getItem())) {
					return tab;
				}
			}
		}
		return null;
	}

	public UITabItem getSelectedTab() {
		return this.findTab(this.getControl().getSelectionModel().getSelectedItem());
	}

	public void setSelectedTab(UITabItem tab) {
		this.getControl().getSelectionModel().select(tab != null ? ((JFXTabItem )tab).getItem() : null);
		this.onTabSelected();
	}

	public int getSelectedIndex() {
		return this.getControl().getSelectionModel().getSelectedIndex();
	}

	public void setSelectedIndex(int index) {
		this.getControl().getSelectionModel().select(index);
		this.onTabSelected();
	}

	public void addSelectionListener(UISelectionListener listener) {
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
	}

	public void addTabCloseListener(UICloseListener listener) {
		this.closeListener.addListener(listener);
	}

	public void removeTabCloseListener(UICloseListener listener) {
		this.closeListener.removeListener(listener);
	}
	
	public void setOnTabCloseRequest(final JFXTabItem tabItem) {
		tabItem.getItem().setOnCloseRequest(new EventHandler<Event>() {
			public void handle(Event event) {
				event.consume();
				
				JFXTabFolder.this.closeListener.onClose(new UICloseEvent(tabItem));
			}
		});
	}
	
	public void setOnSelectionChanged(final JFXTabItem tabItem) {
		tabItem.getItem().setOnSelectionChanged(new EventHandler<Event>() {
			public void handle(Event event) {
				JFXTabFolder.this.onTabSelected();
				JFXTabFolder.this.selectionListener.onSelect(new UISelectionEvent(tabItem));
			}
		});
	}
	
	public void onTabSelected() {
		UITabItem selectedTab = this.getSelectedTab();
		if( selectedTab != null ) {
			((JFXTabItem) selectedTab).onSelect();
		}
	}
	
	public void computePackedSize() {
		for(UIControl uiControl : this.getTabs()) {
			uiControl.computePackedSize();
		}
		
		UISize packedSize = this.getPackedSize();
		if( packedSize.getWidth() == 0f && packedSize.getHeight() == 0f ) {
			super.computePackedSize();
		}
	}
}
