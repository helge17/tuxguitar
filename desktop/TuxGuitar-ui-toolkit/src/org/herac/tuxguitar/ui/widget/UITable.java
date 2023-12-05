package org.herac.tuxguitar.ui.widget;

import org.herac.tuxguitar.ui.event.UISelectionListener;

public interface UITable<T> extends UIControl {
	
	void setColumns(int columns);
	
	int getColumns();

	void setColumnName(int column, String name);
	
	String getColumnName(int column);
	
	T getSelectedValue();
	
	void setSelectedValue(T value);
	
	UITableItem<T> getSelectedItem();
	
	void setSelectedItem(UITableItem<T> item);
	
	void addItem(UITableItem<T> item);
	
	void removeItem(UITableItem<T> item);
	
	void removeItems();
	
	T getItemValue(int index);
	
	UITableItem<T> getItem(int index);
	
	int getItemCount();
	
	void addSelectionListener(UISelectionListener listener);
	
	void removeSelectionListener(UISelectionListener listener);
}
