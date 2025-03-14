package app.tuxguitar.ui.qt.widget;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.ui.event.UICloseListener;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.qt.event.QTTabFolderCloseListenerManager;
import app.tuxguitar.ui.qt.event.QTTabFolderSelectionListenerManager;
import app.tuxguitar.ui.widget.UITabFolder;
import app.tuxguitar.ui.widget.UITabItem;
import org.qtjambi.qt.widgets.QTabWidget;

public class QTTabFolder extends QTWidget<QTabWidget> implements UITabFolder {

	private List<QTTabItem> tabs;
	private QTTabFolderCloseListenerManager closeListener;
	private QTTabFolderSelectionListenerManager selectionListener;

	public QTTabFolder(QTContainer parent, boolean showClose) {
		super(new QTabWidget(parent.getContainerControl()), parent);

		this.tabs = new ArrayList<QTTabItem>();
		this.closeListener = new QTTabFolderCloseListenerManager(this);
		this.selectionListener = new QTTabFolderSelectionListenerManager(this);

		this.getControl().setTabsClosable(showClose);
	}

	public void dispose() {
		List<QTTabItem> items = new ArrayList<QTTabItem>(this.tabs);
		for(QTTabItem item : items) {
			if(!item.isDisposed()) {
				item.dispose();
			}
		}
		super.dispose();
	}

	public void addTab(QTTabItem tabItem) {
		this.getControl().addTab(tabItem.getControl(), new String());
		this.tabs.add(tabItem);
	}

	public void removeTab(QTTabItem tabItem) {
		int index = this.getTabIndex(tabItem);
		if( index >= 0 ) {
			this.tabs.remove(index);
			this.getControl().removeTab(index);
		}
	}

	public int getTabIndex(UITabItem tab) {
		return this.tabs.indexOf(tab);
	}

	public UITabItem createTab() {
		return new QTTabItem(this);
	}

	public List<UITabItem> getTabs() {
		return new ArrayList<UITabItem>(this.tabs);
	}

	public UITabItem getTab(int index) {
		return (index >= 0 && index < this.tabs.size() ? this.tabs.get(index) : null);
	}

	public UITabItem getSelectedTab() {
		return this.getTab(this.getSelectedIndex());
	}

	public void setSelectedTab(UITabItem tab) {
		int index = this.getTabIndex(tab);
		if( index >= 0 ) {
			this.setSelectedIndex(index);
		}
	}

	public int getSelectedIndex() {
		return this.getControl().currentIndex();
	}

	public void setSelectedIndex(int index) {
		this.getControl().setCurrentIndex(index);
	}

	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().currentChanged.connect(this.selectionListener, QTTabFolderSelectionListenerManager.SIGNAL_METHOD);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().currentChanged.disconnect();
		}
	}

	public void addTabCloseListener(UICloseListener listener) {
		if( this.closeListener.isEmpty() ) {
			this.getControl().tabCloseRequested.connect(this.closeListener, QTTabFolderCloseListenerManager.SIGNAL_METHOD);
		}
		this.closeListener.addListener(listener);
	}

	public void removeTabCloseListener(UICloseListener listener) {
		if( this.closeListener.isEmpty() ) {
			this.getControl().tabCloseRequested.disconnect();
		}
		this.closeListener.addListener(listener);
	}
}
