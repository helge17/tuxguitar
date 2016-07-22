package org.herac.tuxguitar.ui.qt.widget;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.qt.event.QTSelectionListenerManager;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIListBoxSelect;
import org.herac.tuxguitar.ui.widget.UISelectItem;

import com.trolltech.qt.gui.QContentsMargins;
import com.trolltech.qt.gui.QListWidget;
import com.trolltech.qt.gui.QListWidgetItem;
import com.trolltech.qt.gui.QScrollBar;

public class QTListBoxSelect<T> extends QTWidget<QListWidget> implements UIListBoxSelect<T> {
	
	private List<UISelectItem<T>> items;
	private QTSelectionListenerManager selectionListener;
	
	public QTListBoxSelect(QTContainer parent) {
		super(new QListWidget(parent.getContainerControl()), parent);
		
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
		int index = this.getControl().currentRow();
		return (index >= 0 && index < this.items.size() ? this.items.get(index) : null);
	}

	public void setSelectedItem(UISelectItem<T> item) {
		int index = (item != null ? this.items.indexOf(item) : -1);
		this.getControl().setCurrentRow(index);
	}

	public void addItem(UISelectItem<T> item) {
		this.items.add(item);
		this.getControl().addItem(item.getText());
	}
	
	public void removeItem(UISelectItem<T> item) {
		int index = (item != null ? this.items.indexOf(item) : -1);
		if( index >= 0 && index < this.items.size() ) {
			this.items.remove(item);
			QListWidgetItem widgetItem = this.getControl().item(index);
			if( widgetItem != null ) {
				widgetItem.dispose();
			}
		}
	}
	
	public void removeItems() {
		this.items.clear();
		this.getControl().clear();
	}
	
	public int getItemCount() {
		return this.items.size();
	}
	
	@Override
	public void computePackedSize() {
		QContentsMargins margins = this.getControl().getContentsMargins();
		
		float width = (margins.left + margins.right);
		float height = (margins.top + margins.bottom);
		
		if(!this.items.isEmpty() ) {
			width += (this.getControl().sizeHintForColumn(0));
			for(int i = 0; i < this.items.size(); i ++) {
				height += this.getControl().sizeHintForRow(i);
			}
		}
		
		QScrollBar vScroll = this.getControl().verticalScrollBar();
		if( vScroll != null && vScroll.isEnabled()) {
			width += vScroll.sizeHint().width();
		}
		this.setPackedSize(new UISize(width, height));
	}
	
	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().currentRowChanged.connect(this.selectionListener, QTSelectionListenerManager.SIGNAL_METHOD);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().currentRowChanged.disconnect();
		}
	}
}