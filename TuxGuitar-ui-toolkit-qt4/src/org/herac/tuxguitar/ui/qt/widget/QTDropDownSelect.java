package org.herac.tuxguitar.ui.qt.widget;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.qt.event.QTSelectionListenerManager;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UISelectItem;

import com.trolltech.qt.gui.QComboBox;

public class QTDropDownSelect<T> extends QTWidget<QComboBox> implements UIDropDownSelect<T> {
	
	private List<UISelectItem<T>> items;
	private QTSelectionListenerManager selectionListener;
	
	public QTDropDownSelect(QTContainer parent) {
		super(new QComboBox(parent.getContainerControl()), parent);
		
		this.selectionListener = new QTSelectionListenerManager(this);
		this.items = new ArrayList<UISelectItem<T>>();
	}

	public T getSelectedValue() {
		UISelectItem<T> selectedItem = this.getSelectedItem();
		return (selectedItem != null ? selectedItem.getValue() : null);
	}
	
	public void setSelectedValue(T value) {
		this.setSelectedItem(new UISelectItem<T>(null, value));
	}
	
	public UISelectItem<T> getSelectedItem() {
		int index = this.getControl().currentIndex();
		return (index >= 0 && index < this.items.size() ? this.items.get(index) : null);
	}

	public void setSelectedItem(UISelectItem<T> item) {
		int index = (item != null ? this.items.indexOf(item) : -1);
		this.getControl().setCurrentIndex(index);
	}

	public void addItem(UISelectItem<T> item) {
		this.items.add(item);
		this.getControl().addItem(item.getText());
	}
	
	public void removeItem(UISelectItem<T> item) {
		int index = (item != null ? this.items.indexOf(item) : -1);
		if( index >= 0 && index < this.items.size() ) {
			this.getControl().removeItem(index);
			this.items.remove(item);
		}
	}
	
	public void removeItems() {
		this.items.clear();
		this.getControl().clear();
	}
	
	public int getItemCount() {
		return this.items.size();
	}
	
	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().currentIndexChanged.connect(this.selectionListener, QTSelectionListenerManager.SIGNAL_METHOD);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().currentIndexChanged.disconnect();
		}
	}
}