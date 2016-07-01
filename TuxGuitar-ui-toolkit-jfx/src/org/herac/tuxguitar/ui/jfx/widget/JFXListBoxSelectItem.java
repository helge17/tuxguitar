package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.control.ListCell;

import org.herac.tuxguitar.ui.widget.UISelectItem;

public class JFXListBoxSelectItem<T> {
	
	private UISelectItem<T> item;
	private ListCell<JFXListBoxSelectItem<T>> cell;
	
	public JFXListBoxSelectItem(UISelectItem<T> item) {
		this.item = item;
	}
	
	public UISelectItem<T> getItem() {
		return item;
	}
	
	public ListCell<JFXListBoxSelectItem<T>> getCell() {
		return cell;
	}

	public void setCell(ListCell<JFXListBoxSelectItem<T>> cell) {
		this.cell = cell;
	}

	public String toString() {
		return this.item.getText();
	}
}
