package org.herac.tuxguitar.ui.qt.widget;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.ui.event.UICheckTableSelectionListener;
import org.herac.tuxguitar.ui.event.UIMouseDoubleClickListener;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.qt.event.QTCheckTableSelectionListenerManager;
import org.herac.tuxguitar.ui.qt.event.QTSelectionListenerManager;
import org.herac.tuxguitar.ui.qt.event.QTTableDoubleClickListenerManager;
import org.herac.tuxguitar.ui.qt.resource.QTImage;
import org.herac.tuxguitar.ui.widget.UICheckTable;
import org.herac.tuxguitar.ui.widget.UITableItem;

import com.trolltech.qt.core.Qt.CheckState;
import com.trolltech.qt.core.Qt.FocusPolicy;
import com.trolltech.qt.core.Qt.ItemFlag;
import com.trolltech.qt.gui.QAbstractItemView.SelectionBehavior;
import com.trolltech.qt.gui.QAbstractItemView.SelectionMode;
import com.trolltech.qt.gui.QHeaderView.ResizeMode;
import com.trolltech.qt.gui.QTableWidget;
import com.trolltech.qt.gui.QTableWidgetItem;

public class QTTable<T> extends QTWidget<QTableWidget> implements UICheckTable<T> {
	
	private boolean checkable;
	private List<UITableItem<T>> items;
	private List<QTColumnName> columnNames;
	private QTSelectionListenerManager selectionListener;
	private QTCheckTableSelectionListenerManager<T> checkSelectionListener;
	private QTTableDoubleClickListenerManager doubleClickListenerManager;
	
	public QTTable(QTContainer parent, boolean headerVisible, boolean checkable) {
		super(new QTableWidget(parent.getContainerControl()), parent);
		
		this.checkable = checkable;
		this.items = new ArrayList<UITableItem<T>>();
		this.columnNames = new ArrayList<QTColumnName>();
		this.selectionListener = new QTSelectionListenerManager(this);
		this.checkSelectionListener = new QTCheckTableSelectionListenerManager<T>(this);
		this.doubleClickListenerManager = new QTTableDoubleClickListenerManager(this);
		
		this.getControl().setShowGrid(false);
		this.getControl().setFocusPolicy(FocusPolicy.NoFocus);
		this.getControl().setSelectionMode(SelectionMode.SingleSelection);
		this.getControl().setSelectionBehavior(SelectionBehavior.SelectRows);
		this.getControl().horizontalHeader().setVisible(headerVisible);
		this.getControl().verticalHeader().setVisible(false);
	}
	
	public void updateColumnNames() {
		int columnCount = this.getColumns();
		while(this.columnNames.size() < columnCount) {
			this.columnNames.add(new QTColumnName());
		}
		while(this.columnNames.size() > columnCount) {
			this.columnNames.remove(this.columnNames.size() - 1);
		}
	}
	
	public void setColumns(int count) {
		if( count >= 0 ) {
			this.getControl().setColumnCount(count);
			this.updateColumnNames();
			
			for(int i = 0; i < count; i ++) {
				this.getControl().horizontalHeader().setResizeMode(i, (i == (count - 1) ? ResizeMode.Stretch : ResizeMode.Interactive));
			}
		}
	}
	
	public int getColumns() {
		return this.getControl().columnCount();
	}
	
	public void setColumnName(int column, String name) {
		if( column >= 0 && column < this.columnNames.size() ) {
			this.columnNames.get(column).setValue(name);
			
			List<String> labels = new ArrayList<String>();
			for(QTColumnName columnName : this.columnNames) {
				labels.add(columnName.getValue());
			}
			this.getControl().setHorizontalHeaderLabels(labels);
		}
	}
	
