package org.herac.tuxguitar.ui.swt.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.swt.event.SWTSelectionListenerManager;
import org.herac.tuxguitar.ui.widget.UIListBoxSelect;
import org.herac.tuxguitar.ui.widget.UISelectItem;

public class SWTListBoxSelect<T> extends SWTControl<org.eclipse.swt.widgets.List> implements UIListBoxSelect<T> {
	
	private List<UISelectItem<T>> uiItems;
	private SWTSelectionListenerManager selectionListener;
	
	public SWTListBoxSelect(SWTContainer<? extends Composite> parent) {
		super(new org.eclipse.swt.widgets.List(parent.getControl(), SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL), parent);
		
		this.selectionListener = new SWTSelectionListenerManager(this);
		this.uiItems = new ArrayList<UISelectItem<T>>();
	}

	public T getSelectedValue() {
		UISelectItem<T> selectedItem = this.getSelectedItem();
		return (selectedItem != null ? selectedItem.getValue() : null);
	}
	
	public void setSelectedValue(T value) {
		this.setSelectedItem(new UISelectItem<T>(null, value));
	}
	
	public UISelectItem<T> getSelectedItem() {
		int index = this.getControl().getSelectionIndex();
		return (index >= 0 && index < this.uiItems.size() ? this.uiItems.get(index) : null);
	}

	public void setSelectedItem(UISelectItem<T> item) {
		int index = (item != null ? this.uiItems.indexOf(item) : -1);
		this.getControl().select(index);
	}

	public void addItem(UISelectItem<T> item) {
		this.uiItems.add(item);
		this.getControl().add(item.getText());
	}
	
	public void removeItem(UISelectItem<T> item) {
		int index = (item != null ? this.uiItems.indexOf(item) : -1);
		if( index >= 0 && index < this.uiItems.size() ) {
			this.getControl().remove(index);
			this.uiItems.remove(item);
		}
	}
	
	public void removeItems() {
		List<UISelectItem<T>> uiItems = new ArrayList<UISelectItem<T>>(this.uiItems);
		for(UISelectItem<T> uiItem : uiItems) {
			this.removeItem(uiItem);
		}
	}
	
	public int getItemCount() {
		return this.uiItems.size();
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
}