package org.herac.tuxguitar.ui.widget;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.ui.resource.UIImage;

public class UITableItem<T> {
	
	private UIImage image;
	private List<String> texts;
	private T value;
	
	public UITableItem(T value) {
		this.value = value;
		this.texts = new ArrayList<String>();
	}

	public UITableItem() {
		this(null);
	}
	
	public String getText(int column) {
		return (column >= 0 && column < this.texts.size() ? this.texts.get(column) : null);
	}

	public void setText(int column, String text) {
		if( column >= 0 ) {
			while( this.texts.size() < column ) {
				this.texts.add(new String());
			}
			this.texts.add(text);
		}
	}

	public UIImage getImage() {
		return image;
	}

	public void setImage(UIImage image) {
		this.image = image;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof UITableItem ) {
			return (this.hashCode() == obj.hashCode());
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return (UITableItem.class.getName() + "-" + (this.getValue() != null ? this.getValue().hashCode() : "null")).hashCode();
	}
}