	public String getColumnName(int column) {
		this.updateColumnNames();
		if( column >= 0 && column < this.columnNames.size() ) {
			return this.columnNames.get(column).getValue();
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
	
	public UITableItem<T> getSelectedItem() {
		List<QTableWidgetItem> selectedItems = this.getControl().selectedItems();
		if( selectedItems != null ) {
			for(QTableWidgetItem selectedItem : selectedItems) {
				int row = selectedItem.row();
				if( row >= 0 && row < this.items.size() ) {
					return this.items.get(row);
				}
			}
		}
		return null;
	}

	public void setSelectedItem(UITableItem<T> item) {
		if( this.items.contains(item) ) {
			this.getControl().selectRow(this.items.indexOf(item));
		} else {
			this.getControl().clearSelection();
		}
	}

	public void addItem(UITableItem<T> item) {
		if(!this.items.contains(item)) {
			this.items.add(item);
			this.getControl().setRowCount(this.items.size());
			
			int row = this.items.indexOf(item);
			int columns = this.getColumns();
			for(int column = 0 ; column < columns; column ++) {
				QTableWidgetItem qTableWidgetItem = new QTableWidgetItem();
				qTableWidgetItem.setFlags(ItemFlag.ItemIsEnabled, ItemFlag.ItemIsSelectable, ItemFlag.ItemIsUserCheckable);
				
				String text = item.getText(column);
				if( text != null ) {
					qTableWidgetItem.setText(text);
				}
				
				if( item.getImage() != null && !item.getImage().isDisposed()) {
					qTableWidgetItem.setIcon(((QTImage) item.getImage()).createIcon());
				}
				
				if( this.checkable && column == 0 ) {
					qTableWidgetItem.setCheckState(CheckState.Unchecked);
				}
				
				this.getControl().setItem(row, column, qTableWidgetItem);
			}
		}
	}
	
	public void removeItem(UITableItem<T> item) {
		int row = this.items.indexOf(item);
		if( row >= 0 ) {
			this.items.remove(row);
			this.getControl().removeRow(row);
			this.getControl().setRowCount(this.items.size());
		}
	}
	
	public void removeItems() {
		this.items.clear();
		this.getControl().clearContents();
		this.getControl().setRowCount(this.items.size());
	}
	
	public UITableItem<T> getItem(int index) {
		if( index >= 0 && index < this.getItemCount()) {
			return this.items.get(index);
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
		return this.items.size();
	}
	
	public boolean isCheckedValue(T value) {
		return this.isCheckedItem(new UITableItem<T>(value));
	}

	public boolean isCheckedItem(UITableItem<T> item) {
		int row = this.items.indexOf(item);
		if( row >= 0 ) {
			QTableWidgetItem qTableWidgetItem = this.getControl().item(row, 0);
			
			return (CheckState.Checked.equals(qTableWidgetItem.checkState()));
		}
		return false;
	}

	public void setCheckedValue(T value, boolean checked) {
		this.setCheckedItem(new UITableItem<T>(value), checked);
	}

	public void setCheckedItem(UITableItem<T> item, boolean checked) {
		int row = this.items.indexOf(item);
		if( row >= 0 ) {
			QTableWidgetItem qTableWidgetItem = this.getControl().item(row, 0);
			qTableWidgetItem.setCheckState(checked ? CheckState.Checked : CheckState.Unchecked);
		}
	}
	
	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().itemSelectionChanged.connect(this.selectionListener, QTSelectionListenerManager.SIGNAL_METHOD);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().itemSelectionChanged.disconnect();
		}
	}
	
	public void addCheckSelectionListener(UICheckTableSelectionListener<T> listener) {
		if( this.checkSelectionListener.isEmpty() ) {
			this.getControl().cellChanged.connect(this.checkSelectionListener, QTCheckTableSelectionListenerManager.SIGNAL_METHOD);
		}
		this.checkSelectionListener.addListener(listener);
	}

	public void removeCheckSelectionListener(UICheckTableSelectionListener<T> listener) {
		this.checkSelectionListener.removeListener(listener);
		if( this.checkSelectionListener.isEmpty() ) {
			this.getControl().cellChanged.disconnect();
		}
	}
	
	public void addMouseDoubleClickListener(UIMouseDoubleClickListener listener) {
		if( this.doubleClickListenerManager.isEmpty() ) {
			this.getControl().itemDoubleClicked.connect(this.doubleClickListenerManager, QTTableDoubleClickListenerManager.SIGNAL_METHOD);
		}
		this.doubleClickListenerManager.addListener(listener);
	}

	public void removeMouseDoubleClickListener(UIMouseDoubleClickListener listener) {
		this.doubleClickListenerManager.removeListener(listener);
		if( this.doubleClickListenerManager.isEmpty() ) {
			this.getControl().itemDoubleClicked.disconnect();
		}
	}
	
	private class QTColumnName {
		
		private String value;
		
		public QTColumnName() {
			this.value = new String();
		}
		
		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
