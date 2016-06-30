package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.jfx.event.JFXSelectionListenerChangeManager;
import org.herac.tuxguitar.ui.widget.UIListBoxSelect;
import org.herac.tuxguitar.ui.widget.UISelectItem;

public class JFXListBoxSelect<T> extends JFXControl<ListView<JFXListBoxSelectItem<T>>> implements UIListBoxSelect<T> {
	
	private JFXSelectionListenerChangeManager<JFXListBoxSelectItem<T>> selectionListener;
	
	public JFXListBoxSelect(JFXContainer<? extends Region> parent) {
		super(new ListView<JFXListBoxSelectItem<T>>(), parent);
		
		this.selectionListener = new JFXSelectionListenerChangeManager<JFXListBoxSelectItem<T>>(this);
	}

	public JFXListBoxSelectItem<T> findItem(UISelectItem<T> item) {
		for(JFXListBoxSelectItem<T> selectItem : this.getControl().getItems()) {
			if( selectItem.getItem().equals(item) ) {
				return selectItem;
			}
		}
		return null;
	}
	
	public T getSelectedValue() {
		UISelectItem<T> selectedItem = this.getSelectedItem();
		return (selectedItem != null ? selectedItem.getValue() : null);
	}
	
	public void setSelectedValue(T value) {
		this.setSelectedItem(new UISelectItem<T>(null, value));
	}
	
	public UISelectItem<T> getSelectedItem() {
		JFXListBoxSelectItem<T> selectedItem = this.getControl().getSelectionModel().getSelectedItem();
		return (selectedItem != null ? selectedItem.getItem() : null);
	}

	public void setSelectedItem(UISelectItem<T> item) {
		JFXListBoxSelectItem<T> selectedItem = this.findItem(item);
		this.getControl().getSelectionModel().select(selectedItem);
	}

	public void addItem(UISelectItem<T> item) {
		this.getControl().getItems().add(new JFXListBoxSelectItem<T>(item));
	}
	
	public void removeItem(UISelectItem<T> item) {
		JFXListBoxSelectItem<T> selectedItem = this.findItem(item);
		if( selectedItem != null ) {
			this.getControl().getItems().remove(selectedItem);
		}
	}
	
	public void removeItems() {
		this.getControl().getItems().clear();
	}
	
	public int getItemCount() {
		return this.getControl().getItems().size();
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
}