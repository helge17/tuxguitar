package org.herac.tuxguitar.ui.jfx.widget;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.event.UICheckTableSelectionListener;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.jfx.event.JFXCheckTableSelectionListenerManager;
import org.herac.tuxguitar.ui.jfx.event.JFXSelectionListenerChangeManager;
import org.herac.tuxguitar.ui.jfx.event.JFXSelectionListenerChangeManagerAsync;
import org.herac.tuxguitar.ui.widget.UICheckTable;
import org.herac.tuxguitar.ui.widget.UITableItem;

public class JFXTable<T> extends JFXControl<TableView<UITableItem<T>>> implements UICheckTable<T> {
	
	private static final Integer VERTICAL_SCROLL_SIZE = 18;
	
	private JFXTableCellFactory<T> cellFactory;
	private JFXTableCellValueFactory<T> cellValueFactory;
	private JFXSelectionListenerChangeManager<UITableItem<T>> selectionListener;
	private JFXCheckTableSelectionListenerManager<T> checkSelectionListener;
	
	public JFXTable(JFXContainer<? extends Region> parent, boolean headerVisible, boolean checkable) {
		super(new TableView<UITableItem<T>>(), parent);
		
		this.selectionListener = new JFXSelectionListenerChangeManagerAsync<UITableItem<T>>(this);
		this.checkSelectionListener = new JFXCheckTableSelectionListenerManager<T>(this);
		this.cellFactory = new JFXTableCellFactory<T>(this, checkable);
		this.cellValueFactory = new JFXTableCellValueFactory<T>();
		
		this.getControl().setEditable(true);
		this.getControl().getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
		this.getControl().widthProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				JFXTable.this.fillAvailableWidth();
			}
		});
	}
	
	public void setColumns(int count) {
		if( count >= 0 ) {
			List<TableColumn<UITableItem<T>, ?>> columns = this.getControl().getColumns();
			
			while(columns.size() < count) {
				TableColumn<UITableItem<T>, JFXTableCellValue<T>> tableColumn = new TableColumn<UITableItem<T>, JFXTableCellValue<T>>();
				tableColumn.setCellFactory(this.cellFactory);
				tableColumn.setCellValueFactory(this.cellValueFactory);
				
				columns.add(tableColumn);
			}
			while(columns.size() > count) {
				columns.remove(columns.size() - 1);
			}
		}
	}

	public int getColumns() {
		return this.getControl().getColumns().size();
	}

	public void setColumnName(int column, String name) {
		if( column >= 0 && column < this.getColumns() ) {
			this.getControl().getColumns().get(column).setText(name);
		}
	}

	public String getColumnName(int column) {
		if( column >= 0 && column < this.getColumns() ) {
			return this.getControl().getColumns().get(column).getText();
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
		return this.getControl().getSelectionModel().getSelectedItem();
	}

	public void setSelectedItem(UITableItem<T> item) {
		if( this.getControl().getItems().contains(item) ) {
			this.getControl().getSelectionModel().select(item);
		} else {
			this.getControl().getSelectionModel().clearSelection();
		}
	}

	public void addItem(UITableItem<T> item) {
		this.getControl().getItems().add(item);
	}
	
	public void removeItem(UITableItem<T> item) {
		if( this.getControl().getItems().contains(item) ) {
			this.getControl().getItems().remove(item);
			this.getControl().getSelectionModel().clearSelection();
			this.cellValueFactory.removeCells(item);
		}
	}
	
	public void removeItems() {
		this.getControl().getItems().clear();
		this.getControl().getSelectionModel().clearSelection();
		this.cellValueFactory.removeCells();
	}
	
	public UITableItem<T> getItem(int index) {
		if( index >= 0 && index < this.getItemCount()) {
			return this.getControl().getItems().get(index);
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
		return this.getControl().getItems().size();
	}
	
	@SuppressWarnings("unchecked")
	public JFXTableCellValue<T> findFirstCell(UITableItem<T> item) {
		if( this.getControl().getColumns().size() > 0 ) {
			return (JFXTableCellValue<T>) this.getControl().getColumns().get(0).getCellData(item);
		}
		return null;
	}
	
	public boolean isCheckedValue(T value) {
		return this.isCheckedItem(new UITableItem<T>(value));
	}

	public boolean isCheckedItem(UITableItem<T> item) {
		JFXTableCellValue<T> cellValue = this.findFirstCell(item);
		if( cellValue != null ) {
			return cellValue.isChecked();
		}
		return false;
	}

	public void setCheckedValue(T value, boolean checked) {
		this.setCheckedItem(new UITableItem<T>(value), checked);
	}

	public void setCheckedItem(UITableItem<T> item, boolean checked) {
		JFXTableCellValue<T> cellValue = this.findFirstCell(item);
		if( cellValue != null ) {
			cellValue.setChecked(checked);
		}
	}
	
	public void fillAvailableWidth() {
		List<TableColumn<UITableItem<T>, ?>> columns = this.getControl().getColumns();
		if(!columns.isEmpty()) {
			Insets padding = getControl().getPadding();
			
			double availableWidth = (this.getControl().getWidth() - (padding.getLeft() + padding.getRight() + VERTICAL_SCROLL_SIZE));
			for(TableColumn<UITableItem<T>, ?> column : columns) {
				availableWidth -= column.getWidth();
			}
			if( availableWidth > 0 ) {
				TableColumn<UITableItem<T>, ?> lastColumn = columns.get(columns.size() - 1);
				
				lastColumn.prefWidthProperty().set(lastColumn.getWidth() + availableWidth);
			}
		}
	}
	
	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().getSelectionModel().selectedItemProperty().addListener(this.selectionListener);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().getSelectionModel().selectedItemProperty().removeListener(this.selectionListener);
		}
	}
	
	public void addCheckSelectionListener(UICheckTableSelectionListener<T> listener) {
		this.checkSelectionListener.addListener(listener);
	}

	public void removeCheckSelectionListener(UICheckTableSelectionListener<T> listener) {
		this.checkSelectionListener.removeListener(listener);
	}
	
	public void onCellChecked(JFXTableCellValue<T> cell) {
		this.setSelectedItem(cell.getControl());
		this.checkSelectionListener.fireEvent(cell.getControl());
	}
}
