package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class JFXListBoxSelectCellFactory<T> implements Callback<ListView<JFXListBoxSelectItem<T>>, ListCell<JFXListBoxSelectItem<T>>> {
	
	public JFXListBoxSelectCellFactory() {
		super();
	}
	
	@Override
	public ListCell<JFXListBoxSelectItem<T>> call(ListView<JFXListBoxSelectItem<T>> param) {
		return new ListCell<JFXListBoxSelectItem<T>>() {
			public void updateItem(JFXListBoxSelectItem<T> item, boolean empty) {
				super.updateItem(item, empty);
				
				this.setText(empty ? null : (item != null ? item.toString() : "null"));
				this.setGraphic(null);
				this.setManaged(false);
				this.applyCss();
				
				if( item != null ) {
					item.setCell(this);
				}
			}
		};
	}
}