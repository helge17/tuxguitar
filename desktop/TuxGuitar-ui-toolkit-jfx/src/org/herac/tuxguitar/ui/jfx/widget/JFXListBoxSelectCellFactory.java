package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class JFXListBoxSelectCellFactory<T> implements Callback<ListView<JFXListBoxSelectItem<T>>, ListCell<JFXListBoxSelectItem<T>>> {
	
	private JFXListBoxSelect<T> control;
	
	public JFXListBoxSelectCellFactory(JFXListBoxSelect<T> control) {
		this.control = control;
	}
	
	@Override
	public ListCell<JFXListBoxSelectItem<T>> call(ListView<JFXListBoxSelectItem<T>> listView) {
		return new ListCell<JFXListBoxSelectItem<T>>() {
			public void updateItem(JFXListBoxSelectItem<T> item, boolean empty) {
				super.updateItem(item, empty);
				
				this.setManaged(false);
				this.applyCss();
				this.setGraphic(null);
				this.setText(empty ? null : (item != null ? item.toString() : "null"));
				
				if( item != null ) {
					this.setPadding(getControl().computeCellPadding());
					this.setPrefWidth(getControl().computeCellWidth(item));
					this.setPrefHeight(getControl().computeCellHeight(item));
				}
			}
		};
	}

	public JFXListBoxSelect<T> getControl() {
		return control;
	}
}