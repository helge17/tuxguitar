package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.control.ComboBox;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.jfx.event.JFXSelectionListenerChangeManager;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UISelectItem;

public class JFXDropDownSelect<T> extends JFXControl<ComboBox<JFXDropDownSelectItem<T>>> implements UIDropDownSelect<T> {
	
	private JFXSelectionListenerChangeManager<JFXDropDownSelectItem<T>> selectionListener;
	
	public JFXDropDownSelect(JFXContainer<? extends Region> parent) {
		super(new ComboBox<JFXDropDownSelectItem<T>>(), parent);
		
		this.selectionListener = new JFXSelectionListenerChangeManager<JFXDropDownSelectItem<T>>(this);
	}

	public JFXDropDownSelectItem<T> findItem(UISelectItem<T> item) {
		for(JFXDropDownSelectItem<T> selectItem : this.getControl().getItems()) {
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
		JFXDropDownSelectItem<T> selectedItem = this.getControl().getSelectionModel().getSelectedItem();
		return (selectedItem != null ? selectedItem.getItem() : null);
	}

	public void setSelectedItem(UISelectItem<T> item) {
		JFXDropDownSelectItem<T> selectedItem = this.findItem(item);
		this.getControl().getSelectionModel().select(selectedItem);
	}

	public void addItem(UISelectItem<T> item) {
		this.getControl().getItems().add(new JFXDropDownSelectItem<T>(item));
	}
	
	public void removeItem(UISelectItem<T> item) {
		JFXDropDownSelectItem<T> selectedItem = this.findItem(item);
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