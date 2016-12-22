package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.swt.event.SWTSelectionListenerManager;
import org.herac.tuxguitar.ui.swt.resource.SWTImage;
import org.herac.tuxguitar.ui.widget.UITable;
import org.herac.tuxguitar.ui.widget.UITableItem;

public class SWTTable<T> extends SWTControl<Table> implements UITable<T> {
	
	public static final int TABLE_STYLE = (SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
	public static final int TABLE_COLUMN_MARGIN = 4;
	
	private SWTSelectionListenerManager selectionListener;
	private SWTTableUpdateManager updateManager;
	
	public SWTTable(SWTContainer<? extends Composite> parent, boolean headerVisible, int style) {
		super(new Table(parent.getControl(), style), parent);
		
		this.selectionListener = new SWTSelectionListenerManager(this);
		this.updateManager = new SWTTableUpdateManager(this);
		
		this.getControl().setHeaderVisible(false);
		this.getControl().addListener(SWT.Paint, this.updateManager);
	}

	public SWTTable(SWTContainer<? extends Composite> parent, boolean headerVisible) {
		this(parent, headerVisible, TABLE_STYLE);
	}
	
	public void setColumns(int columns) {
		int count = this.getControl().getColumnCount();
		for(int i = count; i < columns; i++) {
			new TableColumn(this.getControl(), SWT.NONE);
		}
		for(int i = columns; i < count; i++) {
			this.getControl().getColumn(i).dispose();
		}
	}

	public int getColumns() {
		return this.getControl().getColumnCount();
	}

	public void setColumnName(int column, String name) {
		if( column >= 0 && column < this.getColumns() ) {
			this.getControl().setHeaderVisible(true);
			
			TableColumn tableColumn = this.getControl().getColumn(column);
			tableColumn.setText(name);
			
			if( this.isVisible() ) {
				this.updateManager.setUpdateRequired();
			}
		}
	}

	public String getColumnName(int column) {
		if( column >= 0 && column < this.getColumns() ) {
			return this.getControl().getColumn(column).getText();
		}
		return null;
	}
	
	public T getSelectedValue() {
		UITableItem<T> selectedItem = this.getSelectedItem();
		return (selectedItem != null ? selectedItem.getValue() : null);
	}
	
	public void setSelectedValue(T value) {
		this.setSelectedItem(new UITableItem<T>(value));
	}
	
	@SuppressWarnings("unchecked")
	public UITableItem<T> getSelectedItem() {
		TableItem[] tableItems = this.getControl().getSelection();
		return (tableItems != null && tableItems.length > 0 ? (UITableItem<T>) tableItems[0].getData() : null);
	}

	public void setSelectedItem(UITableItem<T> item) {
		TableItem tableItem = this.getTableItem(item);
		if( tableItem != null ) {
			this.getControl().setSelection(tableItem);
		} else {
			this.getControl().deselectAll();
		}
	}

	public void addItem(UITableItem<T> item) {
		TableItem tableItem = new TableItem(this.getControl(), SWT.NULL);
		tableItem.setData(item);
		if( item.getImage() != null && !item.getImage().isDisposed()) {
			tableItem.setImage(((SWTImage)item.getImage()).getHandle());
		}
		
		int columns = this.getColumns();
		for(int i = 0 ; i < columns; i ++) {
			String text = item.getText(i);
			if( text != null ) {
				tableItem.setText(i, text);
			}
		}
		
		// fix column size
		if( this.isVisible() ) {
			this.updateManager.setUpdateRequired();
		}
	}
	
	public TableItem getTableItem(UITableItem<T> item) {
		if( item != null ) {
			TableItem[] tableItems = this.getControl().getItems();
			for(TableItem tableItem : tableItems) {
				if( tableItem.getData() != null && tableItem.getData().equals(item) ) {
					return tableItem;
				}
			}
		}
		return null;
	}

	public void removeItem(UITableItem<T> item) {
		TableItem tableItem = this.getTableItem(item);
		if( tableItem != null ) {
			tableItem.dispose();
		}
	}
	
	public void removeItems() {
		this.getControl().removeAll();
	}
	
	@SuppressWarnings("unchecked")
	public UITableItem<T> getItem(int index) {
		if( index >= 0 && index < this.getItemCount()) {
			TableItem tableItem = this.getControl().getItem(index);
			if( tableItem != null ) {
				return (UITableItem<T>) tableItem.getData();
			}
		}
		return null;
	}
	
	public T getItemValue(int index) {
		UITableItem<T> item = this.getItem(index);
		if( item != null ) {
			return item.getValue();
		}
		return null;
	}
	
	public int getItemCount() {
		return this.getControl().getItemCount();
	}
	
	public void computePackedSize() {
		this.adjustColumnsWidth();
		
		super.computePackedSize();
	}
	
	public void adjustColumnsWidth() {
		TableColumn[] tableColumns = this.getControl().getColumns();
		for(TableColumn tableColumn : tableColumns) {
			this.adjustColumnWidth(tableColumn);
		}
	}
	
	public void adjustColumnWidth(TableColumn column) {
		int minimumWidth = 0;
		
		String headerText = column.getText();
		if( headerText != null && headerText.length() > 0 ) {
			GC gc = new GC(this.getControl());
			minimumWidth = (gc.stringExtent(headerText).x + (TABLE_COLUMN_MARGIN * 2));
			gc.dispose();
		}
		
		column.pack();
		if( column.getWidth() < minimumWidth ) {
			column.setWidth(minimumWidth);
		}
		
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
	
	private class SWTTableUpdateManager implements Listener {
		
		private SWTTable<?> table;
		private boolean updateRequired;
		
		public SWTTableUpdateManager(SWTTable<?> table) {
			this.table = table;
		}
		
		public void handleEvent (Event e) {
			if( this.updateRequired ) {
				this.updateRequired = false;
				this.table.adjustColumnsWidth();
			}
		}

		public void setUpdateRequired() {
			this.updateRequired = true;
		}
	}
}
