package org.herac.tuxguitar.ui.jfx.widget;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

import org.herac.tuxguitar.ui.widget.UITableItem;

public class JFXTableCellValueFactory<T> implements Callback<CellDataFeatures<UITableItem<T>, JFXTableCellValue<T>>, ObservableValue<JFXTableCellValue<T>>> {
	
	private List<JFXTableCellValue<T>> cells;
	
	public JFXTableCellValueFactory() {
		this.cells = new ArrayList<JFXTableCellValue<T>>();
	}
	
	public ObservableValue<JFXTableCellValue<T>> call(CellDataFeatures<UITableItem<T>, JFXTableCellValue<T>> cdf) {
		int index = cdf.getTableView().getColumns().indexOf(cdf.getTableColumn());
		if( index >= 0 ) {
			return new ReadOnlyObjectWrapper<JFXTableCellValue<T>>(this.findOrCreateCell(cdf.getValue(), index));
		}
        return new ReadOnlyObjectWrapper<JFXTableCellValue<T>>(null);
    }
	
	public JFXTableCellValue<T> findOrCreateCell(UITableItem<T> item, int column) {
		JFXTableCellValue<T> cell = new JFXTableCellValue<T>(item, column);
		if( this.cells.contains(cell) ) {
			return this.cells.get(this.cells.indexOf(cell));
		}
		this.cells.add(cell);
		
		return this.findOrCreateCell(item, column);
	}
	
	public void removeCells(UITableItem<T> item) {
		List<JFXTableCellValue<T>> cells = new ArrayList<JFXTableCellValue<T>>(this.cells);
		for(JFXTableCellValue<T> cell : cells) {
			if( cell.getControl().equals(item) ) {
				this.cells.remove(cell);
			}
		}
	}
	
	public void removeCells() {
		this.cells.clear();
	}
}
