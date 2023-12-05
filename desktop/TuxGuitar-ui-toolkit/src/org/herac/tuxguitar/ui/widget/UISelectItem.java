package org.herac.tuxguitar.ui.widget;

public class UISelectItem<T> {
	
	private String text;
	private T value;
	
	public UISelectItem(String text, T value) {
		this.text = text;
		this.value = value;
	}

	public UISelectItem(String text) {
		this(text, null);
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof UISelectItem ) {
			return (this.hashCode() == obj.hashCode());
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return (UISelectItem.class.getName() + "-" + (this.getValue() != null ? this.getValue().hashCode() : "null")).hashCode();
	}
}
