package org.herac.tuxguitar.ui.swt.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.ui.event.UICloseEvent;
import org.herac.tuxguitar.ui.event.UICloseListener;
import org.herac.tuxguitar.ui.event.UICloseListenerManager;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.swt.event.SWTSelectionListenerManager;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UITabFolder;
import org.herac.tuxguitar.ui.widget.UITabItem;

public class SWTTabFolder extends SWTControl<CTabFolder> implements UITabFolder {
	
	private static final int TAB_HEIGHT = 28;
	
	private boolean showClose;
	private List<SWTTabItem> tabs;
	private UICloseListenerManager closeListener;
	private SWTSelectionListenerManager selectionListener;
	
	public SWTTabFolder(SWTContainer<? extends Composite> container, boolean showClose) {
		super(new CTabFolder(container.getControl(), SWT.TOP), container);
		
		this.tabs = new ArrayList<SWTTabItem>();
		this.closeListener = new UICloseListenerManager();
		this.selectionListener = new SWTSelectionListenerManager(this);
		this.showClose = showClose;
		this.getControl().setTabHeight(TAB_HEIGHT);
		this.getControl().addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void close(CTabFolderEvent event) {
				onTabClose(event);
			}
		});
		this.getControl().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				onTabSelected();
			}
		});
	}
	
	public void dispose(SWTTabItem item) {
		if( this.tabs.contains(item)) { 
			this.tabs.remove(item);
		}
		item.disposeControl();
	}

	public UITabItem createTab() {
		SWTTabItem tabItem = new SWTTabItem(new CTabItem(this.getControl(), (this.showClose ? SWT.CLOSE : 0)), this);
		this.tabs.add(tabItem);
		return tabItem;
	}

	public List<UITabItem> getTabs() {
		return new ArrayList<UITabItem>(this.tabs);
	}
	
	public UITabItem findTab(CTabItem cTabItem) {
		if( cTabItem != null ) {
			for(SWTTabItem tab : this.tabs) {
				if( cTabItem.equals(tab.getItem())) {
					return tab;
				}
			}
		}
		return null;
	}

	public UITabItem getSelectedTab() {
		return this.findTab(this.getControl().getSelection());
	}

	public void setSelectedTab(UITabItem tab) {
		this.getControl().setSelection(((SWTTabItem )tab).getItem());
		this.onTabSelected();
	}

	public int getSelectedIndex() {
		return this.getControl().getSelectionIndex();
	}

	public void setSelectedIndex(int index) {
		this.getControl().setSelection(index);
		this.onTabSelected();
	}

	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().addSelectionListener(this.selectionListener);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().removeSelectionListener(this.selectionListener);
		}
	}

	public void addTabCloseListener(UICloseListener listener) {
		this.closeListener.addListener(listener);
	}

	public void removeTabCloseListener(UICloseListener listener) {
		this.closeListener.removeListener(listener);
	}
	
	public void onTabClose(CTabFolderEvent event) {
		event.doit = false;
		
		UITabItem uiTabItem = this.findTab((CTabItem)event.item);
		UICloseEvent uiCloseEvent = new UICloseEvent(uiTabItem);
		
		this.closeListener.onClose(uiCloseEvent);
	}
	
	public void onTabSelected() {
		UITabItem selectedTab = this.getSelectedTab();
		if( selectedTab != null ) {
			((SWTTabItem) selectedTab).onSelect();
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
