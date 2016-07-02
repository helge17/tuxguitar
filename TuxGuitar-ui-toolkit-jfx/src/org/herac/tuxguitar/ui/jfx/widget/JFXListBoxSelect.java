package org.herac.tuxguitar.ui.jfx.widget;

import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.jfx.event.JFXSelectionListenerChangeManager;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIListBoxSelect;
import org.herac.tuxguitar.ui.widget.UISelectItem;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

public class JFXListBoxSelect<T> extends JFXControl<ListView<JFXListBoxSelectItem<T>>> implements UIListBoxSelect<T> {
	
	private static final float DEFAULT_CELL_LEFT = 8f;
	private static final float DEFAULT_CELL_RIGHT = 8f;
	private static final float DEFAULT_CELL_TOP = 3f;
	private static final float DEFAULT_CELL_BOTTOM = 4f;
	
	private Insets cellPadding;
	private FontMetrics cellFontMetrics;
	private JFXSelectionListenerChangeManager<JFXListBoxSelectItem<T>> selectionListener;
	
	public JFXListBoxSelect(JFXContainer<? extends Region> parent) {
		super(new ListView<JFXListBoxSelectItem<T>>(), parent);
		
		this.selectionListener = new JFXSelectionListenerChangeManager<JFXListBoxSelectItem<T>>(this);
		this.getControl().setCellFactory(new JFXListBoxSelectCellFactory<T>(this));
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
	
	public void computePackedSize() {
		UISize packedContentSize = new UISize();
		for(JFXListBoxSelectItem<T> selectItem : this.getControl().getItems()) {
			packedContentSize.setWidth(Math.max(packedContentSize.getWidth(), this.computeCellWidth(selectItem)));
			packedContentSize.setHeight(packedContentSize.getHeight() + this.computeCellHeight(selectItem));
		}
		
		Insets padding = this.getControl().getPadding();
		
		UISize packedSize = new UISize();
		packedSize.setWidth(packedContentSize.getWidth() + (float)(padding.getLeft() + padding.getRight()));
		packedSize.setHeight(packedContentSize.getHeight() + (float)(padding.getTop() + padding.getBottom()));
		
		this.setPackedSize(packedSize);
	}
	
	public float computeCellWidth(JFXListBoxSelectItem<T> item) {
		Insets insets = this.computeCellPadding();
		FontMetrics fontMetrics = this.computeCellFontMetrics();
		
		return (float)(fontMetrics.computeStringWidth(item.toString()) + insets.getLeft() + insets.getRight());
	}
	
	public float computeCellHeight(JFXListBoxSelectItem<T> item) {
		Insets insets = this.computeCellPadding();
		FontMetrics fontMetrics = this.computeCellFontMetrics();
		
		return (float)(fontMetrics.getLineHeight() + insets.getTop() + insets.getBottom() + 2);
	}
	
	public Insets computeCellPadding() {
		if( this.cellPadding == null ) {
			this.cellPadding = new Insets(DEFAULT_CELL_TOP, DEFAULT_CELL_RIGHT, DEFAULT_CELL_BOTTOM, DEFAULT_CELL_LEFT);
		}
		return this.cellPadding;
	}
	
	public FontMetrics computeCellFontMetrics() {
		if( this.cellFontMetrics == null ) {
			this.cellFontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(Font.getDefault());
		}
		return this.cellFontMetrics;
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